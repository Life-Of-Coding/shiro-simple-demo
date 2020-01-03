package com.lifeofcoding.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import java.util.ArrayList;

/**
 * @Classname AuthorizationTest
 * @Description TODO
 * @Date 2019-08-2019-08-03-17:40
 * @Created by yo
 */
public class AuthorizationTest {
    @Test
    public void testAuthorization() {
        //1.创建Realm并添加数据
        IniRealm iniRealm = new IniRealm("classpath:UserData.ini");

        //2.创建SecurityManager并配置环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(iniRealm);

        //3.创建Subject
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        //4.主体提交认证
        UsernamePasswordToken token = new UsernamePasswordToken("java", "123");
        subject.login(token);

        /*以下为授权的几种方法*/
        //①.直接判断是否有某权限,isPermitted方法返回boolean，不抛异常
        System.out.println("user:login isPermitted: " + subject.isPermitted("user:login"));

        //②.通过角色进行授权，方法有返回值，不抛异常
        subject.hasRole("user");//判断主体是否有某角色
        subject.hasRoles(new ArrayList<String>() {//返回boolean数组，数组顺序与参数Roles顺序一致,接受List<String>参数
                             {
                                 add("admin");
                                 add("user");
                             }
                         });
        subject.hasAllRoles(new ArrayList<String>() {//返回一个boolean，Subject包含所有Roles时才返回true,接受Collection<String>参数
                               {
                                   add("admin");
                                   add("user");
                               }
                            });

        //③.通过角色授权，与上面大体相同，不过这里的方法无返回值，授权失败会抛出异常，需做好异常处理
        subject.checkRole("user");
        subject.checkRoles("user", "admin");//变参

        //④.通过权限授权，无返回值，授权失败抛出异常
        subject.checkPermission("user:login");
        //ini文件配置了test角色拥有"prefix:*"权限，也就是所有以"prefix"开头的权限
        subject.checkPermission("prefix:123:456:......");
        //ini文件配置了test角色拥有"*:*:suffix"权限，意味着其拥有所有以"suffix"结尾的，一共有三级的权限
        subject.checkPermission("1:2:suffix");
        subject.checkPermission("abc:123:suffix");
        subject.checkPermissions("user:login", "admin:login");//变参
        //subject.checkPermission(Permission permission); 需要Permission接口的实现类对象作参数
        //subject.checkPermissions(Collection<Permission> permissions);
    }
}
