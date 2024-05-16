package com.example.demo.entry;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.Objects;

@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataJson {
    private Integer code;
    private String msg;
    private Map data;
}
