package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

/**
 * 主数据模糊匹配汇总数据 VO
 */
@Data
public class MdFuzzyMatchSummaryVO {
    @ExcelColumn(name = "原始 ID")
    private String originalId;

    @ExcelColumn(name = "批次 ID")
    private String batchId;

    @ExcelColumn(name = "原始省份")
    private String originalProvince;

    @ExcelColumn(name = "原始名称")
    private String originalName;

    @ExcelColumn(name = "原始地址")
    private String originalAddress;

    @ExcelColumn(name = "匹配 ID")
    private String keyid;

    @ExcelColumn(name = "匹配名称")
    private String name;

    @ExcelColumn(name = "匹配说明")
    private String remark;
}
