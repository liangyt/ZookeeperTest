package base1;

import common.CustomWatcher;
import common.StatCallback;
import common.ZkConnect;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 描述：判断节点是否存
 * 作者：liangyongtong
 * 日期：2019/10/18 1:40 PM
 * 类名：ExistTest
 * 版本： version 1.0
 */
public class ExistTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = ZkConnect.instance(latch);
        latch.await();

        String exist = "/zk-exist";
        String noExist = "/zk-not-exist";

        CreateTest.statCreate(zk, exist);

        Stat stat = zk.exists(
                exist, // 节点路径
                false // 不使用监听
        );

        System.out.println("exist stat -> " + stat); // exist stat -> 291,291,1571377646937,1571377646937,0,0,0,0,18,0,291

        stat = zk.exists(
                noExist, // 节点路径
                true // 使用会话默认监听
        );

        System.out.println("not exist stat -> " + stat); // not exist stat -> null

        // 如果存在，则stat不为空 如果不存在则为空
        zk.exists(
                noExist,
                new CustomWatcher(), // 节点被创建 删除 更新的监听
                new StatCallback(),
                "异步检测节点是否存，并添加节点监听"
        );
    }
}
