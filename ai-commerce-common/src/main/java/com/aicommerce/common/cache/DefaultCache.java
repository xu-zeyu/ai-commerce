package com.aicommerce.common.cache;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: mao026
 * @date: 2025/11/11
 */
public class DefaultCache implements Cache {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public void setCache(String key, Object obj) {
        this.cache.put(key, obj);
    }

    @Override
    public Optional<Object> getCache(String key) {
        return Optional.ofNullable(this.cache.get(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(String key, Class<V> clazz) {
        return (V) this.cache.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getOrElse(String key, V defaultValue) {
        return (V) this.cache.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean delete(String key) {
        return this.cache.remove(key) != null;
    }

    @Override
    public void set(String key, Object value, Duration expire) {
        throw new UnsupportedOperationException("暂不支持");
    }

}
