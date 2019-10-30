package base;

/**
 * 描述：共享锁
 * 作者：liangyongtong
 * 日期：2019/10/30 2:57 PM
 * 接口：ReadWriteLock
 * 版本： version 1.0
 */
public interface ReadWriteLock {
    /**
     * 写锁
     */
    void readLock() throws Exception;

    /**
     * 读锁
     */
    void writeLock() throws Exception;

    /**
     * 释放锁
     */
    void unLock() throws Exception;
}
