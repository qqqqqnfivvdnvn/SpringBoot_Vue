package com.example.demo.config;

import com.github.pagehelper.PageInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@MapperScan("com.example.demo.mapper") // Mapper 包路径
public class MybatisPlusConfig {

    /**
     * 配置 PageHelper 分页插件
     * 在多数据源环境下，使用 autoRuntimeDialect 实现动态识别数据库类型
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 不指定具体方言，启用运行时自动识别
        // PageHelper 会根据当前数据库连接的 URL 自动判断数据库类型
        properties.setProperty("helperDialect", "");
        // 启用运行时自动识别方言 - 关键配置！
        properties.setProperty("autoRuntimeDialect", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }
}