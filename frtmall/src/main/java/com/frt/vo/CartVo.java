package com.frt.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {
    //购物车显示模型
    private List<CartListVo> cartListVos;
    //总价
     private BigDecimal cartTotalPrice;
     //是否全选
     private boolean allChecked;
     //图片的地址
    private  String imgHost;

    public String getImgHost() {
        return imgHost;
    }

    public void setImgHost(String imgHost) {
        this.imgHost = imgHost;
    }

    public List<CartListVo> getCartListVos() {
        return cartListVos;
    }

    public void setCartListVos(List<CartListVo> cartListVos) {
        this.cartListVos = cartListVos;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

}

