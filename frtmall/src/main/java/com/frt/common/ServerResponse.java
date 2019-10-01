package com.frt.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
//实现序列化接口，为了在网络上传输对象，转换成二进制
//这是一个公共的登录服务器响应类，可以返回json格式的登录状态，异常信息，和登录成功后要返回的数据
//属性和构造都是私有，然后提供公共的获得方法
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)//不返回为空的节点
 public class ServerResponse<T> implements Serializable {
    private int status;//状态,0代表成功，1代表失败
    private String msg;//信息
    private T data;//返回数据不明时，用泛型声明
    private ServerResponse(int status) {
        this.status = status;
    }
    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status, T data) {
        this.status = status;
         this.data = data;
    }
    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    @JsonIgnore// 忽略public字段，不显示在json
    public  boolean isSuccess(){
        //用此时的status和枚举类的Success的code(0)做对比,为0则成功，否则false
        return  status==ResponseCode.Success.getCode();
    }
    //创建成功响应对象，返回成功状态0
    public  static<T>  ServerResponse<T> createSuccess(){
        return new ServerResponse<T>(ResponseCode.Success.getCode());
    }
    //创建成功响应对象，传入信息，返回成功状态0和信息
        public  static<T>  ServerResponse<T> createSuccess(String msg){
        return new ServerResponse<T>(ResponseCode.Success.getCode(),msg);
    }
    //创建成功响应对象，传入要响应的数据data,返回成功状态0和数据
        public  static<T>  ServerResponse<T> createSuccess(T data){
            return  new ServerResponse<T>(ResponseCode.Success.getCode(),data);
    }
    //创建成功响应对象，传入要响应的数据data和信息,返回成功状态0，信息和数据
    public  static<T>  ServerResponse<T> createSuccess(String msg,T data){
             return  new ServerResponse<T>(ResponseCode.Success.getCode(),msg,data);
     }

     //返回错误
        public static <T> ServerResponse<T> createError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
  //两个自定义的错误信息

    public static <T> ServerResponse<T> createError(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createError(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

}