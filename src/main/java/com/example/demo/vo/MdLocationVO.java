package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 主数据地理位置 VO (用于 Excel 导入和导出)
 */
@Data
public class MdLocationVO {

    @ExcelColumn(name = "id")
    private String originalId;

    @ExcelColumn(name = "省份")
    private String originalProvince;

    @ExcelColumn(name = "名称")
    private String originalName;

    @ExcelColumn(name = "地址")
    private String originalAddress;

    // 以下为解析后的字段（导出时使用）
    private String areaName;            // 区县名称
    private String areaId;              // 区县编码
    private String lngLat;              // 经纬度
    private String province;            // 省份
    private String city;                // 城市
    private String batchId;             // 批次 ID
    private String createTime;          // 创建时间

    /**
     * 期望的 Excel 表头
     */
    public static final List<String> EXPECTED_HEADERS = new ArrayList<String>() {{
        add("id");
        add("省份");
        add("名称");
        add("地址");
    }};
}
