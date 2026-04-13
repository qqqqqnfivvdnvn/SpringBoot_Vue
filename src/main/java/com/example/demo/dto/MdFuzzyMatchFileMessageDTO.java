package com.example.demo.dto;

import lombok.Data;

/**
 * 主数据模糊匹配文件消息 DTO
 */
@Data
public class MdFuzzyMatchFileMessageDTO {
    private String message;
    private String batchId;
    private int result;
}
