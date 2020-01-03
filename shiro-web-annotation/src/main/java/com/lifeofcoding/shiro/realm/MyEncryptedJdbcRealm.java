package com.lifeofcoding.shiro.realm;

import com.lifeofcoding.shiro.dao.PermissionDao;
import com.lifeofcoding.shiro.dao.RoleDao;
import com.lifeofcoding.shiro.dao.UserDao;
import com.lifeofcoding.shiro.pojo.User;
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

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

public class MyEncryptedJdbcRealm extends AuthorizingRealm {
    @Resource
    private UserDao userDao;
    @Resource
    private PermissionDao permissionDao;
    @Resource
    private RoleDao roleDao;

    /**加密次数*/
    private int iterations;
    /**加密算法名*/
    private String algorithmName;

    /*---------------------------------实现自定义Realm需要重写的两个方法------------------------------------*/

    /**
     * 身份认证必须实现的方法
     * @param authenticationToken token
     * @return org.apache.shiro.authc.AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.获取主体中的用户名。principal为Object类型，是用户唯一凭证，可以是用户名，用户邮箱，数据库主键等，能唯一确定一个用户的信息。
        String userName = (String) authenticationToken.getPrincipal();
        //2.通过用户名获取密码,getPasswordByName自定义实现
        String password = getPasswordByUserName(userName);
        if(null == password){
            return null;
        }
        //3.如果密码不为空，则构建authenticationInfo认证信息
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,password,"MyRealm");
        String salt = getSaltByUserName(userName);
        //4.认证信息添加盐值
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(salt));
        return authenticationInfo;
    }

    /**
     * 用于授权，必须实现
     * @param principalCollection principal的集合
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
        //添加权限信息
        simpleAuthorizationInfo.setStringPermissions(permissions);
        //添加角色信息
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    //类加载时初始化
    {
        //设置Realm名，可用于获取该realm
        super.setName("MyJdbcRealm");
    }

    /**构造方法,初始化哈希次数及算法名称*/
    MyEncryptedJdbcRealm(){
        iterations = 0;
        algorithmName = "MD5";
    }

    /*--------------------------------------自定义部分--------------------------*/

    /**
     * 自定义部分,通过用户名获取权限信息
     * @param userName username
     * @return 该用户拥有的所有权限
     */
    public Set<String> getPermissionsByUserName(String userName) {
        //1.先通过用户名获取所有角色信息
        Set<String> roles = userDao.getRolesByUserName(userName);
        //2.通过角色信息获取对应的权限
        Set<String> permissions = new HashSet<>();
        roles.forEach(role -> {
            Set<String> tempPermissions = permissionDao.getPermissionsByRole(role);
            if (null != tempPermissions) {
                permissions.addAll(tempPermissions);
            }
        });
        return permissions;
    }

    /**
     * 自定义部分，通过用户名获取密码
     * @param userName username
     * @return java.lang.String
     */
    public String getPasswordByUserName(String userName){
        return userDao.getPasswordByUserName(userName);
    }

    /**
     * 自定义部分，通过用户名获取盐
     * @param userName username
     * @return java.lang.String
     */
    public String getSaltByUserName(String userName){
        return userDao.getSaltByUserName(userName);
    }

    /**
     * 自定义部分，通过用户名获取角色信息
     * @param userName username
     * @return java.util.Set<java.lang.String>
     */
    public Set<String> getRolesByUserName(String userName){
        return userDao.getRolesByUserName(userName);
    }


    /**
     * 往realm添加账号信息
     * @param user user
     */
    public void addAccount(User user) throws Exception {
        String salt = "";
        String password = user.getPassword();
        String userName = user.getUsername();
        //用户信息为空抛出异常
        if (user.getUsername()==null || user.getPassword()==null){
            throw new InfoEmptyException("username or password can not be empty");
        }
        //如果用户已经注册，抛出异常
        if(null != userDao.getPasswordByUserName(userName)){
            throw new UserExistException("user \""+ userName +"\" already exist");
        }
        //如果设置的加密次数大于0,则进行加密
        if(iterations > 0){
            salt = randomSalt();
            password = doHash(password, salt);
        }
        user.setPassword(password);
        user.setSalt(salt);
        userDao.addUser(user);
        if (CollectionUtils.isEmpty(user.getRoles())){
            return;
        }
        roleDao.addRole(userName,user.getRoles());
    }

    /**
     * 添加角色权限
     * @param roleName 角色名
     * @param permissions 该角色拥有的权限
     */
    public void addPermissions(String roleName, Set<String> permissions) throws Exception{
        permissionDao.addPermissions(roleName,permissions);
    }

    /**
    * 用随机数作为盐值，可改为UUID或其他
    * */
    public String randomSalt(){
        return String.valueOf(Math.random()*10);
    }

    /**
     * 删除账号信息
     * @param userName 用户名
     */
    public void deleteAccount(String userName) throws Exception{
        userDao.deleteUser(userName);
        roleDao.deleteRolesByUsername(userName);
    }

    /**
     * 设置加密次数
     * @param iterations 哈希操作的次数
     */
    public void setHashIterations(int iterations){
        this.iterations = iterations;
    }

    /**
     * 设置算法名
     * @param algorithmName 哈希算法名
     */
    public void setAlgorithmName(String algorithmName){
        this.algorithmName = algorithmName;
    }

    /**
     * 进行哈希运算
     * @param source 原来的字符
     * @param salt 盐值
     * @return 运算结果
     * */
    private String doHash(String source, String salt){
        return new SimpleHash(this.algorithmName,source,salt,this.iterations).toString();
    }

    /**
     * 注册时，用户已存在的异常类
     */
    public class UserExistException extends Exception{
        public UserExistException(String message) {super(message);}
    }

    /**
     * 用户信息为空的异常
     * */
    public class InfoEmptyException extends Exception{
        public InfoEmptyException(String message) {super(message);}
    }

}
