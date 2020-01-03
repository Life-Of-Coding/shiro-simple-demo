package com.lifeofcoding.shiro.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.HashSet;
import java.util.Set;

@Component
public class JedisUtil {

    /**jedis连接池*/
    @Autowired
    private JedisPool jedisPool;

    /**获取资源*/
    private Jedis getResource(){
        return jedisPool.getResource();
    }

    /**
     * set
     * */
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = getResource();
        try{
            jedis.set(key, value);
            return value;
        }finally {
            jedis.close();
        }
    }

    /**
     * 设置过期时间
     * */
    public void expire(byte[] key, int seconds) {
        Jedis jedis = getResource();
        try {
            jedis.expire(key,seconds);
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取值
     * */
    public byte[] get(byte[] key) {
        Jedis jedis = getResource();
        try {
            return jedis.get(key);
        } finally {
            jedis.close();
        }
    }

    /**
     * 删除
     * */
    public void del(byte[] key) {
        Jedis jedis = getResource();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    /**
     * "keys"操作
     * */
    public Set<byte[]> keys(String pattern) {
        Jedis jedis = getResource();
        try {
            return jedis.keys((pattern).getBytes());
        } finally {
            jedis.close();
        }
    }

    /**
     * 使用scan获取所有匹配的keys，redis2.8+开始，加入了"scan"操作，
     * 允许每次只获取一部分数据，避免数据量大时"keys"造成阻塞
     * */
    public Set<byte[]> scan(String pattern){
        Jedis jedis = getResource();
        //初始化游标
        byte[] START_CURSOR = "0".getBytes();
        //每次要求返回的数据量
        int NUM_PER_SCAN = 50;
        try{
            //设置初始化游标
            byte[] cursor = START_CURSOR;
            //查询参数对象
            ScanParams params = new ScanParams();
            //设置匹配模式
            params.match(pattern.getBytes());
            //设置理想的每次返回的数据数量(不一定会返回这么多)
            params.count(NUM_PER_SCAN);
            //用一个HashSet来存储查找到的keys，因为结果可能会重复，所以用set去重
            Set<byte[]> keys = new HashSet<>();
            while(true) {
                /*redis的scan与单循环链表相似，每次scan操作,返回部分数据result以及下次scan操作需要的游标cursor*/
                ScanResult result = jedis.scan(cursor,params);
                //获取下次scan的游标，byte[]类型，如果是String类型，返回结果也会是String类型，需要注意。
                cursor = result.getCursorAsBytes();
                keys.addAll(result.getResult());
                //如果已经遍历完所有数据，则退出
                if(result.isCompleteIteration()) {break;}
            }
            return keys;
        }finally {
            jedis.close();
        }
    }

}
