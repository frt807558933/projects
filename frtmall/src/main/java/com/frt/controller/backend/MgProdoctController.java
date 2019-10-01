package com.frt.controller.backend;

import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.pojo.Product;
import com.frt.pojo.User;
import com.frt.service.IProdoctService;
import com.frt.service.serviceImpl.FileServiceImpl;
import com.frt.service.serviceImpl.ProdoctServiceImpl;
import com.google.common.collect.Maps;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class MgProdoctController {
    @Autowired
    ProdoctServiceImpl prodoctService;
    @Autowired
    FileServiceImpl fileService;
    //保存或更新商品
    @RequestMapping("save")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
        }
        return  prodoctService.productSave(product) ;
    }
    //设置更新商品状态
    @RequestMapping("setStatus")
    @ResponseBody
    public ServerResponse setStatus(HttpSession session,Integer productId,Integer status){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
        }

        return  prodoctService.setStatus(productId,status) ;
    }
    //获取商品详情
        @RequestMapping("productDetail")
    @ResponseBody
    public ServerResponse productDetail(HttpSession session,Integer productId){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
        }

        return  prodoctService.productDetail(productId) ;
    }

    //获取商品列表
    @RequestMapping("getList")
    @ResponseBody
    public ServerResponse getList(HttpSession session,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
        }

        return  prodoctService.getList(pageNum,pageSize) ;
    }
    //获取商品列表
    @RequestMapping("getListByNameId")
    @ResponseBody
    public ServerResponse getListByNameId(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
        }

        return  prodoctService.getListByNameId(productName,productId,pageNum,pageSize);
    }
    //商品主图片上传
    @RequestMapping("uploadImg")
    @ResponseBody
  public ServerResponse uploadImg(HttpSession session, @RequestParam(value = "mImg",required = false) MultipartFile file){
     //做判断用户是否登录
      User user=(User)session.getAttribute(Const.CURRENT_USER);
       if(user==null){
          return  ServerResponse.createError(Const.NO_UserLOGIN);
      }
      //身份判断
      if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
          return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
      }
      //获取上传文件的真实路径
      String path=session.getServletContext().getRealPath("/upload");
      //调用fileService
       return fileService.uploadImg(path,file);
  }
    //商品副图片上传
    @RequestMapping("uploadSubImg")
    @ResponseBody
    public Map uploadSubImg(HttpSession session, @RequestParam(value = "subImg",required = false) MultipartFile file){
       Map map= Maps.newHashMap();

      //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            map.put("msg",Const.NO_UserLOGIN);
            map.put("Success",false);
            return  map;
        }
        //身份判断
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            map.put("msg",Const.NO_ADMINPERMISSION);
            map.put("Success",false);
            return map;
        }
        //获取上传文件的路径
        String path=session.getServletContext().getRealPath("/upload");
        //调用fileService
      if(fileService.uploadImg(path,file).isSuccess()){
          map.put("msg","上传成功！");
          map.put("Success",true);
          map.put("url",path);
          return map;
      }
        map.put("msg","上传失败");
        map.put("Success",false);
        return map;
    }

}
