package com.example.demo.entity;

import lombok.Data;

@Data
public class HaoSenCleanData {

    //    batch_code	status	clean_status	data_id	data_type	data_code	original_name	original_province	original_address	company_name	haosen_code	add_time
    private String batchCode;
    private String status;
    private String cleanStatus;
    private String dataId;
    private String dataType;
    private String dataCode;
    private String originalName;
    private String originalProvince;
    private String originalAddress;
    private String companyName;
    private String haosenCode;
    private String addTime;

}
