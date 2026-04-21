package com.example.demo.dto;

import lombok.Data;

/**
 * 主数据模糊匹配批次查询条件 DTO
 */
@Data
public class MdFuzzyMatchBatchConditionDTO {
    private String batchId;
    private Integer status;
    private String dataType; // hospital: 医院 drugstore: 药店
    private String startTime;
    private String endTime;
}
