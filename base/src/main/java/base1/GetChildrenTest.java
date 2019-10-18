package base1;

import common.ChildrenCallback;
import common.CustomWatcher;
import common.ZkConnect;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：获取节点的子节点
 *      getChildren 方法使用方式比较多，这里简单列出来两个
 * 作者：liangyongtong
 * 日期：2019/10/17 3:23 PM
 * 类名：GetChildrenTest
 * 版本： version 1.0
 */
public class GetChildrenTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = ZkConnect.instance(latch);
        latch.await();

        // 直接获取节点的子节点
        List<String> children = getChildren(zk, "/zk-java-stat-01");

        System.out.println("children -> " + children);

        // 添加一个子节点
        // 重复添加子节点出现节点重复异常
        // org.apache.zookeeper.KeeperException$NodeExistsException: KeeperErrorCode = NodeExists for /zk-java-stat-01/child
        CreateTest.statCreate(zk, "/zk-java-stat-01/child01");

        // 再次获取子节点
        children = getChildren(zk, "/zk-java-stat-01");

        System.out.println("children -> " + children);

        // 异常获取子节点列表 并添加一个自定义子节点列表变更监听
        latch = new CountDownLatch(1);
        zk.getChildren(
                "/zk-java-stat-01",
                new CustomWatcher(),
                new ChildrenCallback(latch),
                "异步获取子节点列表"
        );

        latch.await();
    }

    private static List<String> getChildren(ZooKeeper zk, String pPath) throws KeeperException, InterruptedException {
        // 返回的子节点列表路径都是相对于父节点的路径，而不是全路径
        return zk.getChildren(
                    pPath, // 需要获取子节点列表的节点路径
                    false // 不添加监听
            );
    }
}
