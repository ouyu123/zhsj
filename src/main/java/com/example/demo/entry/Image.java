package com.example.demo.entry;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    private Integer id;
    private String ip;
    private  String imageBase64;
    private Date uploadTime;

    private String md5;
    private String imageDescride;
}
