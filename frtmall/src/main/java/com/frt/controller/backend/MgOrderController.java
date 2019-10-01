package com.frt.controller.backend;

import com.frt.common.Const;
import com.frt.common.ServerResponse;
import com.frt.pojo.User;
import com.frt.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage")
public class MgOrderController {
    @Autowired
    IOrderService iOrderService;
    //获取订单详情

   //分页获取所有订单
   //发货
@RequestMapping("/send_goods")
    @ResponseBody
    public ServerResponse sendGoods(HttpSession session,Long orderNo,Integer userId){
//做判断用户是否登录
    User user=(User)session.getAttribute(Const.CURRENT_USER);
    if(user==null){
        return  ServerResponse.createError(Const.NO_UserLOGIN);
    }
    //身份判断
    if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
        return  ServerResponse.createError(Const.NO_ADMINPERMISSION);
    }
 return  iOrderService.sendGoods(orderNo,userId);
}
    //取消订单
    @RequestMapping("/cancelOrder")
    @ResponseBody
    public ServerResponse cancelOrder(HttpSession session,Long orderNo,Integer userId){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return iOrderService.cancelOrder(userId,orderNo,Const.OrderStatusEum.CANCELED.getCode());

    }
    @RequestMapping("/getAllOrders")
    @ResponseBody
    //获取所有订单
    public ServerResponse getAllOrders(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum,
                                       @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
//做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return   iOrderService.getAllOrdersNoId(pageNum,pageSize);
    }
    @RequestMapping("/getOrderDetail")
    @ResponseBody
    //查看订单详情
    public ServerResponse getOrderDetail(HttpSession session,Long orderNo){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return iOrderService.getOrderDetail(null,orderNo);
    }

  //根据订单号获取订单
  @RequestMapping("/getOrderByOn")
  @ResponseBody
    public ServerResponse getOrderByOn(HttpSession session,Long orderNo, @RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum,
                                       @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
       return iOrderService.getorderNo(orderNo,pageNum,pageSize);
    }
}
