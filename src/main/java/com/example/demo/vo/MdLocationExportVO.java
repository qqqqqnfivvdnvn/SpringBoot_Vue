package com.example.demo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 主数据地理位置导出 VO
 */
@Data
public class MdLocationExportVO {

    @ExcelProperty("id")
    private String id;

    @ExcelProperty("名称")
    private String originalName;

    @ExcelProperty("省份")
    private String originalProvince;

    @ExcelProperty("地址")
    private String originalAddress;

    @ExcelProperty("高德省份")
    private String provName;

    @ExcelProperty("高德省 id")
    private String provId;

    @ExcelProperty("高德市")
    private String cityName;

    @ExcelProperty("高德市 id")
    private String cityId;

    @ExcelProperty("高德区县")
    private String countyName;

    @ExcelProperty("高德区县 id")
    private String countyId;

    @ExcelProperty("经纬度")
    private String lngLat;
}
