package com.example.demo.dto;

import lombok.Data;

/**
 * 主数据模糊匹配查询条件 DTO
 */
@Data
public class MdFuzzyMatchConditionDTO {
    private String batchId;
    private String dataType; // hospital: 医院 drugstore: 药店
    private String originalId;
    private String originalProvince;
    private String originalName;
    private String originalAddress;
    private String keyid;
    private String name;
}
