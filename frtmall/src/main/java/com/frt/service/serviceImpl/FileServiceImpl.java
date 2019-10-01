package com.frt.service.serviceImpl;

import com.frt.common.ServerResponse;
import com.frt.service.IFileService;
import com.frt.utils.FtpUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("fileServiceImpl")
public class FileServiceImpl implements IFileService {
    //声明日志对象
    Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    public ServerResponse uploadImg(String path,MultipartFile file){
     //获取图片的上传时的文件名称
        String suffx=file.getOriginalFilename();
        //避免重复
        String fileName= UUID.randomUUID().toString()+suffx;
        //在项目下创建一个保存图像文件的文件夹
        File webpath=new File(path);
        if(!webpath.exists()){
            //赋予权限
            webpath.setWritable(true);
            webpath.mkdirs();
        }
        //获得目的文件夹和上传之后的文件名文件名
        File webfile=new File(path,fileName);
        //打印日志
        logger.info(new Date()+"开始上传文件"+fileName+"到目录"+path);
        // /开始上传文件
        try {
        //上传成功！
            file.transferTo(webfile);
               //再上传至ftp服务器
           FtpUtils.uploadFtp(Lists.newArrayList(webfile));
            //删除upload文件夹下的文件
          //  webfile.delete();

        } catch (IOException e) {
            logger.error("上传文件"+fileName+"失败",e);
            return  ServerResponse.createError("上传失败！");
        }

        return ServerResponse.createSuccess("上传成功!");
    }
}
