package com.lifeofcoding.shiro;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @Classname JdbcRealmTest
 * @Description TODO
 * @Date 2019-09-2019-09-01-20:23
 * @Created by yo
 */
public class JdbcRealmTest {

    DruidDataSource druidDataSource = new DruidDataSource();
    {
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/shiro");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("0113");
    }

    @Test
    public void testJdbcRealm(){

        //1.创建Realm并添加数据
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(druidDataSource);//配置数据源
        jdbcRealm.setPermissionsLookupEnabled(true);//设置允许查询权限，否则checkPermission抛异常，默认值为false

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
