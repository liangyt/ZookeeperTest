package common;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.proto.WatcherEvent;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：默认的监听
 * 作者：liangyongtong
 * 日期：2019/10/15 4:11 PM
 * 类名：DefaultWatcher
 * 版本： version 1.0
 */
public class DefaultWatcher implements Watcher {

    private CountDownLatch latch;

    public DefaultWatcher() {}

    public DefaultWatcher(CountDownLatch latch) {
        this.latch = latch;
    }

    public void process(WatchedEvent event) {
        System.out.println("我是默认监听器");

        /**

         这是会话的状态值

         @Deprecated
         Unknown(-1), // 废弃
         Disconnected(0), // 连接断开
         @Deprecated
         NoSyncConnected(1), // 废弃
         SyncConnected(3), // 已连接
         AuthFailed(4), // 连接 scheme 错误；
         ConnectedReadOnly(5), // 只读连接
         SaslAuthenticated(6),
         Expired(-112), // 客户端会话失效
         Closed(7); // 连接关闭

         可以看到废弃了两个将要不使用了
         */
        Event.KeeperState state = event.getState();

        System.out.println("state -> " + state);

        /**

         这是事件类型

         None(-1), // 无事件
         NodeCreated(1), // 节点创建事件
         NodeDeleted(2), // 节点删除事件
         NodeDataChanged(3), // 节点数据更新事件
         NodeChildrenChanged(4), // 节点子节点列表变更事件
         DataWatchRemoved(5), // 数据更新监听移除事件
         ChildWatchRemoved(6); // 子节点列表变更监听移除事件
         */

        Event.EventType eventType = event.getType();

        System.out.println("eventType -> " + eventType);

        // 节点路径
        String path = event.getPath();

        System.out.println("path -> " + path);

        // 事件对象的原始封装来源
        WatcherEvent watcherEvent = event.getWrapper();

        if (Objects.nonNull(latch)) {
            latch.countDown();
        }
    }
}
