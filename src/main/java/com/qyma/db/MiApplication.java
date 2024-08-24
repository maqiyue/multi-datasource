package com.qyma.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.qyma.db.mapper")
@SpringBootApplication
public class MiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiApplication.class, args);
    }
}
