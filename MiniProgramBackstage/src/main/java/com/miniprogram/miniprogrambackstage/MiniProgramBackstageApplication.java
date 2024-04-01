package com.miniprogram.miniprogrambackstage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.miniprogram.miniprogrambackstage.mapper")
public class MiniProgramBackstageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniProgramBackstageApplication.class, args);
    }

}
