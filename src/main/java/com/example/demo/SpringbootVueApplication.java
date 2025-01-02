package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement // 启用事务管理
@SpringBootApplication
@MapperScan("com.example.demo.mapper") // 扫描mapper

public class SpringbootVueApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootVueApplication.class, args);
    }

}
