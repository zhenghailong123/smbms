package cn.smbms.controller;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;

import cn.smbms.tools.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    UserService userService = (UserService) ctx.getBean("userService");
    String path = null;

    @RequestMapping("/doLogin")
    public String login(@RequestParam("userCode")  String userCode,
                       @RequestParam("userPassword") String userPassword,HttpSession session) {
        System.out.println("login ============ ");
        //获取用户名和密码
        //调用service方法，进行用户匹配
        User user = userService.login(userCode,userPassword);
        if(null != user){//登录成功
            //放入session
            session.setAttribute(Constants.USER_SESSION, user);
            //页面跳转（frame.jsp）
            path="frame";
        }else{
            //页面跳转（login.jsp）带出提示信息--转发
            session.setAttribute("error", "用户名或密码不正确");
            path="init/login";
        }
        return path;
    }
    //登录页面
    @RequestMapping("/init")
    public String init(){
        return "init/login";
    }
}
