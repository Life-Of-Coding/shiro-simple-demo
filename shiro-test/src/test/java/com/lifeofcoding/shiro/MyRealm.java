package com.lifeofcoding.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyRealm extends AuthorizingRealm {

    /**存储用户名和密码*/
    protected final Map<String,String> userMap;
    /**存储用户及其对应的角色*/
    protected final Map<String, Set<String>> roleMap;
    /**存储所有角色以及角色对应的权限*/
    protected final Map<String,Set<String>> permissionMap;

    {
        //设置Realm名
        super.setName("MyRealm")  ;
    }

    public MyRealm(){
        userMap = new ConcurrentHashMap<>(16);
        roleMap = new ConcurrentHashMap<>(16);
        permissionMap = new ConcurrentHashMap<>(16);
    }

    /**
     * 身份认证必须实现的方法
     * @param authenticationToken token
     * @return org.apache.shiro.authc.AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.获取主体中的用户名
        String userName = (String) authenticationToken.getPrincipal();
        //2.通过用户名获取密码,getPasswordByName自定义实现
        String password = getPasswordByUserName(userName);
        if(null == password){
            return null;
        }
        //构建AuthenticationInfo返回
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,password,"MyRealm");
        return authenticationInfo;
    }

    /**
     * 用于授权
     * @return org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.获取用户名。principal为Object类型，是用户唯一标识，可以是用户名，用户邮箱，数据库主键等，能唯一确定一个用户的信息。
        String userName = (String) principalCollection.getPrimaryPrincipal();
        //2.获取角色信息，getRoleByUserName自定义
        Set<String> roles = getRolesByUserName(userName);
        //3.获取权限信息，getPermissionsByRole方法同样自定义，也可以通过用户名查找权限信息
        Set<String> permissions = getPermissionsByUserName(userName);
        //4.构建认证信息并返回。
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    /**
     * 自定义部分,通过用户名获取权限信息
     * @return java.util.Set<java.lang.String>
     */
    private Set<String> getPermissionsByUserName(String userName) {
        //1.先通过用户名获取角色信息
        Set<String> roles = getRolesByUserName(userName);
        //2.通过角色信息获取对应的权限
        Set<String> permissions = new HashSet<String>();
        //3.添加每个角色对应的所有权限
        roles.forEach(role -> {
            if(null != permissionMap.get(role)) {
                permissions.addAll(permissionMap.get(role));
            }

        });
        return permissions;
    }

    /**
     * 自定义部分，通过用户名获取密码，可改为数据库操作
     * @param userName 用户名
     * @return java.lang.String
     */
    private String getPasswordByUserName(String userName){
        return userMap.get(userName);
    }

    /**
     * 自定义部分，通过用户名获取角色信息,可改为数据库操作
     * @param userName 用户名
     * @return java.util.Set<java.lang.String>
     */
    private Set<String> getRolesByUserName(String userName){
        return roleMap.get(userName);
    }

    /**
     * 往realm添加账号信息,变参不传值会接收到长度为0的数组。
     */
    public void addAccount(String userName,String password) throws UserExistException{
        addAccount(userName,password,(String[]) null);
    }

    /**
     * 往realm添加账号信息
     * @param userName 用户名
     * @param password 密码
     * @param roles 角色(变参)
     */
    public void addAccount(String userName,String password,String... roles) throws UserExistException{
        if( null != userMap.get(userName) ){
            throw new UserExistException("user \""+ userName +" \" exists");
        }
        userMap.put(userName, password);
        roleMap.put(userName, CollectionUtils.asSet(roles));
    }

    /**
     * 从realm删除账号信息
     * @param userName 用户名
     */
    public void delAccount(String userName){
        userMap.remove(userName);
        roleMap.remove(userName);
    }

    /**
     * 添加角色权限,变参不传值会接收到长度为0的数组。
     * @param roleName 角色名
     */
    public void addPermission(String roleName,String...permissions){
        permissionMap.put(roleName,CollectionUtils.asSet(permissions));
    }

    /**用户已存在异常*/
    public class UserExistException extends Exception{
        public UserExistException(String message){super(message);}
    }
}
