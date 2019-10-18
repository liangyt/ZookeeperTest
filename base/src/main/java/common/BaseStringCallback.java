package common;

import org.apache.zookeeper.AsyncCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：回调
 * 作者：lyt
 * 日期：2019/10/16 4:32 PM
 * 类名：BaseStringCallback
 * 版本： version 1.0
 */
public class BaseStringCallback implements AsyncCallback.StringCallback {

    private CountDownLatch latch;

    public BaseStringCallback() {}

    public BaseStringCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * 创建节点回调
     * @param resultCode 返回码 0:成功 -110 节点存在 -112 会话过期 -4 连接断开
     * @param path 创建节点传入的路径
     * @param ctx 创建节点传入的数据
     * @param name 服务端创建节点后的真正路径 主要是针对顺序节点
     */
    @Override
    public void processResult(int resultCode, String path, Object ctx, String name) {
        System.out.println("resultCode -> " + resultCode);
        System.out.println("path -> " +path);
        System.out.println("ctx -> " + (Objects.nonNull(ctx) ? ctx.toString() : null));
        System.out.println("name -> " + name);

        if (Objects.nonNull(this.latch)) {
            latch.countDown();
        }
    }
}
