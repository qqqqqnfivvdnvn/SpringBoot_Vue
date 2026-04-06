package com.example.demo.dto;

import lombok.Data;

/**
 * 主数据地理位置查询条件 DTO
 */
@Data
public class MdLocationConditionDTO {
    private String batchId;
    private String originalName;
    private String province;
    private String city;
    private String areaName;
}
