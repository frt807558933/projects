package com.frt.vo;

import java.math.BigDecimal;

public class ProductListVo {
    private  Integer id;
    private  Integer categoryId;
    private  String name;
    private  String mainImage;
    private String subTitle;
    private BigDecimal price;
    private Integer status;
    private String imgHttps;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImgHttps() {
        return imgHttps;
    }

    public void setImgHttps(String imgHttps) {
        this.imgHttps = imgHttps;
    }
}
