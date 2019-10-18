package common;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：异步更新数据
 * 作者：liangyongtong
 * 日期：2019/10/17 2:57 PM
 * 类名：StatCallback
 * 版本： version 1.0
 */
public class StatCallback implements AsyncCallback.StatCallback {

    CountDownLatch latch;

    public StatCallback() {
    }

    public StatCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * 异步更新数据回调方法
     * @param resultCode 返回状态码
     * @param path 更新的节点路径
     * @param ctx 异步更新数据设置的值
     * @param stat 更新状态
     */
    @Override
    public void processResult(int resultCode, String path, Object ctx, Stat stat) {
        System.out.println("我是异步数据状态");

        System.out.println("resultCode -> " + resultCode);
        System.out.println("path -> " + path);
        System.out.println("ctx -> " + ctx);
        System.out.println("stat -> " + stat);

        if (Objects.nonNull(latch)) {
            latch.countDown();
        }
    }
}
