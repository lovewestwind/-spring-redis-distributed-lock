package com.stanwind.lock.redis;

import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisLock 尝试获取锁 立即返回
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-03-31 15:44
 **/
public class RedisLock extends  AbstractRedisLock {

    public RedisLock(RedisTemplate redisTemplate, String lockKey) {
        super(redisTemplate, lockKey);
    }

    public RedisLock(RedisTemplate redisTemplate, String lockKey, int expireTime) {
        super(redisTemplate, lockKey, expireTime);
    }

    /**
     * 尝试获取锁 立即返回
     *
     * @return 是否成功获得锁
     */
    @Override
    public boolean lock() {
        lockValue = UUID.randomUUID().toString();
        //不存在则添加 且设置过期时间（单位ms）
        String result = set(lockKey, lockValue, expireTime);
        locked = OK.equalsIgnoreCase(result);
        return locked;
    }

}
