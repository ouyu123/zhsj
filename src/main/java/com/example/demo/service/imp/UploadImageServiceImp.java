package com.example.demo.service.imp;

//import com.example.demo.controller.ImageController;
import com.example.demo.dao.ImageUploadDao;
import com.example.demo.entry.Image;
import com.example.demo.service.IUploadImageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class UploadImageServiceImp implements IUploadImageService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ImageUploadDao imageUploadDao;

    HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String uploadImage(Image image) throws IOException {
        String imageDescride ;
        image.setImageDescride("还未开始识别");
        int id = imageUploadDao.insert(image);
        System.out.println(id);

        String json = "{\"path\": \"" + image.getImageBase64().replace("\\","\\\\") + "\"}";
        System.out.println(json);
        // 构建请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/api"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();  //构建识别模型的请求

        // 发送请求并处理响应
        try {
            // 发送请求并处理响应
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            //把模型返回结果转化为json
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());

            //获取模型的识别结果
            imageDescride= jsonNode.get("label").asText() ;
            image.setImageDescride(imageDescride);

            //记录入库
            imageUploadDao.insertDecride(image);
            return imageDescride;
        } catch (Exception e) {
            e.printStackTrace();
            return "识别错误";
        }
    }

    @Override
    public String checkImage(String md5) {
        String imageDescride=null;
        if(null != (imageDescride = (String) redisTemplate.opsForValue().get(md5))) return imageDescride; //判断redis中存不存在

        if(null != (imageDescride = imageUploadDao.queryBase4(md5)) && !"还未开始识别".equals(imageDescride)) //判断数据库存不存在
        {
            redisTemplate.opsForValue().set(md5,imageDescride); //如果数据库中存在则加入到redis中
            return imageDescride; //返回标签
        }
        return imageDescride;
    }

    public List<Image> getLogs(String ip)
    {
        List<Image> images = imageUploadDao.queryAll(ip);
        for (Image im : images) {
            System.out.println(im.getImageDescride());
        }
        return images;
    }
}
