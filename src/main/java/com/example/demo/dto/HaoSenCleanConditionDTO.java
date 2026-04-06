package com.example.demo.dto;

import lombok.Data;

@Data
public class HaoSenCleanConditionDTO {

    private String batchCode;
    private Integer  batchStatus;
    private Integer  cleanStatus;
    private String dataId;
    private String dataType;
    private String dataCode;
    private String originalName;
    private String originalProvince;
    private String haosenCode;
    private String startTime;
    private String endTime;

}
