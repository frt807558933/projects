package com.frt.service.serviceImpl;

import com.frt.common.Const;
import com.frt.common.ServerResponse;
import com.frt.common.TokenCache;
import com.frt.dao.UserMapper;
import com.frt.pojo.User;
import com.frt.service.IUserService;
import com.frt.utils.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.sax.SAXSource;
import java.net.StandardSocketOptions;
import java.util.UUID;
@Service("iUserService")
public class UserServiceImp implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Override//登录方法
    public ServerResponse<User> login(String username,String password) {
        //先检查下用户是否存在
       int count= userMapper.checkUser(username);
        if(count==0){
           return ServerResponse.createError("用户不存在！");
        }
      //存储时密码已经加密，此处需要加密登录
       password=MD5Util.MD5EncodeUtf8(password);
       //调用登录
        User user=userMapper.login(username,password);
         //存在判断密码
         if(user==null){
             return  ServerResponse.createError("密码错误");
         }
         //密码置空
         user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createSuccess("登录成功！",user);
    }
    //注册方法
    public ServerResponse<String> register(User user){
        //判断用户名是否存在，调用写好的检验方法
        ServerResponse<String> stringServerResponse=this.checkValid(Const.USERNAME,user.getUsername());
        if(!stringServerResponse.isSuccess()){
           return  stringServerResponse;
        }
        //判断用户邮箱是否存，调用写好的检验方法
       ServerResponse<String> stringServerResponse1=this.checkValid(Const.EMAIL,user.getEmail());
        if(!stringServerResponse1.isSuccess()){
            return  stringServerResponse1;
        }
        //设置用户的角色
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5密码加密,采用utf-8编码加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //调用dao
        int count2=userMapper.insert(user);
          if(count2==0){
            ServerResponse.createError("创建失败！");
        }
        return  ServerResponse.createSuccess("创建成功！");
    }


    //一个公共的可以使用的检验方法
    public ServerResponse<String> checkValid(String typeName,String typeValue){
         //根据传入的值此处做一个大判断，区分是否为空，isBlank(),有空格则为false,符合判断条件
        if(StringUtils.isNotBlank(typeValue)){
             //不为空继续判断，判断是否为邮箱
                 if(Const.EMAIL.equals(typeName)){
                     int count= userMapper.checkEmail(typeValue);
                           if(count>0){
                                  return ServerResponse.createError("邮箱已存在");
                           }
                 } //判断是否为用户名
                 if(Const.USERNAME.equals(typeName)){
                           int count1= userMapper.checkUser(typeValue);
                           if(count1>0){
                                      return ServerResponse.createError("用户已存在");
                           }
                 }
         }
        else {
            return ServerResponse.createError("没有输入参数");
        }
       return  ServerResponse.createSuccess("该"+typeName+"不存在！");
    }
    //找回问题的方法
    @Override
    public ServerResponse<String> getQuestion(String username) {
        //先检查找回密码的用户是否存在
        ServerResponse<String> stringServerResponse=this.checkValid(Const.USERNAME,username);
        if(!stringServerResponse.isSuccess()){
            ServerResponse.createError("用户名不存在！");
        }
        //调用dao
       String question=userMapper.getQuestion(username);
        if(StringUtils.isNotBlank(question)){
          return ServerResponse.createSuccess(question);
        }
          return ServerResponse.createError("你还未设置问题");
    }
  //验证答案是否正确
    @Override
    public ServerResponse<String> getAnswer(String username,String question, String answer) {
        //调用dao
        int count=userMapper.getAnswer(username,question,answer);
        if(count>0){
          return  ServerResponse.createSuccess("验证成功");
        }
                return  ServerResponse.createError("答案错误！");
    }
    //重置密码
    public ServerResponse<String> resetPassword(String username,String password){
        //先检查下用户是否存在
        ServerResponse<String> stringServerResponse=this.checkValid(Const.USERNAME,username);
        if(!stringServerResponse.isSuccess()){
            ServerResponse.createError("用户不存在！");

        }
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        int count=  userMapper.updatePassword(username,md5Password);
           if(count>0){
               return ServerResponse.createSuccess("修改成功");
            }
        return ServerResponse.createError("修改失败");
    }

    @Override
    public ServerResponse<String> loginResetPassword(String oldPassword, String newPassword, User user) {

        String md5Password=MD5Util.MD5EncodeUtf8(oldPassword);
        String md5Password1=MD5Util.MD5EncodeUtf8(newPassword);
     //先调用checkPassword(),判断，旧密码是否正确
        int count=userMapper.checkPassword(md5Password,user.getId());
          if(count==0){
                  ServerResponse.createError("输入的旧密码错误");
          }
          //调用dao
           user.setPassword(md5Password1);
         int count1=  userMapper.loginResetPassword(md5Password1,user.getId());
            if(count1==0){
                      ServerResponse.createError("更新失败，请重新操作");
                     }

           return  ServerResponse.createSuccess("更改成功！");
    }

    @Override
    public ServerResponse<User> updateUser(User user) {
        //先判断传过来的用户信息,邮箱是否已存在，用户名不可更改
        int count= userMapper.checkUser(user.getEmail());
        if(count>0){
            return ServerResponse.createError("邮箱已存在,不可使用！");
        }
     // 调用dao,传入新的信息
        int count1= userMapper.updateByPrimaryKeySelective(user);
          if(count1==0){
              ServerResponse.createError("更新失败");
          }
           return  ServerResponse.createSuccess("更新成功！",user);
    }

}
