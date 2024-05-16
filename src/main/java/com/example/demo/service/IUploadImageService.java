package com.example.demo.service;

import com.example.demo.entry.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUploadImageService {

    public String uploadImage(Image image) throws Exception;


    public String checkImage(String md5);
    public List<Image> getLogs(String ip);
}
