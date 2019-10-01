package com.frt.utils;


import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class FtpUtils {
    private  static  String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private  static  String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private  static  String ftpPass=PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private  String port;
    private  String user;
    private  String pwd;

private  static  Logger logger= LoggerFactory.getLogger(FtpUtils.class);
    public FtpUtils(String ip, String port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
       //批量上传，用list
        public static boolean uploadFtp(List<File> fileList) throws IOException {
        boolean flag=true;
        //连接Ftp
        FTPClient ftpClient=new FTPClient();
        try {
            //连接ip
            ftpClient.connect(ftpIp,21);
            //登录
            ftpClient.login(ftpUser,ftpPass);
            logger.info(new Date()+ftpUser+"连接服务器"+ftpIp+"成功！");
            //切换目录img
            ftpClient.changeWorkingDirectory("img");
            //设置缓存
            ftpClient.setBufferSize(1024);
            //设置编码
            ftpClient.setControlEncoding("UTF-8");
            //设置文件类型，二进制，防止乱码
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //遍历容器
            for(File file:fileList){
                //上传每一个文件到ftp服务器
                ftpClient.storeFile(file.getName(), new FileInputStream(file));
                logger.info(new Date()+"上传文件"+file.getName()+"成功！");
            }

        } catch (Exception e){
            flag=false;
            logger.error(new Date()+"操作失败！异常：\"",e);
        }
        finally {
            //释放资源
            ftpClient.disconnect();
        }

        return  flag;
    }
}
