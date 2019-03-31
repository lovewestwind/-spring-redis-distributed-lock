package com.stanwind.lock.redis;

import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * SpinRedisLock 自旋锁
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-03-31 15:45
 **/
public class SpinRedisLock extends AbstractRedisLock {

    /**
     * 请求锁的超时时间(ms)
     */
    protected long timeout = TIME_OUT;

    public SpinRedisLock(RedisTemplate redisTemplate, String lockKey) {
        super(redisTemplate, lockKey);
    }

    public SpinRedisLock(RedisTemplate redisTemplate, String lockKey, int expireTime) {
        super(redisTemplate, lockKey, expireTime);
    }

    /**
     * 使用默认的锁的过期时间，指定请求锁的超时时间
     *
     * @param redisTemplate
     * @param lockKey       锁的key（Redis的Key）
     * @param timeout       请求锁的超时时间(单位：毫秒)
     */
    public SpinRedisLock(RedisTemplate redisTemplate, String lockKey, long timeout) {
        super(redisTemplate, lockKey);
        this.timeout = timeout;
    }

    /**
     * 锁的过期时间和请求锁的超时时间都是用指定的值
     *
     * @param redisTemplate
     * @param lockKey       锁的key（Redis的Key）
     * @param expireTime    锁的过期时间(单位：秒)
     * @param timeout       请求锁的超时时间(单位：毫秒)
     */
    public SpinRedisLock(RedisTemplate redisTemplate, String lockKey, int expireTime, long timeout) {
        super(redisTemplate, lockKey, expireTime);
        this.timeout = timeout;
    }

    @Override
    public boolean lock() {
        // 生成随机key
        lockValue = UUID.randomUUID().toString();
        // 请求锁超时时间，纳秒
        long _timeout = timeout * 1000000;
        // 系统当前时间，纳秒
        long nowTime = System.nanoTime();
        while ((System.nanoTime() - nowTime) < _timeout) {
            if (OK.equalsIgnoreCase(this.set(lockKey, lockValue, expireTime))) {
                locked = true;
                // 上锁成功结束请求
                return true;
            }

            // 每次请求等待一段时间
            sleep(10, 50000);
        }
        return locked;
    }
}
