package com.frt.service.serviceImpl;

import com.frt.common.Const;
import com.frt.common.ServerResponse;
import com.frt.dao.CartMapper;
import com.frt.pojo.Cart;
import com.frt.pojo.Product;
import com.frt.service.IcartService;
import com.frt.utils.BigDecimalUtils;
import com.frt.utils.DateUtils;
import com.frt.utils.PropertiesUtil;
import com.frt.vo.CartListVo;
import com.frt.vo.CartVo;
import com.google.common.collect.Lists;
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Service("cartServiceImpl")
public class CartServiceImpl implements IcartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProdoctServiceImpl prodoctService;

    //根据商品id和用户id获取购物车
    public ServerResponse getCartByPidUid(Integer userId, Integer productId) {
        if (userId == null) {
            return ServerResponse.createError(Const.ILLEGAl);
        }
        return ServerResponse.createSuccess(cartMapper.getCartByPidUid(userId, productId));
    }
    public ServerResponse addOrUpdate(Cart cart,boolean isOverTake){
        if(cart.getUserId() == null || cart.getProductId() == null || cart.getChecked() == null || cart.getQuantity() == null){
            return ServerResponse.createError(Const.ILLEGAl);
        }
        Product product = (Product) prodoctService.getProductById(cart.getProductId()).getData();
        CartListVo cartListVo= setCartListVo(cart,product);
        cartListVo.setOverTake(isOverTake);
        if(cart.getId()==null){
                //新的插入
                int count = cartMapper.insert(cart);
            if (count == 0) {
                return ServerResponse.createError("新的插入失败");
            }
                return ServerResponse.createSuccess(cartListVo);
        }
        //更新
            int count1 = cartMapper.updateByPrimaryKeySelective(cart);
            if (count1 == 0) {
                return ServerResponse.createError("插入失败");
            }
                return ServerResponse.createSuccess(cartListVo);
    }

    //删除
    public ServerResponse deleteProduct(Integer productId, Integer userId) {
        //判断参数
        if (productId == null || userId == null) {
            return ServerResponse.createError(Const.ILLEGAl);
        }
        int count = cartMapper.deleteProduct(productId, userId);
        if (count == 0) {
            return ServerResponse.createError("删除失败！");
        }
        return ServerResponse.createSuccess("删除成功1");
    }

    @Override
    public ServerResponse dropCart(Integer userId) {
        int count = cartMapper.deleteProduct(null, userId);
        if (count == 0) {
            return ServerResponse.createError("删除失败！");
        }
        return ServerResponse.createSuccess("删除成功1");
    }

    //获取购物车所有商品

    @Override
    public ServerResponse getAllProduct(Integer userId) {
        //获得数据库的所有购物车
         List<Cart> carts = cartMapper.getAllProductByUserId(userId);
        //创建存放每一条展示购物项的容器
        List<CartListVo> listVos = Lists.newArrayList();
        //创建购物车展示模型
        CartVo cartVo = new CartVo();
        BigDecimal CartTotalPrice = new BigDecimal("0");
        if (carts.isEmpty()) {
            return ServerResponse.createError("查询失败1");
        }
        for (Cart cart : carts) {
            Product product = (Product) prodoctService.getProductById(cart.getProductId()).getData();
             CartListVo cartListVo = setCartListVo(cart,product);
            //把商品项展示模型添加到集合
                 listVos.add(cartListVo);
            // 设置每一项的总价
            cartListVo.setTotalPrice(BigDecimalUtils.multiply(cart.getQuantity(), product.getPrice().doubleValue()));
          //判断商品是否选中,计算购物车总价
            if (cartListVo.getChecked() == 1) {
                CartTotalPrice = CartTotalPrice.add(cartListVo.getTotalPrice());
            }
        }
        cartVo.setCartListVos(listVos);
        cartVo.setCartTotalPrice(CartTotalPrice);
       cartVo.setAllChecked(isAllChecked(userId));
       cartVo.setImgHost(PropertiesUtil.getProperty("ftp.server.http.prefix "));
       return ServerResponse.createSuccess("查询成功！", cartVo);
    }

    //购物车的购物项封装到展示的模型购物项中
    private CartListVo setCartListVo(Cart cart,Product product) {
        CartListVo cartListVo = new CartListVo();
        cartListVo.setId(cart.getId());
        cartListVo.setProductId(cart.getProductId());
        cartListVo.setUserId(cart.getUserId());
        cartListVo.setQuantity(cart.getQuantity());
        cartListVo.setChecked(cart.getChecked());
        cartListVo.setUpdateTime(DateUtils.dateToString(cart.getUpdateTime()));
        cartListVo.setCreateTime(DateUtils.dateToString(cart.getCreateTime()));
        cartListVo.setProductMainImg(product.getMainImage());
        cartListVo.setProductSubTitle(product.getSubtitle());
        cartListVo.setProductName(product.getName());
        cartListVo.setProductStock(product.getStock());
        cartListVo.setCurrentUnitPrice(product.getPrice());
        return cartListVo;
    }

    //判断是否全选
    private boolean isAllChecked(Integer userId) {
        int count = cartMapper.isAllCheck(userId);
       return count==0?true:false;
    }

    //更改选中状态
    public  ServerResponse updateChecked(Integer userId,Integer checked,Integer productId){
        if(userId==null||checked==null||productId==null){
            return  ServerResponse.createError(Const.ILLEGAl);
        }

       int count=cartMapper.updateChecked(userId,checked,productId);
        if(count==0){
               return  ServerResponse.createError("更新失败！");
        }
        return  ServerResponse.createSuccess("更新成功");
    }

    @Override
    public ServerResponse allCheckedOrNo(Integer userId,Integer checked) {
        if(userId==null){
            return  ServerResponse.createError(Const.ILLEGAl);
        }
        int count=cartMapper.allCheckedOrNo(userId,checked);
          return ServerResponse.createSuccess("修改成功！",count);
    }

    @Override
    public ServerResponse getProductCount(Integer userId) {
        if(userId==null){
            return  ServerResponse.createError(Const.ILLEGAl);
        }
        List<Integer> count=cartMapper.getProductCount(userId);
        int total=0;
        for(int i:count){
            total=total+i;
        }
        return ServerResponse.createSuccess("查询成功！",total);
    }
}

