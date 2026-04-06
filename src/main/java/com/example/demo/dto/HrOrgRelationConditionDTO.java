package com.example.demo.dto;

import lombok.Data;

/**
 * 恒瑞数据比对关系查询条件 DTO
 */
@Data
public class HrOrgRelationConditionDTO {
    private String batchId;
    private String businessLicenseName;
    private String keyId;
    private String address;
    private String province;
}
