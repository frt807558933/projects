package com.frt.controller.perotal;

import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.pojo.Shipping;
import com.frt.pojo.User;
import com.frt.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    IShippingService iShippingService;
    @RequestMapping("/addShipping")
    @ResponseBody
    public ServerResponse addShipping(HttpSession session, Shipping shipping){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        shipping.setUserId(user.getId());
        return   iShippingService.addShipping(shipping);
    }
    @RequestMapping("/deleteShipping")
    @ResponseBody
    public ServerResponse deleteShipping(HttpSession session,Integer shippingId){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return   iShippingService.deleteShipping(user.getId(),shippingId);
    }
    @RequestMapping("/updateShipping")
    @ResponseBody
    public ServerResponse updateShipping(HttpSession session,Shipping shipping){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        shipping.setUserId(user.getId());
         return   iShippingService.updateShipping(shipping);
    }
    @RequestMapping("/getShippingByUidSid")
    @ResponseBody
    public ServerResponse getShippingByUidSid(HttpSession session,Integer shippingId ){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return   iShippingService.getShippingByUidSid(user.getId(),shippingId);
    }
    @RequestMapping("/getAllShipping")
    @ResponseBody
    public ServerResponse getAllShipping(HttpSession session){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return   iShippingService.getAllShipping(user.getId());
    }
}
