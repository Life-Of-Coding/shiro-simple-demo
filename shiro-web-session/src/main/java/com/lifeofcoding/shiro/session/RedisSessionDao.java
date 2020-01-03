package com.lifeofcoding.shiro.session;

import com.lifeofcoding.shiro.utils.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RedisSessionDao extends AbstractSessionDAO {

    /**封装的redis工具类*/
    @Resource
    private JedisUtil jedisUtil;

    /**在redis中存储的session的前缀*/
    private final String SHIRO_SESSION_PREFIX="shiro-session:";

    /**
     * 把传入的key(sessionId)转化为在redis中存储的统一格式的key
     * */
    private byte[] getKey(String key){
        return (SHIRO_SESSION_PREFIX+key).getBytes();
    }

    /**
     * 保存session到redis中
     * */
    private void saveSession(Session session){
        if (null != session && null != session.getId()) {
            //获取session的id并将其传化为指定格式
            byte[] key = getKey(session.getId().toString());
            //对session进行序列化
            byte[] value = SerializationUtils.serialize(session);
            jedisUtil.set(key, value);
            jedisUtil.expire(key, 600);
        }
    }

    /**
     * 把session保存到redis
     * */
    @Override
    protected Serializable doCreate(Session session) {
        //创建sessionId
        Serializable sessionId = generateSessionId(session);
        //给session绑定sessionId
        assignSessionId(session,sessionId);
        //保存session到redis中
        saveSession(session);
        return sessionId;
    }

    /**
     * 读取session
     * */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        System.out.println("Read session from redis: "+sessionId);
        if (null == sessionId) {
            return null;
        }
        //把sessionId转化为redis中的key的格式
        byte[] key = getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        //返回反序列化后的session
        return (Session) SerializationUtils.deserialize(value);
    }

    /**
     * 更新session
     * */
    @Override
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    /**
     * 删除session
     * */
    @Override
    public void delete(Session session) {
        if (null == session && null == session.getId()){
            return;
        }
        byte[] key = getKey(session.getId().toString());
        jedisUtil.del(key);
    }

    /**
     * 获取活跃的session
     * */
    @Override
    public Collection<Session> getActiveSessions() {
        //获取redis中存储session的所有key
        //Set<byte[]> keys = jedisUtil.keys(SHIRO_SESSION_PREFIX+"*");
        //可以自己改写、优化scan，用"scan"操作替代"keys",避免数据量大时阻塞。
        Set<byte[]> keys = jedisUtil.scan(SHIRO_SESSION_PREFIX+"*");
        Set<Session> sessions = new HashSet<Session>();
        if (CollectionUtils.isEmpty(keys)){
            return sessions;
        }
        for (byte[] key : keys){
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
