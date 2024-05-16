package com.example.demo.controller;

import com.example.demo.entry.DataJson;
import com.example.demo.entry.Image;
import com.example.demo.service.IUploadImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
@RequestMapping("upload")
public class UploadImage {

    @Autowired
    private IUploadImageService uploadImageService;

    private final String BASEPATH = "D:\\xuexi\\javawed\\Ideacode\\zhs\\demo\\src\\main\\resources\\static\\image\\";

    @PostMapping ("image")
    @ResponseBody
    public DataJson uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request)
    {
        if(file.isEmpty())
        {
            return DataJson.builder().code(1).msg("上传失败！文件不能为空").build();
        }
        try {

            String md5 = fileToMD5(file); //求文件通过的md5加密的结果

            String descride;
            if(null != (descride = uploadImageService.checkImage(md5))){  //根据文件的md5判断redis中中存不存在 然后在查询数据库 ，如果存在直接返回识别结果
                Map<String,String> map = new HashMap<>();
                map.put("decride",descride);
                return DataJson.builder().code(0).msg("上传成功").data(map).build();
            }
            // 获取文件的原始名称
            String originalFilename = file.getOriginalFilename();
            // 指定文件存储路径（这里假设存储在当前项目的 upload 目录下）

           String destinationPath = BASEPATH + originalFilename;
            // 创建目标文件
            File destinationFile = new File(destinationPath);
            // 如果目标文件所在的目录不存在，则创建目录
            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }

            //存储图片到本地
            file.transferTo(destinationFile);

            System.out.println("请求的ip地址："+request.getRemoteAddr());

            //构建image实体
            Image image = Image.builder().imageBase64(destinationPath).ip(request.getRemoteAddr()).md5(md5).build();

            //去识别图片 并把记录存储到数据库中
            descride = uploadImageService.uploadImage(image);
            Map<String,String> map = new HashMap<>();
            map.put("decride",descride);
            return DataJson.builder().code(0).msg("上传成功").data(map).build();
        } catch (Exception e) {
            return DataJson.builder().code(2).msg("识别失败").build();
        }

    }

    @GetMapping("getLogs")
    public String getLogs()
    {
        return "Listlog";
    }

    @PostMapping("getIpLogs")
    @ResponseBody
    public DataJson getIpLogs(HttpServletRequest request) throws IOException {
        String ip4 = request.getRemoteAddr();
        List<Image> images = uploadImageService.getLogs(ip4);
        Map<Integer,Image> map = new HashMap<>();
        for (Image image : images) {
            String absolutePath = image.getImageBase64();
            String basePath = "D:\\\\xuexi\\\\javawed\\\\Ideacode\\\\zhs\\\\demo\\\\src\\\\main\\\\resources\\\\static\\";

            Path pathAbsolute = Paths.get(absolutePath);
            Path pathBase = Paths.get(basePath);
            Path pathRelative = pathBase.relativize(pathAbsolute); // 获取相对路径

            String relativePath =  pathRelative.toString();
            System.out.println(relativePath);
            image.setImageBase64(relativePath);
            map.put(image.getId(),image);
        }
        return DataJson.builder().code(0).msg("查询成功").data(map).build();
    }



     protected String fileToMD5(MultipartFile file) throws NoSuchAlgorithmException, IOException {

         byte[] fileBytes = file.getBytes();
         String base64String = Base64.getEncoder().encodeToString(fileBytes);


        // 将 Base64 编码的字符串解码为字节数组
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        // 计算字节数组的 MD5 哈希值
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(decodedBytes);

        // 将 MD5 哈希值转换为十六进制字符串表示
        StringBuilder sb = new StringBuilder();
        for (byte b : md5Bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
