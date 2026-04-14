package com.example.demo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 主数据模糊匹配汇总数据 VO
 */
@Data
public class MdFuzzyMatchSummaryVO {
    @ExcelProperty(value = "id", index = 0)
    private String originalId;

    @ExcelProperty(value = "批次 ID", index = 1)
    private String batchId;

    @ExcelProperty(value = "省份", index = 2)
    private String originalProvince;

    @ExcelProperty(value = "名称", index = 3)
    private String originalName;

    @ExcelProperty(value = "keyid", index = 4)
    private String keyid;

    @ExcelProperty(value = "标准名称", index = 5)
    private String name;

    @ExcelProperty(value = "历史名称", index = 6)
    private String namehistory;

    @ExcelProperty(value = "省", index = 7)
    private String province;

    @ExcelProperty(value = "市", index = 8)
    private String cityname;

    @ExcelProperty(value = "区", index = 9)
    private String areaname;

    @ExcelProperty(value = "地址", index = 10)
    private String address;

    @ExcelProperty(value = "负责人", index = 11)
    private String principal;

    @ExcelProperty(value = "法人", index = 12)
    private String legalperson;

    @ExcelProperty(value = "登记状态", index = 13)
    private String sign_status;

    @ExcelProperty(value = "大库状态", index = 14)
    private String status;
}
