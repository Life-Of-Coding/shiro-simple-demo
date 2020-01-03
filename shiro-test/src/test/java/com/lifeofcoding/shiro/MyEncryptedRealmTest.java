package com.lifeofcoding.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @Classname MyEncryptionRealmTest
 * @Description TODO
 * @Date 2019-11-2019-11-20-17:41
 * @Created by yo
 */
public class MyEncryptedRealmTest {

    private MyEncryptedRealm myEncryptedRealm;

    @Test
    public void testShiroEncryption() {
        //1.创建Realm并添加数据
        MyEncryptedRealm myEncryptedRealm = new MyEncryptedRealm();
        myEncryptedRealm.setHashIterations(3);
        myEncryptedRealm.setAlgorithmName("MD5");
        try {
            myEncryptedRealm.addAccount("java", "123456", "admin");
        }catch (Exception e){
            e.printStackTrace();
        }
        myEncryptedRealm.addPermission("admin","user:create","user:delete");

        //2.创建SecurityManager对象
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager(myEncryptedRealm);

        //3.设置加密
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");//设置加密算法
        matcher.setHashIterations(3);//设置加密次数
        myEncryptedRealm.setCredentialsMatcher(matcher);

        //4.创建主体并提交认证
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("java","123456");
        subject.login(token);
        System.out.println(subject.getPrincipal()+" isAuthenticated: "+subject.isAuthenticated());
        subject.checkRole("admin");
        subject.checkPermission("user:delete");
    }
}
