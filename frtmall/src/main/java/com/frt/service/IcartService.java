package com.frt.service;

import com.frt.common.ServerResponse;
import com.frt.pojo.Cart;



public interface IcartService {
    ServerResponse getCartByPidUid(Integer userId,Integer productId);
    ServerResponse addOrUpdate(Cart cart,boolean isOverTake);
    ServerResponse deleteProduct(Integer productId,Integer userId);
    ServerResponse dropCart(Integer userId);
    ServerResponse getAllProduct(Integer userId);
    ServerResponse updateChecked(Integer userId,Integer checked,Integer productId);
    ServerResponse allCheckedOrNo(Integer userId,Integer checked);
    ServerResponse  getProductCount(Integer userId);

}
