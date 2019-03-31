package com.stanwind.lock.redis;

import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * BlockRedisLock 以阻塞方式的获取锁
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-03-31 15:41
 **/
public class BlockRedisLock extends AbstractRedisLock {

    public BlockRedisLock(RedisTemplate redisTemplate, String lockKey) {
        super(redisTemplate, lockKey);
    }

    public BlockRedisLock(RedisTemplate redisTemplate, String lockKey, int expireTime) {
        super(redisTemplate, lockKey, expireTime);
    }

    @Override
    public boolean lock() {
        lockValue = UUID.randomUUID().toString();
        while (true) {
            //不存在则添加 且设置过期时间（单位ms）
            String result = set(lockKey, lockValue, expireTime);
            if (OK.equalsIgnoreCase(result)) {
                locked = true;
                return true;
            }

            // 每次请求等待一段时间
            sleep(10, 50000);
        }
    }
}
