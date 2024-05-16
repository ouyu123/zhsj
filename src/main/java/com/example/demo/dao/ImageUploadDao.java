package com.example.demo.dao;

import com.example.demo.entry.Image;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ImageUploadDao {

    int  insert(Image image);
    void insertDecride(Image image);

    List<Image> queryAll(String ip);

    String queryBase4(String md5);
    int deleteId(int id);
}
