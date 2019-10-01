package com.frt.controller.perotal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.pojo.User;
import com.frt.service.IOrderService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    private static Logger logger= LoggerFactory.getLogger(OrderController.class);
@Autowired
    IOrderService iOrderService;
//创建订单,是在确认收货地址以后调用
@RequestMapping("/addOrder")
@ResponseBody
public ServerResponse addOrder(HttpSession session,Integer shippingId){
    //做判断用户是否登录
    User user=(User)session.getAttribute(Const.CURRENT_USER);
    if(user==null){
        return ServerResponse.createError(Const.NO_UserLOGIN);
    }
    return iOrderService.addOrder(user.getId(),shippingId);
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
      return   iOrderService.getAllOrders(user.getId(),pageNum,pageSize);
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
       return iOrderService.getOrderDetail(user.getId(),orderNo);
    }































//支付订单
    @RequestMapping("/pay")
    @ResponseBody
    public ServerResponse pay(HttpSession session,Long orderNo){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
     String path=session.getServletContext().getRealPath("upload");
    return  iOrderService.pay(orderNo,user.getId(),path);
    }
    //支付宝回调
    @RequestMapping("/alipay_callback.do")
    @ResponseBody
  public  Object callBack(HttpServletRequest request){
     //创建一个存放回调信息的map
        Map map= Maps.newHashMap();
   //获取回调信息集合
        Map<String, String[]> parameterMap = request.getParameterMap();
        //遍历
        for(Iterator iterator=parameterMap.keySet().iterator();iterator.hasNext();){
           String name=(String)iterator.next();
           String []values=parameterMap.get(name);
            //再次遍历values
            String valueStr="";
             for(int i=0;i<values.length;i++){
            //如果有多个值，则用，想隔
                 if(values.length>0){
                     valueStr=values[i]+",";
                 }  //如果只有一个值
                 valueStr=values[i];
             }
            //添加到map
        map.put(name,valueStr);
        }
          // 调用支付宝验证方法,判断回调是否为支付宝所发，还要避免重复通知
        //首先去除sign和signtype
        map.remove("sign");
        map.remove("sign_type");
        try {
            boolean signIsTrue = AlipaySignature.rsaCheckV2(map, Configs.getPublicKey().toString(),
                    "utf-8", Configs.getSignType().toString());
           if(!signIsTrue){
               return ServerResponse.createError("验证不通过！恶意请求！");
           }

        } catch (AlipayApiException e) {
          logger.info("支付宝验证回调异常！",e);
        }
        //继续验证订单，并返回信息给支付宝
        return (iOrderService.callBack(map).isSuccess())?Const.AlipayCallback.RESPONSED_SUCCESS:Const.AlipayCallback.RESPONSED_FAILED;
   }
   //用户支付成功后，查询订单是否支付成功！
    @RequestMapping("/isTradeSuccess")
    @ResponseBody
    public ServerResponse<Boolean> isTradeSuccess(HttpSession session,Long orderNo){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        ServerResponse serverResponse=iOrderService.isTradeSuccess(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
           return ServerResponse.createSuccess(true);
        }
       return ServerResponse.createSuccess(false);
    }

 }
