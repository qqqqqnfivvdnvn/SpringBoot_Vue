package com.example.demo.dto;

import lombok.Data;

/**
 * 主数据地理位置批次查询条件 DTO
 */
@Data
public class MdLocationBatchConditionDTO {
    private String batchId;
    private Integer status;
    private String startTime;
    private String endTime;
}
