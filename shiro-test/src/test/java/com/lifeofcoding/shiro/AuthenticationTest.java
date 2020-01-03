package com.lifeofcoding.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @Classname AuthenticationTest
 * @Description shiro认证测试
 * @Date 2019-08-2019-08-01-18:27
 * @Created by yo
 */
public class AuthenticationTest {
    @Test
    public void testAuthentication(){
        //1.创建Realm并添加数据
        SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();
        simpleAccountRealm.addAccount("java","123");

        //2.创建SecurityManager并配置环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        //3.创建subject
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        //4.Subject通过Token提交认证
        UsernamePasswordToken token = new UsernamePasswordToken("java","123");
        subject.login(token);

        //验证认证情况
        System.out.println("isAuthenticated: "+ subject.isAuthenticated());
        //退出登录subject.logout();
    }
}
