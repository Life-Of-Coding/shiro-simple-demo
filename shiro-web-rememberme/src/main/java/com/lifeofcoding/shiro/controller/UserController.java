package com.lifeofcoding.shiro.controller;

import com.lifeofcoding.shiro.pojo.User;
import com.lifeofcoding.shiro.realm.MyEncryptedJdbcRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Controller
public class UserController {



    /**
     * spring配置文件文件已经配置了Realm,直接用autowired注解从spring管理的bean中取得实例，然后注入依赖；
     * 因为用户、角色、权限、加密信息等都封装到了此Realm类,因而需要用到该实例，也可直接用DAO进行CRUD操作
     * */
    @Autowired
    private MyEncryptedJdbcRealm realm;

    /**
     * 用户登录
     * @param user 用户信息，包括用户名(username)和密码(password)
     * api示例: POST /subLogin?username=java&password=123
     * */
    @ResponseBody
    @RequestMapping(value = "/subLogin",method = RequestMethod.POST,produces= {"application/json;charset=UTF-8"})
    public String subLogin(User user){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword());
        try {
            //设置自动登录
            token.setRememberMe(user.getRememberMe());
            subject.login(token);
        }catch (Exception e){
            return e.getMessage();
        }
        return "\""+subject.getPrincipal().toString()+"\""+"登陆成功";
    }

    /**
     * 用户注册
     * @param user 用户信息，包括:用户名(username)、密码(password)、角色(roles)(可选)
     * @return 返回注册信息
     * api示例: POST /register?username=java&password=123&roles=admin&roles=user  可通过指定多个roles传入roles数组
     * */
    @ResponseBody
    @RequestMapping(value = "/register",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    public String register(User user){
        //配置realm设置加密方式
        realm.setAlgorithmName("MD5");
        realm.setHashIterations(3);
        //添加账号
        try {
            realm.addAccount(user);
        }catch (Exception e){
            System.out.println("Exception:"+e.getMessage());
            if (e instanceof MyEncryptedJdbcRealm.UserExistException){
                return "user \"" + user.getUsername() +"\" already exists";
            }
            return "ERROR";
        }
        return "Add account \"" + user.getUsername() + "\" succeeded";
    }


    /**
     * 添加权限
     * @param role 角色名
     * @return 返回信息
     * api示例: GET /addPermissions?role=admin&permissions=user:delete&permissions=user:modify
     * */
    @ResponseBody
    @RequestMapping(value = "addPermissions",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public String addPermissions(String role, Set<String> permissions){
        if (role==null || CollectionUtils.isEmpty(CollectionUtils.asSet(permissions))){
            return "rolename or permissions can not be empty";
        }
        try {
            realm.addPermissions(role, permissions);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "ERROR";
        }
        return "add permission success";
    }


    @RequiresRoles("user")
    @ResponseBody
    @RequestMapping(value = "testRole",method = RequestMethod.GET)
    public String testRole(){
        return "has role: admin";
    }

    /**包含其中一种角色即可*/
    @RequiresRoles({"user","admin"})
    @ResponseBody
    @RequestMapping(value = "testRoles",method = RequestMethod.GET)
    public String testRoles(){
        return "has roles: admin";
    }

    @RequiresPermissions("user:delete")
    @ResponseBody
    @RequestMapping(value = "testPermission",method = RequestMethod.GET)
    public String testPermission(){return "has permission: user:delete";}

    /**包含其中一种权限即可*/
    @RequiresPermissions({"user:delete","user:login","ahha"})
    @ResponseBody
    @RequestMapping(value = "testPermissions",method = RequestMethod.GET)
    public String testPermissions(){return "has permissions: user:delete";}


}
