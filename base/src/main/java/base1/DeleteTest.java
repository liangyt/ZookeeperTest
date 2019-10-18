package base1;

import common.BaseVoidCallback;
import common.ZkConnect;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;


/**
 * 描述：删除节点
 * 作者：lyt
 * 日期：2019/10/16 5:39 PM
 * 类名：DeleteTest
 * 版本： version 1.0
 */
public class DeleteTest {
    public static void main(String[] args) throws Exception {
        ZooKeeper zk = ZkConnect.instance();

        Thread.sleep(2000);

        // 删除节点
        baseDelete(zk);

        // 异步删除节点
        asyncDelete(zk);

        Thread.sleep(2000);
    }

    private static void baseDelete(ZooKeeper zk) throws KeeperException, InterruptedException {
        zk.delete(
                "/zk-java-01", // 需要删除的节点全路径
                0 // 删除节点的版本号 如果版本号对不上的话则删除失败
        );
    }

    private static void asyncDelete(ZooKeeper zk) {
        zk.delete(
                "/zk-java-01",
                0,
                new BaseVoidCallback(), // 删除回调
                "删除基本节点"
        );
    }
}
