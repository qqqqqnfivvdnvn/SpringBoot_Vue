package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 恒瑞批次数据 VO
 */
@Data
public class HrBatchVO {
    private String batchId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
    private String statusDesc;
    private Long dataCount;
    private String originalFilename; // 原始文件名
    private String message; // 处理状态信息（成功/失败消息）
}
