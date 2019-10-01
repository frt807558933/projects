package com.frt.controller.perotal;

import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.pojo.User;
import com.frt.service.serviceImpl.ProdoctServiceImpl;
import com.frt.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProdoctServiceImpl prodoctService;
    //前台商品展示
    //根据商品分类获取所有商品，商品状态必须在售
    @RequestMapping(value = "getProductByCid",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductByCid(HttpSession session, Integer categoryId,
                                                           @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                           @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }

       return prodoctService.getProductByCid(categoryId,pageNum,pageSize);
    }
    //根据商品名称搜取所有商品
    @RequestMapping(value = "getProductByNameId",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse getProductByNameId(HttpSession session, String productName,Integer productId,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //做判断用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createError(Const.NO_UserLOGIN);
        }
        return  prodoctService.getProductByNameId(productName,productId,pageNum,pageSize);

    }
    //根据id获取商品详情
    public ServerResponse getProductById(Integer id){
    return  prodoctService.getProductById(id);
    }
}
