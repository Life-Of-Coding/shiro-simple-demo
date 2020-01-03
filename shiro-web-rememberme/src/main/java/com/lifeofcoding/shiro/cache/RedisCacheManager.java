package com.lifeofcoding.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import javax.annotation.Resource;

public class RedisCacheManager implements CacheManager {
    @Resource
    private RedisCache redisCache;

    /**
     * 参数s为cache的名称，此处只有一个cache，即RedisCache，直接返回单例的RedisCache实例
     * */
    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return redisCache;
    }
}
