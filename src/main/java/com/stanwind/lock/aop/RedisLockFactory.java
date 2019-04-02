package com.stanwind.lock.aop;

import com.stanwind.lock.bean.LockPolicy;
import com.stanwind.lock.redis.AbstractRedisLock;
import com.stanwind.lock.redis.BlockRedisLock;
import com.stanwind.lock.redis.RedisLock;
import com.stanwind.lock.redis.SpinRedisLock;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisLockFactory 锁工厂
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-03-31 15:56
 **/
public class RedisLockFactory {

    public static AbstractRedisLock getLock(LockPolicy policy, RedisTemplate redisTemplate, String lockKey,
            int expireTime, long timeout) {
        AbstractRedisLock lock = null;
        switch (policy) {
            case BlockLock:
                lock = new BlockRedisLock(redisTemplate, lockKey, expireTime);
                break;
            case SpinLock:
                lock = new SpinRedisLock(redisTemplate, lockKey, expireTime, timeout);
                break;
            case Lock:
            default:
                lock = new RedisLock(redisTemplate, lockKey, expireTime);
                break;
        }

        return lock;
    }

}
