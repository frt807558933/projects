package com.frt.controller.perotal;

import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.common.TokenCache;
import com.frt.pojo.User;
import com.frt.service.IUserService;
import com.frt.service.serviceImpl.UserServiceImp;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    IUserService iUserService;
    //登录方法
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
public ServerResponse<User> login(String username, String password, HttpSession session){
     //调用service
    ServerResponse<User> serverResponse= iUserService.login(username,password);
    //调用响应的是否成功
     if(serverResponse.isSuccess()){
              session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
     }
        return  serverResponse;
   }
   //登出方法
    @RequestMapping(value = "loginOut")
    @ResponseBody
       public ServerResponse<User> loginOut(HttpSession session){
          session.removeAttribute(Const.CURRENT_USER);
          return ServerResponse.createSuccess("登出成功！");
   }
   //注册方法
    @RequestMapping(value = "register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
    //调用service
    return  iUserService.register(user);
       }
    //检验用户名，邮箱是否存在方法
    @RequestMapping(value = "loginCheck",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String typeName,String typeValue){
        //调用service
       return iUserService.checkValid(typeName,typeValue);
    }
    //获取用户信息方法
    @RequestMapping(value = "getUserInfo",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<User> getUserInfo(HttpSession httpSession){
   User user=(User)httpSession.getAttribute(Const.CURRENT_USER);
   if(user==null){
    return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return ServerResponse.createSuccess("获取成功!",user);
   }

 //返回找回密码的问题的方法
    @RequestMapping(value = "getQuestion",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getQuestion(String username){
     //调用service
     return   iUserService.getQuestion(username);
    }
    //验证问题是否正确
    @RequestMapping(value = "getAnswer",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getAnswer(String username,String question,String answer){
        //调用service
      return   iUserService.getAnswer(username,question,answer);
    }
    //重置密码的方法
    @RequestMapping(value = "resetPassword",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> resetPassword(String username,String password){
        //调用service
        return  iUserService.resetPassword(username,password);
    }
    //已登录状态修改密码
    @RequestMapping(value = "loginResetPassword",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> loginResetPassword(String oldPassword,String newPassword,HttpSession session){
        //做判断用户是否登录
       User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
             return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        // /调用service
           return  iUserService.loginResetPassword(oldPassword,newPassword,user);
    }
    //更新用户信息
    @RequestMapping(value = "updateUser",method = RequestMethod.POST)
    @ResponseBody
   public ServerResponse<User> updateUser(HttpSession session,User changeUser){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //调用更新
        //传入的user没有id,此处赋值
        changeUser.setId(user.getId());
        ServerResponse<User> serverResponse= iUserService.updateUser(changeUser);
          if(serverResponse.isSuccess()){
              //更新成功，存入session
           session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
          }
         //更新失败返回信息
       return  serverResponse;
    }

}