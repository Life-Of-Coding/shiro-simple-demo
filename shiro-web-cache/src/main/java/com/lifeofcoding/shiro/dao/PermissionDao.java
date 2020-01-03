package com.lifeofcoding.shiro.dao;

import java.util.Set;

public interface PermissionDao {

    /**
     * 给角色添加权限
     * @param roleName 角色名
     * @param permissions 该用户对应的所有权限
     * */
    void addPermissions(String roleName, Set<String> permissions) ;

    /**
     * 获取角色拥有的权限
     * @param role 角色
     * @return 所有该角色拥有的权限的集合
     * */
    Set<String> getPermissionsByRole(String role);

    /**
     * 删除某角色的所有权限
     * @param role 角色名
     * */
    void deletePermissionsByRole(String role);

    /**
     * 删除数据库中某一权限
     * @param permission 要删除的权限
     * */
    void deletePermission(String permission);

    /**
     * 删除某角色的某一权限
     * @param role 角色
     * @param permission 权限
     * */
    void deleteRolePermission(String role, String permission);
}
