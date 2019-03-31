# spring-redis-distributed-lock
<p align="center"><a href="https://gitee.com/lovewestwind/spring-redis-distributed-lock/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-MIT-000000.svg" /></a></p>  
#### 介绍
spring-redis-distributed-lock 为redisLock项目扩展而来 https://gitee.com/billion/redisLock

一个应用于springboot项目的，基于redis的分布式锁
可用于多节点项目防重复业务调用

通过方法注解开启 简单、易用

可以通过简单的注解配置达到同机器或不同机器竞争锁

#### 使用步骤
0. 在包在SpringBootApplication上添加@ComponentScan(basePackages = { "com.stanwind.lock" } 扫描lock所在包
1. 在SpringBootApplication上添加@EnableLock注解引入redisLock。
2. 在需要开启分布式锁的方法上添加@Lock注解。
3. 分布式锁支持粒度为方法参数的级别，通过@LockKey注解到方法参数上，被注解的参数会添加作为redis key的后缀。 具有相同redis key的方法调用会竞争同一把锁。一个方法可以添加多个@LockKey。

#### 代码样例
**直接使用**
```java
@Lock
public boolean service() {
    //业务代码
}
```

**参数粒度**
```java
@Lock
public boolean service(@LockKey int id) {
    //业务代码
}
```

**同机器不同进程竞争**
```java
@Lock(featureParams = MacAddressFeatureParam.class)
public boolean service(@LockKey int id) {
    //业务代码
}
```

**自旋锁,超时放弃 默认300 单位ms**
```java
@Lock(policy = LockPolicy.SpinLock, timetOut = 500L)
public boolean service(@LockKey int id) {
    //业务代码
}
```

**阻塞锁 一直自旋重试**
```java
@Lock(policy = LockPolicy.SpinLock, timetOut = 500L)
public boolean service(@LockKey int id) {
    //业务代码
}
```

#### @Lock参数介绍
| 名称          | 类型             | 默认值      | 备注                                                                     |
| ------------- | ---------------- | ----------- | ------------------------------------------------------------------------ |
| policy        | LockPolicy       | Lock        | 锁策略 默认为Lock：获取锁失败立即返回  SpinLock：自旋锁，达到timeout后返回  BlockLock 持续自旋直到获取锁返回                                        |
| keyPrex       | String           | 类名+方法名 | redis锁的key前缀 如果为空，则默认为类名+方法名                           |
| featureParams | LockFeatureParam | 无          | 特征字参数方法集，可以增加不在业务方法中的特征参数用于区分锁，防止竞争锁 可实现LockFeatureParam 扩展自定义非业务参数 |
| lockTime | int | 60 | 锁持续时间，将设定在redis中用于值存在的时间 单位s                                             |
| timetOut      | long             | 300         | SpinLock超时时间                                                         |

