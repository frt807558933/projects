package com.frt.utils;

import com.frt.common.Const;
import org.junit.Test;

import java.util.Random;

public class test {
    @Test
    public void test1(){
   /*     String s="";
        Long orderNo=System.currentTimeMillis();
       int i= new Random().nextInt(100);
        System.out.println(orderNo+"*"+i);*/
        String desc=null;
        switch (20){
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
        System.out.println(desc);
    }
}
