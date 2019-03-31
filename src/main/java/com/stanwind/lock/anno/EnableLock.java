package com.stanwind.lock.anno;

import com.stanwind.lock.aop.LockManager;
import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

/**
 * @summary 开启分布式锁注解 #{@link Lock}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(LockManager.class)
public @interface EnableLock {

}
