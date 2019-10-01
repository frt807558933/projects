package com.frt.service;

import com.frt.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    ServerResponse uploadImg(String path, MultipartFile file);
}
