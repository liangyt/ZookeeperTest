package common;

import org.apache.zookeeper.AsyncCallback;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 描述：删除节点回调
 * 作者：lyt
 * 日期：2019/10/16 6:59 PM
 * 类名：BaseVoidCallback
 * 版本： version 1.0
 */
public class BaseVoidCallback implements AsyncCallback.VoidCallback {

    private CountDownLatch latch;

    public BaseVoidCallback() {
    }

    public BaseVoidCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void processResult(int resultCode, String path, Object ctx) {
        System.out.println("resultCode -> " + resultCode);
        System.out.println("path -> " + path);
        System.out.println("ctx ->" + ctx);

        if (Objects.nonNull(latch)) {
            latch.countDown();
        }
    }
}
