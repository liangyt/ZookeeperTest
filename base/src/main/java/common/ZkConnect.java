package common;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：连接
 * zk 创建会话的过程是异步创建的，可以通过监听器了解是否连接成功
 * 作者：liangyongtong
 * 日期：2019/10/15 3:42 PM
 * 类名：ZkConnect
 * 版本： version 1.0
 */
public class ZkConnect {

    private ZkConnect() {}

    /**
     * 创建zk连接
     * @return
     * @throws IOException
     */
    public static ZooKeeper instance() throws IOException {

        ZooKeeper zk = new ZooKeeper(
                "localhost:2181", // 连接的服务地址
                5000,  // 会话超时时间, 在超时时间内会进行心跳检测；如果超过这个时间没有心跳检测，则服务端认为这个会话超时了
                new DefaultWatcher() // 默认的会话监听器, 如果设置为 null 则表示没有默认的监听器了
        );
        return zk;
    }

    /**
     * 创建zk连接
     * @return
     * @throws IOException
     */
    public static ZooKeeper instance(CountDownLatch latch) throws IOException {

        ZooKeeper zk = new ZooKeeper(
                "localhost:2181", // 连接的服务地址
                5000,  // 会话超时时间, 在超时时间内会进行心跳检测；如果超过这个时间没有心跳检测，则服务端认为这个会话超时了
                new DefaultWatcher(latch) // 默认的会话监听器, 如果设置为 null 则表示没有默认的监听器了
        );
        return zk;
    }

    /**
     * 创建zk连接
     * @param sessionId 会话id zk.getSessionId()
     * @param sessionpwd 会话密码 zk.getSessionPasswd()
     * @return
     * @throws IOException
     */
    public static ZooKeeper instance(long sessionId, byte[] sessionpwd) throws IOException {
        ZooKeeper zk = new ZooKeeper(
                "127.0.0.1:2181",
                5000,
                new DefaultWatcher(),
                sessionId,
                sessionpwd
                );

        return zk;
    }
}
