package base1;

import common.DigestUtil;
import common.ZkConnect;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：节点权限  zookeeper:qW/HnTfCSoQpB5G8LgkwT3IbiFc= 超级用户的账号 zookeeper:admin
 * 作者：liangyongtong
 * 日期：2019/10/17 4:26 PM
 * 类名：AclTest
 * 版本： version 1.0
 */
public class AclTest {
    public static void main(String[] args) throws Exception {
        String node = "/zk-java-acl";

        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = ZkConnect.instance(latch);
        latch.await();

        // 设置超级权限
        /**
         * 设置超级账户连接， super:id 这是 digest:id 的一种特殊方式
         * 启用超级账户需要启动zookeeper服务的时候配置对应的参数(这种提供两种方式)：
         * 1. 启动的时候直接添加 -Dzookeeper.DigestAuthenticationProvider.superDigest=zookeeper:qW/HnTfCSoQpB5G8LgkwT3IbiFc=
         * 2. 在配置文件 zoo.cfg 里面配置 DigestAuthenticationProvider.superDigest=zookeeper:qW/HnTfCSoQpB5G8LgkwT3IbiFc=
         * 这两种方式都可以配置超级账户，账户可以自定义，结果： BASE64(SHA-1)
         * 可以命令行生成也可以java代码运行生成:
         * java:  DigestAuthenticationProvider.generateDigest(name:password)
         *
         */
        zk.addAuthInfo("digest", "zookeeper:admin".getBytes());

        // 先创建一个永久节点, 权限为 ZooDefs.Ids.OPEN_ACL_UNSAFE
        // 如果报节点已存在异常 则把这一行注释掉
        // org.apache.zookeeper.KeeperException$NodeExistsException: KeeperErrorCode = NodeExists for /zk-java-acl
        CreateTest.statCreate(zk, node);

        // 获取节点的权限
        Stat stat = new Stat();
        List<ACL> acls = zk.getACL(
                node, // 需要获取权限信息的节点
                stat // 回设节点状态
        );
//
//        //  [31,s{'world,'anyone}] 这是默认创建的节点权限
        System.out.println("acls ->" + acls);
        System.out.println("aclVersion -> " + stat.getAversion());

        // 异步获取节点权限
        zk.getACL(node, stat, new AsyncCallback.ACLCallback() {
            @Override
            public void processResult(int resultCode, String path, Object ctx, List<ACL> list, Stat stat) {
                System.out.println("async acls ->" + list);
            }
        }, "异步获取权限");

//
        String auth = "name:password";
        // 对节点设置权限
        acl(node, zk, stat, auth);


        // 另起一个无权限会话 读取该节点的数据看看
//        noAuthGetData(node);

        // 另起一个会话并设置权限 读取该节点数据
        authGetData(node, auth);

        // 删除节点
        zk.getData(node, null, stat);
        zk.delete(node, stat.getVersion());
    }

    // 给节点设置权限
    private static void acl(String node, ZooKeeper zk, Stat stat, String auth) throws NoSuchAlgorithmException, KeeperException, InterruptedException {
        // 创建授权对象
        Id id = new Id("digest", DigestUtil.digest(auth));
        // 可以定义一个 ip 模式的授权对象
//        Id ipId = new Id("ip", "192.168.3.17");

        // 定义操作权限
        ACL aclRead = new ACL(ZooDefs.Perms.READ, id); // 读取权限
        ACL aclCrd = new ACL(ZooDefs.Perms.CREATE, id); // 创建权限
        ACL aclDel = new ACL(ZooDefs.Perms.DELETE, id); // 删除权限
        ACL aclUpd = new ACL(ZooDefs.Perms.WRITE, id); // 更新权限
        ACL aclAdm = new ACL(ZooDefs.Perms.ADMIN, id); // 权限管理权限

        // 对节点 /zk-java-acl 设置权限
        List<ACL> acls = new ArrayList<>();
        acls.add(aclRead);
        acls.add(aclUpd);

        // 返回状态 权限版本号已改变了
        Stat aclStat = zk.setACL(node, acls, stat.getAversion());
        System.out.println("aclStatVersion -> " + aclStat.getAversion());
    }

    private static void authGetData(String node, String auth) throws IOException, InterruptedException, KeeperException {
        CountDownLatch latch1 = new CountDownLatch(1);
        ZooKeeper authZk = ZkConnect.instance(latch1);
        latch1.await();

        // 设置会话权限
        authZk.addAuthInfo("digest", auth.getBytes());

        Stat stat = new Stat();

        byte[] dataAuth = authZk.getData(node, false, stat);
        System.out.println("dataAuth -> " + new String(dataAuth));

        // 没有更新权限，看看能否更新
        authZk.setData(node, "试试能否更新成功".getBytes(), stat.getVersion());
    }

    // 测试没有读取权限的会话去读取数据是什么样的
    private static void noAuthGetData(String node) throws IOException, InterruptedException, KeeperException {
        CountDownLatch latch1 = new CountDownLatch(1);
        ZooKeeper noAuthZk = ZkConnect.instance(latch1);
        latch1.await();

        // 出现异常
        // org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth for /zk-java-acl
        byte[] dataNoAuth = noAuthZk.getData(node, false, null);
        System.out.println("dataNoAuth -> " + new String(dataNoAuth));
    }
}
