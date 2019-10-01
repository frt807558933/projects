package com.frt.service;

import com.frt.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Long orderNo,Integer userId,String path);
    ServerResponse callBack(Map map);
    ServerResponse isTradeSuccess(Integer userId,Long orderNo);
    ServerResponse addOrder(Integer userId,Integer shippingId);
    ServerResponse cancelOrder(Integer userId,Long orderNo,Integer status);
    ServerResponse getAllOrders(Integer userId,Integer pageNum,Integer pageSize);
    ServerResponse getOrderDetail(Integer userId,Long orderNo);
    ServerResponse sendGoods(Long orderNo,Integer userId);
    ServerResponse getAllOrdersNoId(Integer pageNum,Integer pageSize);
    ServerResponse getorderNo(Long orderNo,Integer pageNum, Integer pageSize);
}
