package com.lifeofcoding.shiro;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @Classname MyJdbcRealmTest
 * @Description TODO
 * @Date 2019-09-2019-09-03-18:20
 * @Created by yo
 */
public class MyJdbcRealmTest {

    //从数据库获取对应用户密码实现认证
    protected static final String AUTHENTICATION_QUERY = "select password from test_users where username = ?";
    //从数据库中获取对应用户的所有角色
    protected static final String USER_ROLES_QUERY = "select role from test_user_roles where username = ?";
    //从数据库中获取角色对应的所有权限
    protected static final String PERMISSIONS_QUERY = "select permission from test_roles_permissions where role = ?";

    DruidDataSource druidDataSource = new DruidDataSource();
    {
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/shiro");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("0113");
    }

    @Test
    public void testMyJdbcRealm(){

        //1.创建Realm并设置数据库查询语句
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(druidDataSource);//配置数据源
        jdbcRealm.setPermissionsLookupEnabled(true);//设置允许查询权限，否则checkPermission抛异常，默认值为false
        jdbcRealm.setAuthenticationQuery(AUTHENTICATION_QUERY);//设置用户认证信息查询语句
        jdbcRealm.setUserRolesQuery(USER_ROLES_QUERY);//设置用户角色信息查询语句
        jdbcRealm.setPermissionsQuery(PERMISSIONS_QUERY);//设置角色权限信息查询语句

        //2.创建SecurityManager并配置环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);

        //3.创建subject
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        //4.Subject通过Token提交认证
        UsernamePasswordToken token = new UsernamePasswordToken("java","123");
        subject.login(token);//退出登录subject.logout();

        //验证认证与授权情况
        System.out.println("isAuthenticated: "+ subject.isAuthenticated());
        subject.hasRole("admin");
        subject.checkPermission("user:delete");
    }
}
