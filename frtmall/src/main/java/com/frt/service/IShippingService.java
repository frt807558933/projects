package com.frt.service;

import com.frt.common.ServerResponse;
import com.frt.pojo.Shipping;

public interface IShippingService {
    ServerResponse addShipping(Shipping shipping);
    ServerResponse deleteShipping(Integer userId,Integer shippingId);
    ServerResponse updateShipping(Shipping shipping);
    ServerResponse getShippingByUidSid(Integer userId,Integer shippingId);
    ServerResponse getAllShipping(Integer userId);
}
