package base;

/**
 * 描述：基础 Lock
 * 作者：liangyongtong
 * 日期：2019/10/21 4:22 PM
 * 接口：Lock
 * 版本： version 1.0
 */
public interface Lock {

    /**
     * 获取锁
     */
    void lock() throws Exception;

    /**
     * 释放锁
     */
    void unLock() throws Exception;
}
