package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务配置类
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 批次处理线程池
     * 用于恒瑞项目批次数据异步处理、主数据模糊匹配批次处理
     */
    @Bean(name = "batchExecutor")
    public Executor batchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量，超过核心线程数时任务放入队列
        executor.setQueueCapacity(20);
        // 线程名称前缀
        executor.setThreadNamePrefix("batch-async-");
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        // 等待任务执行完再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 关闭时等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        // 队列满且达到最大线程数时的拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程池
        executor.initialize();
        return executor;
    }

    /**
     * 单条数据匹配线程池
     * 用于主数据模糊匹配单条数据并发处理
     */
    @Bean(name = "matchExecutor")
    public Executor matchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(50);
        // 线程名称前缀
        executor.setThreadNamePrefix("match-async-");
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        // 等待任务执行完再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 关闭时等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        // 队列满且达到最大线程数时的拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程池
        executor.initialize();
        return executor;
    }
}
