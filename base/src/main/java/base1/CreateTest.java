package base1;

import common.BaseStringCallback;
import common.ZkConnect;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;


/**
 * 描述：创建
 * 作者：liangyongtong
 * 日期：2019/10/15 3:41 PM
 * 类名：CreateTest
 * 版本： version 1.0
 */
public class CreateTest {

//    static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        // 获取连接
        ZooKeeper zk = ZkConnect.instance();
        Thread.sleep(2000);

        // 创建一个节点
        normalCreate(zk);

        // 创建带自定义状态的节点
        statCreate(zk, "/zk-java-stat-01");

        // 异步创建节点
        asyncCreate(zk);

//        latch.await();
    }

    private static void normalCreate(ZooKeeper zk) throws KeeperException, InterruptedException {
        zk.create(
                "/zk-java-01", // 节点路径
                "你们好".getBytes(), // 节点内容

                /**
                 * 节点权限 ZooDefs.Ids.OPEN_ACL_UNSAFE -> 是 world anyone 所有的权限
                 * 可以自已定义一个权限列表: ArrayList<ACL>
                 * new ACL("per", "ID") 构成一个权限对象 可以设置多个
                 * per 表示操作权限： READ = 1; WRITE = 2; CREATE = 4; DELETE = 8; ADMIN = 16; ALL = 31;
                 * ID 表示授权对象 new ID("scheme", "id") 如 new ID("world", "anyone")
                 */
                ZooDefs.Ids.OPEN_ACL_UNSAFE,

                /**
                 * 节点类型: 使用 zkCli 客户端创建节点一般是只有前四种
                 * PERSISTENT 持久型
                 * PERSISTENT_SEQUENTIAL 持久有序型
                 * EPHEMERAL 临时型
                 * EPHEMERAL_SEQUENTIAL 临时有序型
                 * CONTAINER 容器节点，用于Leader、Lock等特殊用途，当容器节点不存在任何子节点时，容器将成为服务器在将来某个时候删除的候选节点
                 * PERSISTENT_WITH_TTL 有TTL[存活时间]的永久节点，节点在TTL时间之内没有得到更新并且无子节点，就会被自动删除 需要配合另外一个参数一起
                 * PERSISTENT_SEQUENTIAL_WITH_TTL 有TTL[存活时间]和有序的永久节点，节点在TTL时间之内没有得到更新并且无子节点，就会被自动删除 需要配合另外一个参数一起
                 */
                CreateMode.PERSISTENT // 永久节点
        );
    }

    public static void statCreate(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
        // 用于保存节点创建完成后的状态信息
        Stat stat = new Stat();
        String p = zk.create(
                path,
                "带自定义状态".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                /**
                 * 节点状态:
                 * czxid;
                 * mzxid;
                 * ctime;
                 * mtime;
                 * version;
                 * cversion;
                 * aversion;
                 * ephemeralOwner;
                 * dataLength;
                 * numChildren;
                 * pzxid;
                 */
                stat
        );

        System.out.println("path ->" + p);
        System.out.println("stat -> " + stat);
    }

    private static void asyncCreate(ZooKeeper zk) throws InterruptedException {
        zk.create(
                "/zk-java-async-01",
                "带自定义状态".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                new BaseStringCallback(), // 节点创建完成的回调
                "我是异步创建节点"
        );
    }
}
