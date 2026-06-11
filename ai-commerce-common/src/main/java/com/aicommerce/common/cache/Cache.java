package com.aicommerce.common.cache;

import java.time.Duration;
import java.util.Optional;

/**
 * @author: mao026
 * @date: 2025/11/11
 */
public interface Cache {

    void setCache(String key, Object obj);

    void set(String key, Object value, Duration expire);

    Optional<Object> getCache(String key);

    <V> V get(String key, Class<V> clazz);

    <V> V getOrElse(String key, V defaultValue);

    boolean delete(String key);

}
