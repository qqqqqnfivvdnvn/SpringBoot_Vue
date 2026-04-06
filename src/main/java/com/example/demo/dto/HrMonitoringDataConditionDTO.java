package com.example.demo.dto;

import lombok.Data;

/**
 * 恒瑞数据汇总查询条件 DTO
 */
@Data
public class HrMonitoringDataConditionDTO {
    private String batchId;
    private String keyId;
    private String address;
    private String productName;
    private String name;
    private String businessLicenseName;
    private String province;
    private String createTime;
}
