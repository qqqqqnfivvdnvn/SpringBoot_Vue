package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 主数据地理位置表
 */
@Data
public class MdLocation {
    private String id;
    private String batchId;
    private String originalName;        // 原始名称
    private String originalProvince;    // 原始省份
    private String originalAddress;     // 原始地址
    private String areaName;            // 区县名称
    private String areaId;              // 区县编码
    private String lngLat;              // 经纬度 (经度，纬度)
    private String province;            // 省份
    private String city;                // 城市
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
