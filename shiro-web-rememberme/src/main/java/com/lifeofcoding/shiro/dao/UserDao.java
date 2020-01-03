package com.lifeofcoding.shiro.dao;

import com.lifeofcoding.shiro.pojo.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public interface UserDao {

    /**
     * 通过用户名获取用户密码
     * @param userName 用户名
     * @return String 用户密码
     * */
    String getPasswordByUserName(String userName);

    /**
     * 通过用户名获取该用户拥有的所有角色
     * @param userName 用户名
     * @return Set&lt;String&gt; 存储该用户所有角色的Set
     * */
    Set<String> getRolesByUserName(String userName);

    /**
     * 通过用户名获取该用户的盐值
     * @param userName 用户名
     * @return String 用户的盐值
     * */
    String getSaltByUserName(String userName);

    /**
     * 添加用户
     * @param user 用户对象，包括用户名(username)、用户密码(password)、用户盐值(salt)、用户角色(roles)
     * @throws Exception 有可能抛出数据库操作的一些异常
     * */
    void addUser(User user) throws Exception;

    /**
     * 删除用户
     * @param userName 用户名
     * @throws Exception 有可能抛出数据库操作的异常
     * */
    void deleteUser(String userName) throws Exception;
}
