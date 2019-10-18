package common;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：获取数据异步
 * 作者：liangyongtong
 * 日期：2019/10/16 9:43 PM
 * 类名：DataCallback
 * 版本： version 1.0
 */
public class DataCallback implements AsyncCallback.DataCallback {

    private CountDownLatch latch;

    public DataCallback() {}

    public DataCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * 异步获取数据
     * @param resultCode 返回状态
     * @param path 节点路径
     * @param ctx 异步获取数据设置的值
     * @param bytes 异步获取的数据
     * @param stat 节点状态
     */
    @Override
    public void processResult(int resultCode, String path, Object ctx, byte[] bytes, Stat stat) {
        System.out.println("我是获取数据异步回调");

        System.out.println("resultCode -> " + resultCode);
        System.out.println("path -> " + path);
        System.out.println("ctx -> " + ctx);
        System.out.println("data -> " + new String(bytes));
        System.out.println("stat -> " + stat);

        if (Objects.nonNull(latch)) {
            latch.countDown();
        }
    }
}
