package com.frt.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {
    //乘
    public static BigDecimal multiply(double d1,double d11){
           BigDecimal m1=new BigDecimal(Double.toString(d1));
        BigDecimal q1=new BigDecimal(Double.toString(d11));
           return  m1.multiply(q1);
       }
       //除
       public  static  BigDecimal divide(double d2,double d22){
           BigDecimal m2=new BigDecimal(Double.toString(d2));
           BigDecimal q2=new BigDecimal(Double.toString(d22));
           return  m2.divide(q2);
       }
       //加
       public static BigDecimal add(double d3,double d33){
           BigDecimal m3=new BigDecimal(Double.toString(d3));
           BigDecimal q3=new BigDecimal(Double.toString(d33));

           return  m3.add(q3);
       }
    //减
    public static BigDecimal subtract(double d4,double d44){
        BigDecimal m4=new BigDecimal(Double.toString(d4));
        BigDecimal q4=new BigDecimal(Double.toString(d44));
        return  m4.add(q4);
    }

}

