package com.frt.utils;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateUtils {
private static String format="YYYY-MM-dd HH:mm:ss";
    //字符串转时间
       public static Date stringToDate(String date){
       //声明格式
       DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(format);
       DateTime dateTime=DateTime.parse(date,dateTimeFormatter);
             return  dateTime.toDate();
   }
   //时间转字符串
    public static String dateToString(Date date){
       DateTime dateTime=new DateTime(date);

           return  dateTime.toString(format);
    }


}
