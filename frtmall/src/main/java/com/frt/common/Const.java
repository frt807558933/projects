package com.frt.common;



//常量类，用于存储经常用到的属性
 public class Const {
    public  static  final String CURRENT_USER="currentUSer";
    public  static  final  String EMAIL="email";
    public  static  final  String USERNAME="username";
    public  static  final String ILLEGAl="参数错误";
    public  static  final String NO_UserLOGIN="暂无用户登录！";
    public  static  final String NO_ADMINPERMISSION="没有权限访问！";

    //封装用户的角色信息,使用Role接口类
    public interface  Role{
    int ROLE_CUSTOMER=0;//普通用户
    int ROLE_ADMIN=1;//管理员
    }
    //订单状态
    public enum OrderStatusEum{
       CANCELED(0,"订单已取消"),
       NO_PAY(10,"订单未支付"),
       PAYD(20,"订单已支付"),
       SHIPPED(30,"订单已发货"),
       ORDER_FINISHED(40,"订单已完成"),
       ORDER_CLOSED(50,"订单已关闭")

       ;

       OrderStatusEum(int code, String desc) {
          this.code = code;
          this.desc = desc;
       }

       private int code;
       private String desc;

       public int getCode() {
          return code;
       }

       public void setCode(int code) {
          this.code = code;
       }

       public String getDesc() {
          return desc;
       }

       public void setDesc(String desc) {
          this.desc = desc;
       }
    }
    //支付状态
   public interface AlipayCallback{
       String TRADE_STATUS_WAIT_PAY="WAIT_PAY";
       String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";
       String RESPONSED_SUCCESS="success";
       String RESPONSED_FAILED="failed";
    }
    //支付平台
   public enum PAYPLAT{
       ALIPAY(1,"支付宝");
       ;

       PAYPLAT(int code, String desc) {
          this.code = code;
          this.desc = desc;
       }

       private int code;
      private String desc;

      public int getCode() {
         return code;
      }

      public void setCode(int code) {
         this.code = code;
      }

      public String getDesc() {
         return desc;
      }

      public void setDesc(String desc) {
         this.desc = desc;
      }
   }
    //支付平台
    public enum PAYTYPE{
        ONLINE_PAY(1,"在线支付");
        ;

        PAYTYPE(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
 }
