package com.aicommerce.starter.cache;

import com.aicommerce.common.cache.Cache;
import jakarta.annotation.Resource;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 类名: RedissionCache
 * 描述: 基于 Redisson 的 Redis 缓存实现类
 * 作者: xuzeyu
 * 创建时间: 2025/12/23
 */
@Component
public class CacheUtils implements Cache {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void setCache(String key, Object obj) {
        redissonClient.getBucket(key).set(obj);
    }

    @Override
    public void set(String key, Object value, Duration expire) {
        redissonClient.getBucket(key).set(value, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<Object> getCache(String key) {
        return Optional.ofNullable(redissonClient.getBucket(key).get());
    }

    @Override
    public <V> V get(String key, Class<V> clazz) {
        RBucket<V> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }


    @Override
    public <V> V getOrElse(String key, V defaultValue) {
        RBucket<V> bucket = redissonClient.getBucket(key);
        return Optional.ofNullable(bucket.get()).orElse(defaultValue);
    }

    @Override
    public boolean delete(String key) {
        return redissonClient.getBucket(key).delete();
    }
    // 扩展方法：操作 Map 结构
    public <K, V> RMap<K, V> getMap(String key) {
        return redissonClient.getMap(key);
    }
}
