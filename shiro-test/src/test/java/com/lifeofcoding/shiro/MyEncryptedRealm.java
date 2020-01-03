package com.lifeofcoding.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.CollectionUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyEncryptedRealm extends AuthorizingRealm {

    /** 加密次数 */
    private int iterations;
    /** 算法名 */
    private String algorithmName;

    /** 存储用户名和密码 */
    private final Map<String,String> userMap;
    /** 存储用户及其对应的角色 */
    private final Map<String, Set<String>> roleMap;
    /** 存储所有角色以及角色对应的权限 */
    private final Map<String,Set<String>> permissionMap;
    /** 存储用户盐值 */
    private Map<String,String> saltMap;

    {
        //设置Realm名
        super.setName("MyRealm");
    }

    public MyEncryptedRealm(){
        this.iterations = 0;
        this.algorithmName = "MD5";
        this.userMap = new ConcurrentHashMap<>(16);
        this.roleMap = new ConcurrentHashMap<>(16);
        this.permissionMap  = new ConcurrentHashMap<>(16);
        this.saltMap = new ConcurrentHashMap<>(16);
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
        //3.构建authenticationInfo认证信息
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,password,"MyRealm");
        //设置盐值
        String salt = getSaltByUserName(userName);
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(salt));
        return authenticationInfo;
    }


    /**
     * 用于授权
     * @return org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.获取用户名。principal为Object类型，是用户唯一凭证，可以是用户名，用户邮箱，数据库主键等，能唯一确定一个用户的信息。
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
        Set<String> permissions = new HashSet<>();
        //3.添加每个角色对应的所有权限
        roles.forEach(role -> {
            if (null != permissionMap.get(role)) {
                permissions.addAll(permissionMap.get(role));
            }
        });
        return permissions;
    }

    /**
     * 自定义部分，通过用户名获取密码，可改为数据库操作
     * @return java.lang.String
     */
    private String getPasswordByUserName(String userName){
        return userMap.get(userName);
    }

    /**
     * 自定义部分，通过用户名获取角色信息,可改为数据库操作
     * @return java.util.Set<java.lang.String>
     */
    private Set<String> getRolesByUserName(String userName){
        return roleMap.get(userName);
    }

    /**
     * 自定义部分，通过用户名获取盐值,可改为数据库操作
     * @return java.util.Set<java.lang.String>
     */
    private String getSaltByUserName(String userName) {
        return saltMap.get(userName);
    }

    /**
     * 往realm添加账号信息,变参不传值会接收到长度为0的数组。
     */
    public void addAccount(String userName,String password) throws UserExistException {
        addAccount(userName,password,(String[]) null);
    }

    /**
     * 往realm添加账号信息
     */
    public void addAccount(String userName,String password,String... roles) throws UserExistException {
        if(null != userMap.get(userName)){
            throw new UserExistException("user \""+ userName +"\" exist");
        }
        //如果配置的加密次数大于0，则进行加密
        if(iterations > 0){
            //使用随机数作为密码，可改为UUID或其它
            String salt = String.valueOf(Math.random()*10);
            saltMap.put(userName,salt);
            password = doHash(password, salt);
        }
        userMap.put(userName, password);
        roleMap.put(userName, CollectionUtils.asSet(roles));
    }

    /**
     * 从realm删除账号信息
     * @param userName 用户名
     */
    public void deleteAccount(String userName){
        userMap.remove(userName);
        roleMap.remove(userName);
    }


    /**
     * 添加角色权限,变参不传值会接收到长度为0的数组。
     * @param roleName 角色名
     */
    public void addPermission(String roleName,String...permissions){
        permissionMap.put(roleName, CollectionUtils.asSet(permissions));
    }

    /**
     * 设置加密次数
     * @param iterations 加密次数
     */
    public void setHashIterations(int iterations){
        this.iterations = iterations;
    }

    /**
     * 设置算法名
     * @param algorithmName 算法名
     */
    public void setAlgorithmName(String algorithmName){
        this.algorithmName = algorithmName;
    }

    /**
     * 计算哈希值
     * @param str 要进行"加密"的字符串
     * @param salt 盐
     * @return String
     */
    private String doHash(String str,String salt){
        salt = null==salt ? "" : salt;
        return new SimpleHash(this.algorithmName,str,salt,this.iterations).toString();
    }


    /**
     * 注册时，用户已存在的异常
     */
    public class UserExistException extends Exception{
        public UserExistException(String message) {super(message);}
    }

}
