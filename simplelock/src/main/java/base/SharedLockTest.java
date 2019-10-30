package base;

/**
 * 描述：测试锁
 * 作者：liangyongtong
 * 日期：2019/10/30 11:22 AM
 * 类名：LockTest
 * 版本： version 1.0
 */
public class SharedLockTest {
    // 验证锁 起三个线程测试一下
    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                testWriteLock(8);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testReadLock(10);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testReadLock(20);
            }
        }).start();

//        Thread.sleep(1000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                testWriteLock(11);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testWriteLock(14);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testReadLock(9);
            }
        }).start();
    }

    // 读锁
    private static void testReadLock(long sleepTime) {
        try {
            SharedLock lock = new SharedLock();
            System.out.println("i try to readlock ->" + sleepTime);
            lock.readLock();
            System.out.println("i get readlock ->" + sleepTime);
            Thread.sleep(sleepTime);
            lock.unLock();
            System.out.println("i unlock ->" + sleepTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 写锁
    private static void testWriteLock(long sleepTime) {
        try {
            SharedLock lock = new SharedLock();
            System.out.println("i try to writelock ->" + sleepTime);
            lock.writeLock();
            System.out.println("i get writelock ->" + sleepTime);
            Thread.sleep(sleepTime);
            lock.unLock();
            System.out.println("i unlock ->" + sleepTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
