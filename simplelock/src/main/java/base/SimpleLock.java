package base;

import common.ZkConnect;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * 描述：这是实现一个简单的排它锁
 * 作者：liangyongtong
 * 日期：2019/10/21 4:29 PM
 * 类名：SimpleLock
 * 版本： version 1.0
 */
public class SimpleLock implements Lock {

    // 持久化锁节点
    private final String ROOT_NODE = "/zk-root-lock";
    // 需要加锁的节点 获取得到的是全路径
    private String lockNode;

    // 服务器连接对象
    ZooKeeper zk;

    public SimpleLock() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        zk = ZkConnect.instance(latch);
        latch.await();
    }

    /**
     * 开始一段锁代码
     * @throws Exception
     */
    @Override
    public void lock() throws Exception {
        try {
            // 创建锁节点
            lockNode = zk.create(ROOT_NODE + "/lock", "LOCK".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            // 如果持久化节点不存在则创建一下
            if (e instanceof KeeperException.NoNodeException) {
                // 多线程并发的时候再次创建的时候可能节点已经存在了
                try {
                    zk.create(ROOT_NODE, "LOCK".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } catch (KeeperException e1) {
                    if (e1 instanceof KeeperException.NodeExistsException) {
                        System.out.println("根节点已存在");
                    }
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                // 重新调起锁
                lock();
            }
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        waitLock();
    }

    /**
     * 等待锁的获取
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void waitLock() throws KeeperException, InterruptedException {

        // 获取子节点列表
        List<String> nodes = zk.getChildren(ROOT_NODE, false);
        // 从小到大排序 [lock00000001,lock00000002......]
        List<String> sortNodes = nodes.stream().sorted((node1, node2) -> node1.compareTo(node2)).collect(Collectors.toList());

        // 判断最小一个节点是否自已的，如果是自已的则为获取锁
        if (lockNode.equals(ROOT_NODE.concat("/").concat(sortNodes.get(0)))) {
            // 获取锁 返回
            return;
        }
        // 不是最小节点则需要等待获取锁
        else {
            // 1. 取得比自已小的最近一个节点
            int index = 0;
            for (int i = 0; i < sortNodes.size(); i++) {
                if (lockNode.indexOf(sortNodes.get(i)) > 0) {
                    index = i;
                    break;
                }
            }
            // 需要被监听的节点
            String watcherNode = ROOT_NODE + "/" + sortNodes.get(index - 1);

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
            waitLock();
        }
    }

    @Override
    public void unLock() throws Exception {
        // 默认数据版本为 0
        zk.delete(lockNode, 0);
    }
}
