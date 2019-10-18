package base1;

import common.CustomWatcher;
import common.DataCallback;
import common.DefaultWatcher;
import common.ZkConnect;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;


/**
 * 描述：获取
 * 作者：liangyongotng
 * 日期：2019/10/16 7:26 PM
 * 类名：GetTest
 * 版本： version 1.0
 */
public class GetTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = ZkConnect.instance(latch);

        latch.await();


        // 获取数据
        Stat stat = new Stat();
        getData(zk, stat);

        // 获取数据 不使用默认监听器
//        getDataNewWatcher(zk);

        // 异步获取数据
//        asyncGetData(zk);


        Thread.sleep(1000 * 60);
    }

    public static String getData(ZooKeeper zk, Stat stat) throws KeeperException, InterruptedException {

        byte[] data = zk.getData(
                "/zk-java-stat-01", // 节点路径
                true,  // 是否使用默认监听器
                stat // 用于存放服务器返回的 stat
        );

        System.out.println("data -> " + new String(data));

        System.out.println("aversion -> " + stat.getAversion());
        System.out.println("ctime -> " + stat.getCtime());
        System.out.println("cversion -> " + stat.getCversion());
        System.out.println("dataLength -> " + stat.getDataLength());
        System.out.println("version -> " + stat.getVersion());

        return new String(data);
    }

    public static String getDataNewWatcher(ZooKeeper zk) throws KeeperException, InterruptedException {

        Stat stat = new Stat();
        byte[] data = zk.getData(
                "/zk-java-stat-01",
                new CustomWatcher(), // 注册节点内容变更监听
                stat
        );

        System.out.println("data -> " + new String(data));

        return new String(data);
    }

    private static void asyncGetData(ZooKeeper zk) {
        zk.getData(
                "/zk-java-stat-01",
                false, // 不使用监听器
                new DataCallback(), // 异步获取数据的回调
                "异步获取数据"
        );

        zk.getData(
                "/zk-java-stat-01",
                new CustomWatcher(), // 使用自定义数据变更监听器 节点内容变更监听
                new DataCallback(), // 异步获取数据的回调
                "异步获取监听数据"
        );
    }
}
