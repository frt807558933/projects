package com.frt.service.serviceImpl;

import com.frt.common.Const;
import com.frt.common.ServerResponse;
import com.frt.dao.ShippingMapper;
import com.frt.pojo.Shipping;
import com.frt.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse addShipping(Shipping shipping) {
        int count = shippingMapper.insert(shipping);
        if (count == 0) {
            return ServerResponse.createError("添加失败");
        }
        return ServerResponse.createSuccess("添加成功", shipping.getId());
    }

    @Override
    public ServerResponse deleteShipping(Integer userId, Integer shippingId) {
        if (userId == null || shippingId == null) {
            return ServerResponse.createError(Const.ILLEGAl);
        }
        int count = shippingMapper.deleteShippingByUidSid(userId, shippingId);
        if (count == 0) {
            return ServerResponse.createError("删除失败！");
        }

        return ServerResponse.createSuccess("删除成功！");
    }

    @Override
    public ServerResponse updateShipping(Shipping shipping) {
        int count = shippingMapper.updateShippingByUidSid(shipping);
        if (count == 0) {
            return ServerResponse.createError("更新失败！");
        }

        return ServerResponse.createSuccess("更新成功！");
    }

    @Override
    public ServerResponse getShippingByUidSid(Integer userId, Integer shippingId) {
        if (userId == null || shippingId == null) {
            return ServerResponse.createError(Const.ILLEGAl);
        }
        Shipping shipping = shippingMapper.getShippingByUidSid(userId, shippingId);
        if (shipping == null) {
            return ServerResponse.createError("查询失败！");
        }

        return ServerResponse.createSuccess("查询成功！", shipping);
    }


    @Override
    public ServerResponse getAllShipping(Integer userId) {
        if (userId == null) {
            return ServerResponse.createError(Const.ILLEGAl);
        }
        List<Shipping> shippingList = shippingMapper.getAllShipping(userId);
        if (shippingList.isEmpty()) {
            return ServerResponse.createError("查询失败！");
        }

        return ServerResponse.createSuccess("查询成功！", shippingList);
    }
}
