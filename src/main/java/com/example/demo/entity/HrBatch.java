package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 恒瑞批次表
 */
@Data
public class HrBatch {
    private String batchId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status; // 0 处理中 1 已处理 2 处理失败
    private String originalFilename; // 原始文件名
    private String message; // 处理状态信息（成功/失败消息）
}
