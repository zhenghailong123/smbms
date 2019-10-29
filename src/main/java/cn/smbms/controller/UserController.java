package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.role.RoleServiceImpl;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    UserService userService = (UserService) ctx.getBean("userService");
    String path = null;

    //   添加用户
    @RequestMapping("/add")
    public String add(@RequestParam("userCode") String userCode,
                      @RequestParam("userName") String userName,
                      @RequestParam("userPassword") String userPassword,
                      @RequestParam("gender") String gender,
                      @RequestParam("birthday") String birthday,
                      @RequestParam("phone") String phone,
                      @RequestParam("address") String address,
                      @RequestParam("userRole") String userRole, HttpSession session) {
        System.out.println("add()================");
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
        if (userService.add(user)) {
            path = "redirect:/user/query";
        } else {
            path = "add";
        }
        return path;
    }

    //    显示数据
    @RequestMapping("/query")
    public String query(@RequestParam(value = "queryname", required = false) String queryUserName,
                        @RequestParam(value = "queryUserRole", required = false) String temp,
                        @RequestParam(value = "pageIndex", required = false) String pageIndex,
                        Model model) {
        int queryUserRole = 0;
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        System.out.println("queryUserName servlet--------" + queryUserName);
        System.out.println("queryUserRole servlet--------" + queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);
        if (queryUserName == null) {
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")) {
            queryUserRole = Integer.parseInt(temp);
        }

        if (pageIndex != null) {
            try {
                currentPageNo = Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                return "error";
            }
        }
        //总数量（表）
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }


        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryUserName);
        model.addAttribute("queryUserRole", queryUserRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", currentPageNo);
        return "userlist";
    }

    //获取所有角色表数据
    @RequestMapping("/getrolelist")
    @ResponseBody
    public String getRoleList(HttpServletResponse response) {
        RoleService roleService = new RoleServiceImpl();
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
       /* response.setContentType("application/json");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");*/
        //把roleList转换成json对象输出
        return JSONArray.toJSONString(roleList);
//        return JSON.toJSONString(roleList);
    }

    //判断用户账号是否可用
    @RequestMapping("/ucexist")
    @ResponseBody
    public Object userCodeExist(@RequestParam("userCode") String userCode) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            System.out.println("userCode================>" + userCode);
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        } else {
            User user = userService.selectUserCodeExist(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    //删除用户信息
    @RequestMapping("/deluser")
    @ResponseBody
    public String delUser(@RequestParam("uid") String uid){
        Integer delId = 0;
        try{
            delId = Integer.parseInt(uid);
        }catch (Exception e) {
            // TODO: handle exception
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
//			UserService userService = new UserServiceImpl();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }
    //修改用户
    @RequestMapping("/modifyexe")
    public String modify(@RequestParam("uid") String uid,
                         @RequestParam("userName") String userName,
                         @RequestParam("gender") String gender,
                         @RequestParam("birthday") String birthday,
                         @RequestParam("phone") String phone,
                         @RequestParam("address") String address,
                         @RequestParam("userRole") String userRole, HttpSession session){
        User user = new User();
        user.setId(Integer.valueOf(uid));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

//		UserService userService = new UserServiceImpl();
        if(userService.modify(user)){
            path = "redirect:query";
        }else{
            path = "usermodify";
        }
        return path;
    }
    //以用户id获取用户密码
    @RequestMapping("/pwdmodify")
    @ResponseBody
    public String getPwdByUserId(@RequestParam("oldpassword") String oldpassword, HttpSession session){
        Object o = session.getAttribute(Constants.USER_SESSION);
        Map<String, String> resultMap = new HashMap<String, String>();
        if(null == o ){//session过期
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
            resultMap.put("result", "error");
        }else{
            String sessionPwd = ((User)o).getUserPassword();
            if(oldpassword.equals(sessionPwd)){
                resultMap.put("result", "true");
            }else{//旧密码输入不正确
                resultMap.put("result", "false");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }
    //修改密码
    @RequestMapping("/savepwd")
    public String updatePwd(@RequestParam("newpassword") String newpassword,Model model,HttpSession session){
        Object o = session.getAttribute(Constants.USER_SESSION);
        boolean flag = false;
        if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
//			UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User)o).getId(),newpassword);
            if(flag){
                model.addAttribute(Constants.SYS_MESSAGE, "修改密码成功,请退出并使用新密码重新登录！");
                session.removeAttribute(Constants.USER_SESSION);//session注销
            }else{
                model.addAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
            }
        }else{
            model.addAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
        }
        return "pwdmodify";
    }
    //以用户id获取用户数据
    @RequestMapping("/view")
    public String getUserById(@RequestParam("uid") String uid,@RequestParam("path") String path,Model model){
        if(!StringUtils.isNullOrEmpty(uid)){
            //调用后台方法得到user对象
            User user = userService.getUserById(uid);
            model.addAttribute("user", user);
        }
        return path;
    }

    //跳转到添加用户表
    @RequestMapping("/toAdd")
    public String toadd() {
        return "useradd";
    }

    //跳转到修改密码页面
    @RequestMapping("/toPwdmodify")
    public String topwdmodify() {
        return "pwdmodify";
    }

    @RequestMapping("/login")
    public String login(){
        return "init/login";
    }
}
