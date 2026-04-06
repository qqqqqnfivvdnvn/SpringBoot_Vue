package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 恒瑞数据比对关系表
 * business_license_name 为主键
 */
@Data
public class HrOrgRelation {
    private String province;
    private String businessLicenseName;
    private String keyId;
    private String name;
    private String address;
    private LocalDateTime addTime;
    private LocalDateTime updateTime;
}
