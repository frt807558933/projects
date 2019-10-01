package com.frt.service;

import com.frt.common.ServerResponse;
import com.frt.pojo.User;
import org.springframework.stereotype.Service;


public interface IUserService {
    ServerResponse<User> login(String username,String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String typeValue,String typeName);
    ServerResponse<String> getQuestion(String username);
    ServerResponse<String> getAnswer(String username,String question,String answer);
    ServerResponse<String> resetPassword(String username,String password);
    ServerResponse<String> loginResetPassword(String oldPassword,String newPassword,User user);
    ServerResponse<User>  updateUser(User user);
}
