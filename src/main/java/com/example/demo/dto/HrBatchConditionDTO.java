package com.example.demo.dto;

import lombok.Data;

/**
 * 恒瑞批次查询条件 DTO
 */
@Data
public class HrBatchConditionDTO {
    private String batchId;
    private Integer status;
    private String startTime;
    private String endTime;
}
