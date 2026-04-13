package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 主数据模糊匹配汇总数据实体
 */
@Data
public class MdFuzzyMatchSummary {
    private String batchId;
    private String originalId;
    private String originalProvince;
    private String originalName;
    private String originalAddress;
    private String keyid;
    private String name;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
