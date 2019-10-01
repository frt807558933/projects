package com.frt.controller.backend;

import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.pojo.Category;
import com.frt.pojo.User;
import com.frt.service.serviceImpl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/manage/category")
public class MgCategoryController {
    @Autowired
    CategoryServiceImpl categoryService;
       //添加分类
    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse<Category> addCategory(HttpSession session,String categoryName, @RequestParam(value = "parentId",defaultValue ="0") Integer parentId){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
        }
        //调用service
        return   categoryService.addCategory(categoryName,parentId);
 }
       //修改分类名称
       @RequestMapping("/setCategoryName")
       @ResponseBody
    public ServerResponse<Category> setCategoryName(HttpSession session,String newCategoryName,Integer id){
           //做判断用户是否登录
           User user=(User)session.getAttribute(Const.CURRENT_USER);
           if(user==null){
               return  ServerResponse.createError(Const.NO_UserLOGIN);
           }
           //身份判断
           if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
               return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
           }
        return  categoryService.setCategoryName(newCategoryName,id);
    }
      //获取当前节点平级分类
      @RequestMapping("/getAllCategory")
      @ResponseBody
    public ServerResponse<List<Category>> getAllCategory(HttpSession session,@RequestParam(value = "parent_id",defaultValue = "0") Integer  parent_id){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
        }
        return   categoryService.getAllCategory( parent_id);
    }
      //获取父节点下所有子节点
      @RequestMapping("/getDeepAll")
      @ResponseBody
      public ServerResponse<Set<Category>> getDeepAll(HttpSession session,Integer id){
          //做判断用户是否登录
          User user=(User)session.getAttribute(Const.CURRENT_USER);
          if(user==null){
              return  ServerResponse.createError(Const.NO_UserLOGIN);
          }
          //身份判断
          if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
              return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
          }

          return   categoryService.getDeepAll(id);
      }
}

