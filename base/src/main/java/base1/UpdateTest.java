package base1;

import common.StatCallback;
import common.ZkConnect;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：更新数据
 * 作者：liangyongtong
 * 日期：2019/10/17 1:53 PM
 * 类名：UpdateTest
 * 版本： version 1.0
 */
public class UpdateTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = ZkConnect.instance(latch);
        latch.await();

        Stat gStat = new Stat();
        String oldData = GetTest.getData(zk, gStat);

        System.out.println("oldData -> " + oldData);
        System.out.println("oldVersion -> " + gStat.getVersion());

        Stat stat = zk.setData(
                "/zk-java-stat-01", // 节点路径
                "我是新数据".getBytes(), // 新数据
                gStat.getVersion() // 如果版本号跟服务器上保存的不一样， 则此时出现异常 org.apache.zookeeper.KeeperException$BadVersionException: KeeperErrorCode = BadVersion for /zk-java-stat-01
                );
        // 此时版本号已更新
        System.out.println("newVersion -> " + stat.getVersion());

        CountDownLatch latch1 = new CountDownLatch(1);
        zk.setData(
                "/zk-java-stat-01", // 节点路径
                "我是异步更新新数据".getBytes(), // 新数据
                stat.getVersion(),
                new StatCallback(latch1),
                "异步更新数据"
        );

        latch1.await();
    }
}
