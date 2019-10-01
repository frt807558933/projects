package com.frt.service;

import com.frt.common.ServerResponse;
import com.frt.pojo.Product;
import com.frt.vo.ProductDetailVo;

public interface IProdoctService {
    //保存商品
    ServerResponse productSave(Product product);
    //设置商品状态
    ServerResponse<String> setStatus(Integer productId,Integer status);
    //获取商品详情
    ServerResponse<ProductDetailVo>productDetail(Integer productId);
    //获取所有商品列表，分页显示
    ServerResponse getList(int pageNum,int pageSize);
    //根据商品名称或商品id获取商品
    ServerResponse getListByNameId(String productName,Integer productId,int pageNum,int pageSize);
    //前台商品展示
    //根据商品分类获取所有商品，或名字搜取，商品状态必须在售


}
