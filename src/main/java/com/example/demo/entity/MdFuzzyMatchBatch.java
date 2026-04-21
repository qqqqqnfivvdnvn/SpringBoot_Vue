package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 主数据模糊匹配批次表实体
 */
@Data
public class MdFuzzyMatchBatch {
    private String batchId;
    private Integer status; // 0: 处理中 1: 已处理 2: 处理失败
    private String dataType; // hospital: 医院 drugstore: 药店
    private String originalFilename;
    private String message;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 状态描述（非数据库字段）
    private String statusDesc;

    public String getStatusDesc() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "处理中";
            case 1: return "已处理";
            case 2: return "处理失败";
            default: return "未知";
        }
    }
}
