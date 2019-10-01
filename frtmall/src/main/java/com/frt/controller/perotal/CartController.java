package com.frt.controller.perotal;

import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.pojo.Cart;
import com.frt.pojo.Product;
import com.frt.pojo.User;
import com.frt.service.IcartService;
import com.frt.service.serviceImpl.ProdoctServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    IcartService icartService;
    @Autowired
    ProdoctServiceImpl prodoctService;
    //是否超出标识
    boolean isOverTake=false;
    //添加商品都购物车
    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse add(HttpSession session,Cart cart){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }

  //购物车直接从数据库查询,先从数据库获得
      ServerResponse serverResponse= icartService.getCartByPidUid(user.getId(),cart.getProductId());
        Cart resultCart=(Cart) serverResponse.getData();
        if(resultCart==null){
         //创建一个购物车
            Cart newCart=new Cart();
             newCart.setProductId(cart.getProductId());
             newCart.setUserId(user.getId());
            //判断数量是否合法
              ifQutity(cart);
             newCart.setQuantity(cart.getQuantity());
            newCart.setChecked(cart.getChecked());
           ServerResponse serverResponse1= icartService.addOrUpdate(newCart,isOverTake);
           return ServerResponse.createSuccess("新建添加成功1",serverResponse1.getData());
        }
         //把商品的信息，数量，放进该购物车
        resultCart.setQuantity(resultCart.getQuantity()+cart.getQuantity());
        //判断数量是否超出库存
           ifQutity(resultCart);
        resultCart.setUpdateTime(null);
             ServerResponse response = icartService.addOrUpdate(resultCart,isOverTake);
        return ServerResponse.createSuccess("添加成功1",response.getData());
    }
    private void ifQutity(Cart cartm){
        //判断数量是否合法
        Product product=(Product) prodoctService.getProductById(cartm.getProductId()).getData();
        if(cartm.getQuantity()>product.getStock()){
               isOverTake=true;
            cartm.setQuantity(product.getStock());
        }
    }
    //删除购物车里的商品
    @RequestMapping("/delete")
    @ResponseBody
    public ServerResponse deleteProduct(HttpSession session,Integer productId){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
     ServerResponse serverResponse= icartService.deleteProduct(productId,user.getId());
        return  serverResponse;
    }
    //清空购物车
    @RequestMapping("/drop")
    @ResponseBody
    public ServerResponse dropCart(HttpSession session){
        //到此已经必须登录，所以无需判断
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse serverResponse = icartService.dropCart(user.getId());
        return  serverResponse;
    }
    // 获取购物车的所有商品,并计算出勾选的总价格
    @RequestMapping("/getAllProducts")
    @ResponseBody
    public ServerResponse getAllProduct(HttpSession session){
//做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return  icartService.getAllProduct(user.getId());
    }
    //更改购物车商品选中状态
    @RequestMapping("/updateChecked")
    @ResponseBody
    public ServerResponse updateChecked(HttpSession session,Integer checked,Integer productId) {
//做判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
         return icartService.updateChecked(user.getId(),checked,productId);
    }
    //全选货否
    @RequestMapping("/allCheckedOrNo")
    @ResponseBody
    public ServerResponse allCheckedOrNo(HttpSession session,Integer checked) {
//做判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return icartService.allCheckedOrNo(user.getId(),checked);
    }
 //获得购物车商品总数

    @RequestMapping("/getProductCount")
    @ResponseBody
    public ServerResponse  getProductCount(HttpSession session) {
//做判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return icartService.getProductCount(user.getId());
    }
}
