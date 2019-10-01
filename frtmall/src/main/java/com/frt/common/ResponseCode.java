package com.frt.common;

//封装类一些成功信息和错误信息，不用总是手动写，直接可以调用这些值
 public enum ResponseCode {
    //枚举类，先声明一些实例
    Success(0,"SUCCESS！"),
    ERROR(1,"ERROR！");

    //然后根据实例里的参数，来声明，并创建构造函数
        private  int code;//状态
        private  String desc;//信息

         ResponseCode(int code,String desc){
        this.code=code;
       this.desc=desc;
        }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}