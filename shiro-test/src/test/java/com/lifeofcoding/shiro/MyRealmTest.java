package com.lifeofcoding.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @Classname MyRealmTest
 * @Description TODO
 * @Date 2019-11-2019-11-20-15:27
 * @Created by yo
 */
public class MyRealmTest {

    @Test
    public void testMyRealm(){

        //1.创建Realm并添加数据
        MyRealm myRealm = new MyRealm();
        try {
            myRealm.addAccount("java", "123", "admin");
        }catch (Exception e){
            e.printStackTrace();
        }
        myRealm.addPermission("admin","user:delete");

        //2.创建SecurityManager并配置环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(myRealm);

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


