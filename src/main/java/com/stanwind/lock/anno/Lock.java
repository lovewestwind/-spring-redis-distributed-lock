package com.stanwind.lock.anno;

import com.stanwind.lock.bean.LockPolicy;
import com.stanwind.lock.feature.LockFeatureParam;
import java.lang.annotation.*;

/**
 * @summary 分布式锁
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 锁策略 默认为获取锁失败立即返回
     * @return
     */
    LockPolicy policy() default LockPolicy.Lock;

    /**
     * 锁持续时间 单位秒 默认1分钟
     */
    int lockTime() default 60;

    /**
     * redis锁的key前缀 如果为空，则默认为类名+方法名
     */
    String keyPrex() default "";

    /**
     * 特征字参数方法集
     */
    Class<? extends LockFeatureParam>[] featureParams() default {};

    /**
     * 自旋锁超时时间 单位毫秒
     */
    long timetOut() default 300L;
}
