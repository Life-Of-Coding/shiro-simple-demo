package com.lifeofcoding.shiro.controller;

import com.lifeofcoding.shiro.pojo.User;
import com.lifeofcoding.shiro.realm.MyEncryptedRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    private MyEncryptedRealm realm;

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
        //加密次数
        realm.setHashIterations(3);
        //添加账号
        try {
            realm.addAccount(user);
        }catch (Exception e){
            return e.getMessage();
        }
        return "Add account \"" + user.getUsername() + "\" succeeded";
    }

    /**
     * 测试已登录的用户是否拥有某角色
     * @param role 角色名
     * @return 返回信息
     * api示例: GET /testRole?role=admin
     * */
    @ResponseBody
    @RequestMapping(value = "testRole",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public String testRole(String role){
        if (null == role){
            return "no input";
        }
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole(role)){
            return "user \"" + subject.getPrincipal()+"\" has role \"" + role +"\"";
        }
        return  "user \"" + subject.getPrincipal()+"\" do not have role \"" + role + "\"";
    }

    /**
     * 测试已登录的用户是否拥有某权限
     * @param permission 权限
     * @return 返回信息
     * api示例: GET /testPermission?permission=user:delete
     * */
    @ResponseBody
    @RequestMapping(value = "testPermission",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public String testPermission(String permission){
        if (null == permission){
            return "no input";
        }
        Subject subject = SecurityUtils.getSubject();
        if (subject.isPermitted(permission)){
            return "user \"" + subject.getPrincipal()+"\" has permission \"" + permission +"\"";
        }
        return  "user \"" + subject.getPrincipal()+"\" do not have permission \"" + permission + "\"";
    }

    /**
     * 添加权限
     * api示例: GET /addPermissions?role=admin&permissions=user:delete&permissions=user:modify
     * */
    @ResponseBody
    @RequestMapping(value = "addPermissions",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public String addPermissions(String role, String...permissions){
        if (role==null || CollectionUtils.isEmpty(CollectionUtils.asSet(permissions))){
            return "rolename or permissions can not be empty";
        }
        realm.addPermissions(role, CollectionUtils.asSet(permissions));
        return null;
    }
}
