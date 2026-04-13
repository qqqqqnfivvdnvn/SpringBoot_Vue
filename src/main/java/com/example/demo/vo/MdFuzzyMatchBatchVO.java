package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

/**
 * 主数据模糊匹配批次 VO
 */
@Data
public class MdFuzzyMatchBatchVO {
    @ExcelColumn(name = "批次 ID")
    private String batchId;

    @ExcelColumn(name = "文件名称")
    private String originalFilename;

    @ExcelColumn(name = "状态")
    private Integer status;

    @ExcelColumn(name = "处理信息")
    private String message;

    @ExcelColumn(name = "创建时间")
    private String createTime;

    @ExcelColumn(name = "更新时间")
    private String updateTime;
}
