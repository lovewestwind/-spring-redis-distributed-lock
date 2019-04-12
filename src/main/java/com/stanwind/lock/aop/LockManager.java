package com.stanwind.lock.aop;

import com.stanwind.lock.anno.Lock;
import com.stanwind.lock.anno.LockKey;
import com.stanwind.lock.bean.LockException;
import com.stanwind.lock.redis.AbstractRedisLock;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @version v1
 * @summary 分布式锁管理
 */
@Aspect
@Component
public class LockManager {

    private static Logger logger = LoggerFactory.getLogger(LockManager.class);

    private static Map<AbstractRedisLock, Thread> locks = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate redisTemplate;

    @Around(value = "@annotation(lock)", argNames = "pjp, lock")
    public Object around(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        Class clazz = pjp.getTarget().getClass();
        String methodName = pjp.getSignature().getName();
        //获取参数
        Object[] args = pjp.getArgs();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method m = ms.getMethod();

        String keyPrix = lock.keyPrex();
        if (keyPrix.equals("")) {
            keyPrix = clazz.getName() + ":" + methodName + ":";
        }

        //features
        StringBuilder sb = new StringBuilder(keyPrix);
        if (lock.featureParams().length > 0) {
            Arrays.stream(lock.featureParams()).forEach(c -> {
                try {
                    String feature = c.newInstance().getFeature();
                    if (feature == null) {
                        throw new LockException("feature参数值不能为null");
                    }

                    sb.append(feature).append(":");
                } catch (Exception e) {
                    doThrow(e);
                }
            });
        }

        keyPrix = sb.toString();

        //分布式锁的key
        StringBuilder lockKey = new StringBuilder("lock:" + keyPrix);
        //获取加注解的方法参数的值
        Annotation[][] parameterAnnotations = m.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == LockKey.class) {
                    LockKey anno = (LockKey) annotation;
                    if (anno.keyField() != null && anno.keyField().length > 0) {
                        lockKey.append(genModelKey(args[i], anno.keyField()));
                    } else {
                        lockKey.append("_");
                        lockKey.append(args[i]);
                    }
                }
            }
        }

        Object result = null;
        try (AbstractRedisLock redisLock = RedisLockFactory
                .getLock(lock.policy(), redisTemplate, lockKey.toString(), lock.lockTime(), lock.timetOut())) {
            try {
                //置入map 程序结束时释放
                locks.put(redisLock, Thread.currentThread());
                // 获得锁
                if (redisLock.lock()) {
                    result = pjp.proceed();
                } else {
                    throw new LockException("获取分布式锁失败, key=" + lockKey.toString());
                }
            } finally {
                locks.remove(redisLock);
            }
        }

        return result;
    }

    @PreDestroy
    public void destroy() {
        //shutdownHook
        while (locks.size() > 0) {
            logger.info("等待{}个占用锁线程退出\n{}", locks.size(), Arrays.toString(locks.keySet().toArray()));
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                logger.error("等待锁线程退出被中断", e);
            }
        }
    }

    static <E extends Exception> void doThrow(Exception e) throws E {
        throw (E) e;
    }

    private String genModelKey(Object model, String[] fields) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            sb.append("_");
            try {
                sb.append(model.getClass().getMethod(buildGetMethod(field)).invoke(model));
            } catch (Exception e) {
                logger.error("redisLock error: keyField '{}' 不存在", field, e);
                throw e;
            }
        }
        return sb.toString();
    }

    private String buildGetMethod(String field) {
        StringBuilder sb = new StringBuilder("get");
        sb.append(Character.toUpperCase(field.charAt(0)));
        sb.append(field.substring(1, field.length()));
        return sb.toString();
    }

}
