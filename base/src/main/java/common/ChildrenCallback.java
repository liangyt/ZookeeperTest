package common;

import org.apache.zookeeper.AsyncCallback;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：子节点列表异步获取
 * 作者：liangyongtong
 * 日期：2019/10/17 3:44 PM
 * 类名：ChildrenCallback
 * 版本： version 1.0
 */
public class ChildrenCallback implements AsyncCallback.ChildrenCallback {
    private CountDownLatch latch;

    public ChildrenCallback() {
    }

    public ChildrenCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void processResult(int resultCode, String path, Object ctx, List<String> list) {
        System.out.println("子节点列表异步获取");

        System.out.println("resultCode -> " + resultCode);
        System.out.println("path -> " +path);
        System.out.println("ctx -> " + ctx);
        System.out.println("list -> " + list);

        if (Objects.nonNull(latch)) {
            latch.countDown();
        }
    }
}
