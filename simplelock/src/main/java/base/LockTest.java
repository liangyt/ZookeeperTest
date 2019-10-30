package base;

/**
 * 描述：测试锁
 * 作者：liangyongtong
 * 日期：2019/10/30 11:22 AM
 * 类名：LockTest
 * 版本： version 1.0
 */
public class LockTest {
    // 验证锁 起三个线程测试一下
    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                testLock(10);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testLock(20);
            }
        }).start();

//        Thread.sleep(1000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                testLock(11);
            }
        }).start();
    }

    private static void testLock(long sleepTime) {
        try {
            SimpleLock lock = new SimpleLock();
            System.out.println("i try to lock ->" + sleepTime);
            lock.lock();
            System.out.println("i get lock ->" + sleepTime);
            Thread.sleep(sleepTime);
            lock.unLock();
            System.out.println("i unlock ->" + sleepTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
