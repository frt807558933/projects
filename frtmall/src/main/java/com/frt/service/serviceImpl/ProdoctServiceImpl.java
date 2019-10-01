package com.frt.service.serviceImpl;

import com.frt.common.Const;
import com.frt.common.ResponseCode;
import com.frt.common.ServerResponse;
import com.frt.dao.ProductMapper;
import com.frt.pojo.Product;
import com.frt.service.IProdoctService;
import com.frt.utils.DateUtils;
import com.frt.utils.PropertiesUtil;
import com.frt.vo.ProductDetailVo;
import com.frt.vo.ProductListVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("prodoctServiceImpl")
public class ProdoctServiceImpl implements IProdoctService {
    @Autowired
    ProductMapper productMapper;
    //添加商品
   public ServerResponse productSave(Product product) {
       //判断product是否为空
      if (product!=null) {
           //如果子图不为空把子图赋予主图
           if(StringUtils.isNotBlank(product.getSubImages())){
       //    把子图根据，分割成每个图片的名字
               String[] sbs=product.getSubImages().split(",");
               if(sbs.length>0){
                   product.setMainImage(sbs[0]);
               }
           }
           //判断id为空否，为空插入新的商品
           if(product.getId()==null){
               int count = productMapper.insert(product);
                 if(count>0){
                     return    ServerResponse.createSuccess("创建成功！");
                 }
                 return  ServerResponse.createError("创建失败");
           }
           else {
           //否则开始更新
              int count1 = productMapper.updateByPrimaryKeySelective(product);
                  if(count1>0){
                    return   ServerResponse.createSuccess("更新成功！");
                  }
                 return ServerResponse.createError("更新失败");
           }
      }
          return  ServerResponse.createError("参数错误！创建或更新产品失败！");
   }
   //更新商品状态
    public ServerResponse<String> setStatus(Integer productId,Integer status){
         if(productId==null||status==null){
          return ServerResponse.createError(Const.ILLEGAl);
         }
         Product  product =new Product();
         product.setId(productId);
         product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if(count>0){
          return   ServerResponse.createSuccess("更新成功");
        }


        return  ServerResponse.createError("更新失败");
    }
    public ServerResponse<ProductDetailVo>productDetail(Integer productId){
        if(productId==null){
           return ServerResponse.createError("商品已下架");
        }
       Product product= productMapper.selectByPrimaryKey(productId);
        if(product==null){
             return  ServerResponse.createError("查询失败！");
        }
        setProductDetailVo(product);
        //需要一个承载结果的商品类对象
        return  ServerResponse.createSuccess("查询成功",setProductDetailVo(product));

    }
           //把查询的结果封装到vo里
           private   ProductDetailVo setProductDetailVo(Product product){
                ProductDetailVo productDetailVo=new ProductDetailVo();
                  productDetailVo.setId(product.getId());
                  productDetailVo.setName(product.getName());
                  productDetailVo.setSubtitle(product.getSubtitle());
                  productDetailVo.setMainImage(product.getMainImage());
                  productDetailVo.setSubImages(product.getSubImages());
                  productDetailVo.setDetail(product.getDetail());
                  productDetailVo.setPrice(product.getPrice());
                  productDetailVo.setStock(product.getStock());
                  productDetailVo.setStatus(product.getStatus());
                  productDetailVo.setCreateTime(DateUtils.dateToString(product.getCreateTime()));
                  productDetailVo.setUpdateTime(DateUtils.dateToString(product.getUpdateTime()));
                 //从配制文件中获取
               productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.frtmall.com/yoga.jpg"));

              return  productDetailVo;
           }
           //获取商品列表
    public ServerResponse getList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> list=productMapper.getList();
        if(list.size()==0){
          ServerResponse.createError("获取失败");
        }
        List<ProductListVo> listVos= Lists.newArrayList();
        for(Product product:list){
                listVos.add( setProductListVo(product));
        }
        PageInfo pageInfo=new PageInfo(listVos);
        return  ServerResponse.createSuccess("查询成功",pageInfo);
    }
        //封装ProductListVo
          private  ProductListVo setProductListVo(Product product){
              ProductListVo productListVo=new ProductListVo();
                  productListVo.setId(product.getId());
                  productListVo.setCategoryId(product.getCategoryId());
                  productListVo.setName(product.getName());
                  productListVo.setSubTitle(product.getSubtitle());
                  productListVo.setMainImage(product.getMainImage());
                  productListVo.setPrice(product.getPrice());
                  productListVo.setStatus(product.getStatus());
                  productListVo.setImgHttps(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
           return  productListVo;
          }
      //根据名称或id搜索获取所有商品
    public ServerResponse getListByNameId(String productName,Integer productId,int pageNum,int pageSize){
              if(StringUtils.isNotBlank(productName)){
                  productName= "%"+productName+"%";
              }
        List<Product> list = productMapper.getListByNameId(productName, productId);
        if(list.isEmpty()){
            return  ServerResponse.createError("查询失败！无此内容！");
        }
        List<ProductListVo> listVos=Lists.newArrayList();
        for(Product product:list){
            listVos.add( setProductListVo(product));
        }
        //开始分页
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo=new PageInfo(listVos);
        return  ServerResponse.createSuccess("查询成功！",pageInfo);

    }
     //前台商品展示
    //根据商品分类获取所有商品，商品状态必须在售
     public  ServerResponse getProductByCid(Integer categoryId,int pageNum,int pageSize){
      List<Product> list=productMapper.getProductByCid(categoryId);
      if(list.isEmpty()){
        return  ServerResponse.createError("查询失败！");
      }
      List<ProductDetailVo> list1=Lists.newArrayList();
      for(Product product:list){
          //判断商品状态
          if(product.getStatus()==1){
              list1.add(setProductDetailVo(product));
          }
      }
      //分页
         PageHelper.startPage(pageNum,pageSize);
         PageInfo<ProductDetailVo> pageInfo=new PageInfo(list1);
        return ServerResponse.createSuccess(pageInfo);
     }
     //根据商品名称搜取
      public ServerResponse  getProductByNameId(String productName,Integer productId,int pageNum,int pageSize){
         if(productId!=null){
             List<Product> list = productMapper.getListByNameId(null,productId);
             //第一个即为详情商品，转换成显示的详情商品
             ProductDetailVo productDetailVo= setProductDetailVo(list.get(0));
            if(productDetailVo==null){
            return  ServerResponse.createError("商品不存在");
            }
            if(productDetailVo.getStatus()>1){
             return ServerResponse.createError("商品不存在或已下架！");
            }
            return ServerResponse.createSuccess("查询成功！",productDetailVo);
         }
          if(StringUtils.isNotBlank(productName)){
              productName= "%"+productName+"%";
          }
          //把id值为空，则是根据名字搜寻商品
          List<Product> list = productMapper.getListByNameId(productName,null);
          if(list.isEmpty()){
              return  ServerResponse.createError("查询失败！无此内容！");
          }

          List<ProductDetailVo> listVos=Lists.newArrayList();
          for(Product product:list){
              //添加status为1的商品
              if(product.getStatus()==1 ){
                  listVos.add(setProductDetailVo(product));
              }

          }
          //开始分页
          PageHelper.startPage(pageNum,pageSize);
          PageInfo pageInfo=new PageInfo(listVos);
          return  ServerResponse.createSuccess("查询成功！",pageInfo);
    }
    //根据id获取商品详情
    public ServerResponse getProductById(Integer productId){
          if(productId==null){
              return  ServerResponse.createError(Const.ILLEGAl);
          }
          Product product=productMapper.selectByPrimaryKey(productId);
          if(product==null){
         return ServerResponse.createError("无此商品");
          }
          return ServerResponse.createSuccess("查询成功",product);
    }

}
