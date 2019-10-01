package com.frt.service.serviceImpl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.Main;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.frt.common.Const;
import com.frt.common.ServerResponse;
import com.frt.dao.*;
import com.frt.pojo.*;
import com.frt.service.IOrderService;
import com.frt.utils.BigDecimalUtils;
import com.frt.utils.DateUtils;
import com.frt.utils.FtpUtils;
import com.frt.utils.PropertiesUtil;
import com.frt.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service("orderServiceImpl")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    PayInfoMapper payInfoMapper;
    @Autowired
    ShippingServiceImpl shippingService;
    @Autowired
    CartServiceImpl cartService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ShippingMapper shippingMapper;
    private static Log log = LogFactory.getLog(OrderServiceImpl.class);
//
     Long orderNo=getOrderNo();

    @Override
    @Transactional
    public ServerResponse addOrder(Integer userId, Integer shippingId) {
        //获得收货信息，购物车的信息
       Shipping shipping= (Shipping)shippingService.getShippingByUidSid(userId,shippingId).getData();
       //获得购物车中选中的商品以生成支付的商品
        CartVo cartVo=(CartVo) cartService.getAllProduct(userId).getData();
       List<CartListVo> cartListVos=cartVo.getCartListVos();
       //存放订单项
       List<OrderItem> orderItems=Lists.newArrayList();
        BigDecimal stotalPrice=new BigDecimal("0");
         for (CartListVo cartListVo:cartListVos){
             if(cartListVos.isEmpty()){
                   return ServerResponse.createError("购物车为空！");
             }
             if(cartListVo.getChecked()==1){
                  //同步商品数量
                 List<Product> productList=(List<Product>) productMapper.getListByNameId(null,cartListVo.getProductId());
                 Product product=productList.get(0);
                 //商品库存-商品购买数量
                 if(cartListVo.getQuantity()>product.getStock()){
                     return  ServerResponse.createError("商品"+product.getName()+"库存不足！还有"+product.getStock()+"件");
                 }
                 product.setStock(product.getStock()-cartListVo.getQuantity());
                 productMapper.updateByPrimaryKeySelective(product);
                 //封装到订单项
                 // 再添加到订单项集合
                 orderItems.add(setOrderItem(cartListVo));
                 //计算一个购物车总价，即订单总价
                 stotalPrice=BigDecimalUtils.add(cartListVo.getTotalPrice().doubleValue(),stotalPrice.doubleValue());

             }

         }
        //封装到order,并添加到数据库
        Order order=setOrder(stotalPrice,shippingId,userId);
        if(order==null){
            //此处需要添加事务的一致性！记得！
       return  ServerResponse.createError("订单已存在！请勿重复创建！");
        }
        //订单项批量插入到数据库
        orderItemMapper.addManyToo(orderItems);

         //返回给前台VO
        ShippingVo shippingVo=setShippingVo(shipping);
        OrderVo orderVo=setOrderVo(order,orderItems,shippingVo);
        return ServerResponse.createSuccess(orderVo);
 }
 //封装shippingVO
    private ShippingVo setShippingVo(Shipping shipping){
        ShippingVo shippingVo=new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setUserId(shipping.getUserId());

        return shippingVo;
    }
    //封装orderItemVo
    private OrderItemVo setOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo=new OrderItemVo();
          orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
          orderItemVo.setOrderNo(orderItem.getOrderNo());
         orderItemVo.setProductImage(orderItem.getProductImage());
         orderItemVo.setProductName(orderItem.getProductName());
         orderItemVo.setQuantity(orderItem.getQuantity());
         orderItemVo.setTotalPrice(orderItem.getTotalPrice());
         orderItemVo.setProductId(orderItem.getProductId());
         orderItemVo.setCreateTime(DateUtils.dateToString(orderItem.getCreateTime()));
         orderItemVo.setUpdateTime(DateUtils.dateToString(orderItem.getUpdateTime()));
            return orderItemVo;
    }
    //封装orderVo
    private OrderVo setOrderVo(Order order,List<OrderItem> orderItems,ShippingVo shippingVo){
        OrderVo orderVo=new OrderVo();
      orderVo.setOrderNo(order.getOrderNo());
      orderVo.setPayment(order.getPayment());
      orderVo.setPaymentTime(DateUtils.dateToString(order.getPaymentTime()));
      orderVo.setSendTime(DateUtils.dateToString(order.getSendTime()));
      orderVo.setEndTime(DateUtils.dateToString(order.getEndTime()));
      orderVo.setCloseTime(DateUtils.dateToString(order.getCloseTime()));
      orderVo.setPostage(order.getPostage());
      orderVo.setPaymentType(order.getPaymentType());
      orderVo.setPaymentTypeDesc(Const.PAYTYPE.ONLINE_PAY.getDesc());
      orderVo.setShippingId(order.getShippingId());
      orderVo.setStatus(order.getStatus());
      String desc=null;
      switch (order.getStatus()){
          case 0: desc= Const.OrderStatusEum.CANCELED.getDesc();
              break;
          case 10:desc=Const.OrderStatusEum.NO_PAY.getDesc();
              break;
          case 20:desc=Const.OrderStatusEum.PAYD.getDesc();
              break;
          case 30:desc=Const.OrderStatusEum.SHIPPED.getDesc();
              break;
          case 40:desc=Const.OrderStatusEum.ORDER_FINISHED.getDesc();
              break;
          case 50:desc=Const.OrderStatusEum.ORDER_CLOSED.getDesc();

      }
      orderVo.setStatusDesc(desc);
      orderVo.setUserId(order.getUserId());
      orderVo.setCreateTime(DateUtils.dateToString(order.getCreateTime()));
      orderVo.setUpdateTime(DateUtils.dateToString(order.getUpdateTime()));
      orderVo.setImgHost(PropertiesUtil.getProperty("ftp.server.http.prefix "));
   List<OrderItemVo> orderVoList=Lists.newArrayList();
      for(OrderItem orderItem: orderItems){
          orderVoList.add(setOrderItemVo(orderItem));
      }
        orderVo.setList(orderVoList);
        orderVo.setReceiverName(shippingVo.getReceiverName());
        orderVo.setShippingVo(shippingVo);
        return orderVo;
    }
    // 封装到订单项，再添加到订单
    private OrderItem setOrderItem(CartListVo cartListVo){
        OrderItem orderItem=new OrderItem();
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(cartListVo.getProductId());
        orderItem.setCurrentUnitPrice(cartListVo.getCurrentUnitPrice());
        orderItem.setProductImage(cartListVo.getProductMainImg());
        orderItem.setProductName(cartListVo.getProductName());
        orderItem.setQuantity(cartListVo.getQuantity());
        orderItem.setTotalPrice(cartListVo.getTotalPrice());
        orderItem.setUserId(cartListVo.getUserId());
        orderItem.setCreateTime(DateUtils.stringToDate(cartListVo.getCreateTime()));
        orderItem.setUpdateTime(DateUtils.stringToDate(cartListVo.getUpdateTime()));
        return orderItem;
    }
    //封装到订单
  private Order setOrder(BigDecimal stotalPrice,Integer shippingId,Integer userId){
      Order order=new Order();
      order.setOrderNo(orderNo);
      order.setStatus(Const.OrderStatusEum.NO_PAY.getCode());
      order.setPayment(stotalPrice);
      order.setPaymentType(Const.PAYTYPE.ONLINE_PAY.getCode());
      order.setShippingId(shippingId);
      order.setPostage(0);
      order.setUserId(userId);
      //判断订单是否已存在
      Order resultOrder=orderMapper.getOrderByOidUid(orderNo,userId);
      if(resultOrder!=null){
         return  null;
      }
     // 订单插入到数据库
      int count = orderMapper.insert(order);
      if(count>0){
          return order;
      }
      return null;
  }
  private Long getOrderNo(){
      return System.currentTimeMillis()+new Random().nextInt(100);

  }

    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo,Integer status) {
        //若果订单状态已支付，则无法取消
        Order order = orderMapper.getOrderByOidUid(orderNo, userId);
        if(order == null) {
            return ServerResponse.createError("订单不存在！");
        }
        if(order.getStatus()==Const.OrderStatusEum.CANCELED.getCode()){
           return  ServerResponse.createError("订单已经取消！请勿重复取消！");
        }
        if (order.getStatus()>Const.OrderStatusEum.NO_PAY.getCode()) {
            return ServerResponse.createError("订单已支付！，无法取消！");
        }
        //取消订单，商品库存需要更新至原来的数量
        //获得购物车中选中的商品以生成支付的商品
        CartVo cartVo = (CartVo) cartService.getAllProduct(userId).getData();
        List<CartListVo> cartListVos = cartVo.getCartListVos();
        //存放订单项
        for (CartListVo cartListVo : cartListVos) {
            List<Product> productList = (List<Product>) productMapper.getListByNameId(null, cartListVo.getProductId());
            Product product = productList.get(0);
            if(cartListVo.getChecked()==1){
                product.setStock(cartListVo.getQuantity() + product.getStock());
                productMapper.updateByPrimaryKeySelective(product);
            }

        }
        return ServerResponse.createSuccess("订单取消成功！", orderMapper.updateOrderStatus(userId, orderNo, status));
    }

    @Override
    public ServerResponse getAllOrders(Integer userId,Integer pageNum,Integer pageSize) {
        List<Order> orderList = orderMapper.getAllOrders(userId);
        List<OrderVo> orderVoList=Lists.newArrayList();
        for(Order order:orderList){
            Shipping shipping=(Shipping)shippingService.getShippingByUidSid(userId,order.getShippingId()).getData();
            ShippingVo shippingVo=setShippingVo(shipping);
            List<OrderItem> orderItemList=orderItemMapper.getAllOrderItem(userId,order.getOrderNo());
            //封装到orderVo,并添加到集合中
            orderVoList.add(setOrderVo(order,orderItemList,shippingVo));
        }
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo=new PageInfo(orderVoList);
        return  ServerResponse.createSuccess( pageInfo);
    }

    @Override
    public ServerResponse getAllOrdersNoId(Integer pageNum, Integer pageSize) {
        List<Order> orderList = orderMapper.getAllOrdersNoId();
        List<OrderVo> orderVoList=Lists.newArrayList();
        for(Order order:orderList){
            List<OrderItem> allOrderItem = orderItemMapper.getAllOrderItem(null, order.getOrderNo());
            Shipping shipping = shippingMapper.getShippingByUidSid(null, order.getShippingId());


            orderVoList.add( setOrderVo(order,allOrderItem,setShippingVo(shipping)));
        }
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo=new PageInfo(orderVoList);
        return  ServerResponse.createSuccess( pageInfo);
    }




    //后台管理
    //发货
 public ServerResponse sendGoods(Long orderNo,Integer userId){
       Order order=orderMapper.getOrderByOidUid(orderNo,null);
       if(order==null){
           return ServerResponse.createError("订单不存在!");
       }
       if(order.getStatus()==Const.OrderStatusEum.CANCELED.getCode()){
        return  ServerResponse.createError("订单已取消，无法发货！");
       }
     int i = orderMapper.updateOrderStatus(userId, orderNo, Const.OrderStatusEum.SHIPPED.getCode());
     if(i==0){
          return  ServerResponse.createError("发货失败！");
     }
     return ServerResponse.createSuccess("发货成功！");

 }

    //查询订单根据on
    @Override
    public ServerResponse getorderNo(Long orderNo,Integer pageNum, Integer pageSize) {
        List<Order> orderList=orderMapper.getOrderBYNo(orderNo);
        if(orderList.isEmpty()){
          return  ServerResponse.createError("无此订单！");
        }
        List<OrderVo> orderVoList=Lists.newArrayList();
        for(Order order:orderList){
            List<OrderItem> allOrderItem = orderItemMapper.getAllOrderItem(null, order.getOrderNo());
            Shipping shipping = shippingMapper.getShippingByUidSid(null, order.getShippingId());


            orderVoList.add( setOrderVo(order,allOrderItem,setShippingVo(shipping)));
        }
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo=new PageInfo(orderVoList);
        return ServerResponse.createSuccess(pageInfo);
    }

    @Override
    public ServerResponse getOrderDetail(Integer userId, Long orderNo) {
        if(userId==null){
            Order order= orderMapper.getOrderByOidUid(orderNo,userId);
            if(order==null){
                return ServerResponse.createError("订单不存在！");
            }
            Shipping shipping=shippingMapper.getShippingByUidSid(null, order.getShippingId());
            List<OrderItem> allOrderItem = orderItemMapper.getAllOrderItem(null, order.getOrderNo());
            OrderVo orderVo=setOrderVo(order,allOrderItem,setShippingVo(shipping));
            return ServerResponse.createSuccess("查询成功！",orderVo);
        }
        Order order= orderMapper.getOrderByOidUid(orderNo,userId);
        if(order==null){
            return ServerResponse.createError("订单不存在！");
        }
        Shipping shipping=(Shipping)shippingService.getShippingByUidSid(userId, order.getShippingId()).getData();
        List<OrderItem> orderItemList= orderItemMapper.getAllOrderItem(userId,orderNo);
        OrderVo orderVo=setOrderVo(order,orderItemList,setShippingVo(shipping));
        return ServerResponse.createSuccess("查询成功！",orderVo);
    }



















    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        // 支付宝当面付2.0服务
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        //存放生成订单的信息
        Map map = Maps.newHashMap();
        //根据orderNo查询是否有此订单
        Order order = orderMapper.getOrderByOidUid(orderNo, userId);
        if (order == null) {
            return ServerResponse.createError("无此订单!");
        }
        map.put("orderNo", orderNo);
        //支付宝生成支付二维码操作

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        //##1.修改订单号
        String outTradeNo = orderNo.toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        //##2.#修改订单标题
        String subject = new StringBuilder().append("用户在frtmall商城当面付扫码消费，订单号：").append(orderNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        //##3.修改商品价格
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // ##如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        //##4.修改订单描述
        String body = new StringBuilder().append("订单" + orderNo).append("购买商品共计" + totalAmount + "元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        //获取该订单下的所有订单项集合
        List<OrderItem> orderItemList = orderItemMapper.getAllOrderItem(userId, orderNo);
        //遍历得到每一项商品
        for (OrderItem orderItem : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtils.multiply(orderItem.getCurrentUnitPrice().doubleValue(), new Double("100")).longValue(), orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)//配置回调地址
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                //简单回应
                dumpResponse(response);
                //根据路径创建文件夹
                File filePath = new File(path);
                if (!filePath.exists()) {
                    filePath.setWritable(true);
                    filePath.mkdirs();
                }
                //此处路径要拼写/
                //这是包含文件名的全路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrName = String.format("qr-%s.png", response.getOutTradeNo());
                //工具生成二维码,生成到指定文件夹下
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                //根据文件名和路径创建文件
                File qrFile = new File(qrName, path);
                // 上传到ftp服务器
                try {
                    FtpUtils.uploadFtp(Lists.<File>newArrayList(qrFile));
                } catch (IOException e) {
                    log.info("上传二维码异常！", e);
                }
                log.info("qrPath:" + qrPath);
                //把二维码地址放入map
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + qrFile.getName();
                map.put("qrUrl", qrUrl);
                return ServerResponse.createSuccess(map);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createError("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createError("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createError("不支持的交易状态，交易返回异常!!!");
        }
    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
    //验证订单并修改订单信息，插入支付信息
    public ServerResponse callBack(Map map){
        //获取订单号
        Long orderNo=(Long)map.get("out_trade_no");
         //判断订单是否存在
        Order order=orderMapper.getOrderByOidUid(orderNo,null);
        if(order==null){
       return  ServerResponse.createError("该订单不存在！");
        }
        //判断数据库的订单状态是否已支付(状态码10)
        if (order.getStatus()>=Const.OrderStatusEum.PAYD.getCode()){
         return ServerResponse.createError("已回调过了，请勿重复调用！");
        }
        String status=(String)map.get("trade_status");
        //获取回调的订单支付结果，如果成功，则更新到数据库订单
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(status)){
           order.setStatus(Const.OrderStatusEum.PAYD.getCode());
           order.setUpdateTime(DateUtils.stringToDate(map.get("gmt_payment").toString()));
           orderMapper.updateByPrimaryKeySelective(order);
        }
        //插入支付信息
        String tradeNo=(String)map.get("trade_no");
        PayInfo payInfo=new PayInfo();
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PAYPLAT.ALIPAY.getCode());
        payInfo.setPlatformStatus(status);
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setUserId(order.getUserId());
        payInfoMapper.insert(payInfo);
             return ServerResponse.createSuccess();
    }
    //前台查看订单是否已支付
public ServerResponse    isTradeSuccess(Integer userId,Long orderNo) {
    Order order = orderMapper.getOrderByOidUid(orderNo, userId);
    if (order == null) {
        return ServerResponse.createError("无此订单!");
    }
    Order order1 = orderMapper.getOrderByOidUid(orderNo, userId);
    if (order1.getStatus() >= Const.OrderStatusEum.PAYD.getCode()) {
        return ServerResponse.createSuccess();
    }
    return ServerResponse.createError();
}


}
