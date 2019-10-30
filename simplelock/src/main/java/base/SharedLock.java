package base;

import common.ZkConnect;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * 描述：这是实现一个简单的共享锁 <br/>
 * 读锁：只要本临时有序节点前面的节点都是读节点或自已是最小节点，即可认为获取了锁 <br/>
 * 写锁：跟排他锁一样，只有本节点为最小节点的时候才会认为获取了锁 <br/>
 * 本程序存在问题:
 * 不支持锁重入和锁升级(由读锁升级为写锁) <br/>
 * 作者：liangyongtong
 * 日期：2019/10/30 4:29 PM
 * 类名：SimpleLock
 * 版本： version 1.0
 */
public class SharedLock implements ReadWriteLock {

    // 持久化锁节点
    private final String ROOT_NODE = "/zk-root-readwritelock";
    // 需要加锁的节点 获取得到的是全路径
    private String lockNode;

    // 服务器连接对象
    ZooKeeper zk;

    public SharedLock() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        zk = ZkConnect.instance(latch);
        latch.await();
    }

    @Override
    public void readLock() throws Exception {
        try {
            // 创建锁节点
            lockNode = zk.create(ROOT_NODE + "/lock-r", "R".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            // 如果持久化节点不存在则创建一下
            if (e instanceof KeeperException.NoNodeException) {
                // 多线程并发的时候再次创建的时候可能节点已经存在了
                createRootNode();
                // 重新调起锁
                readLock();
            }
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        waitLock(true);
    }


    @Override
    public void writeLock() throws Exception {
        try {
            // 创建锁节点
            lockNode = zk.create(ROOT_NODE + "/lock-w", "W".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            // 如果持久化节点不存在则创建一下
            if (e instanceof KeeperException.NoNodeException) {
                // 多线程并发的时候再次创建的时候可能节点已经存在了
                createRootNode();
                // 重新调起锁
                writeLock();
            }
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        waitLock(false);
    }

    // 创建根节点
    private void createRootNode() {
        try {
            zk.create(ROOT_NODE, "LOCK".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            if (e instanceof KeeperException.NodeExistsException) {
                System.out.println("根节点已存在");
            }
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待锁的获取
     * @Param isRead 是否读锁
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void waitLock(boolean isRead) throws KeeperException, InterruptedException {

        // 获取子节点列表
        List<String> nodes = zk.getChildren(ROOT_NODE, false);
        // 从小到大排序 [lock-r00000001,lock-w00000002......]
        List<String> sortNodes = nodes.stream().sorted((node1, node2) -> node1.substring(5).compareTo(node2.substring(5))).collect(Collectors.toList());

        // 不管是写锁还是读锁，判断最小一个节点是否自已的，如果是自已的则为获取锁
        if (lockNode.equals(ROOT_NODE.concat("/").concat(sortNodes.get(0)))) {
            // 获取锁 返回
            return;
        }
        // 如果是读锁，则判断比自已小的节点是不是都是读锁，如果都是读锁，则为获取锁
        else if (isRead && checkRead(sortNodes)) {
            return;
        }
        // 如果既不能获取读锁也不能获取写锁，需要进行监听操作
        else {
            // 当前节点位置
            int index = getCurrentIndex(sortNodes);

            // 需要被监听的节点
            // 如果是读锁获取离自已最近的一个写节点
            // 如果是写锁，取得离自已最近的一个节点
            String watcherNode = "";
            // 读锁
            if (isRead) {
                for (int i = index - 1; i > 0; i--) {
                    // 找到了离得最近的一个写节点，那么它的后一个节点要么是一个读节点，要么就是待加锁的节点本身
                    if (sortNodes.get(i).indexOf("lock-w") >= 0) {
                        watcherNode = ROOT_NODE + "/" + sortNodes.get(i);
                        break;
                    }
                }
            }
            // 写锁
            else {
                watcherNode = ROOT_NODE + "/" + sortNodes.get(index - 1);
            }

            // 利用同步锁工具进行线程等待
            CountDownLatch latch = new CountDownLatch(1);
            // 2. 监听最近一个节点的删除事件
            zk.exists(watcherNode, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    // 可以开始接着下一次检查了
                    latch.countDown();
                }
            });

            latch.await();
            // 再次尝试获取锁
            waitLock(isRead);
        }
    }

    // 判断比自已小的节点是否都是读节点
    private boolean checkRead(List<String> sortNodes) {
        // 当前节点位置
        int currentIndex = getCurrentIndex(sortNodes);

        for (int i = 0; i < currentIndex - 1; i++) {
            // 只要有一个写锁，则不能直接获取读锁
            if (sortNodes.get(i).indexOf("lock-w") >= 0) {
                return false;
            }
        }

        return true;
    }

    // 获取当前节点的位置
    private int getCurrentIndex(List<String> sortNodes) {
        for (int i = 0; i < sortNodes.size(); i++) {
            if (lockNode.indexOf(sortNodes.get(i)) > 0) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void unLock() throws Exception {
        // 默认数据版本为 0
        zk.delete(lockNode, 0);
    }
}
