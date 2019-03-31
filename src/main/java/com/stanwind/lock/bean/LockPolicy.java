package com.stanwind.lock.bean;

/**
 * LockPolicy 锁策略
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-03-31 15:59
 **/
public enum LockPolicy {
    /**
     * 默认锁 失败立即返回
     */
    Lock,
    /**
     * 自旋锁 超时时间timeout
     */
    SpingLock,
    /**
     * 阻塞锁 无超时自旋
     */
    BlockLock
}
