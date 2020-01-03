package com.lifeofcoding.shiro.dao;

import java.util.Set;

public interface RoleDao {

    /**
     * 用于给用户添加角色
     * @param username 用户名
     * @param roles 该用户拥有的所有角色
     * */
    void addRole(String username, Set<String> roles);

    /**
     * 删除某用户的所有角色
     * @param userName 角色名
     * */
    void deleteRolesByUsername(String userName);

    /**
     * 删除数据库中某一角色
     * @param role 要删除的角色
     * */
    void deleteRole(String role);

    /**
     * 删除某用户的某一角色
     * @param userName 用户名
     * @param role 角色
     * */
    void deleteUserRole(String userName, String role);
}
