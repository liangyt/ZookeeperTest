package common;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 描述：自定义的监听器
 * 作者：liangyongtong
 * 日期：2019/10/17 1:32 PM
 * 类名：CustomWatcher
 * 版本： version 1.0
 */
public class CustomWatcher implements Watcher {

    @Override
    public void process(WatchedEvent event) {
        System.out.println("我不是默认监听器");

        System.out.println("path -> " + event.getPath());
        System.out.println("state -> " + event.getState());
        System.out.println("type ->" + event.getType());
        
    }
}
