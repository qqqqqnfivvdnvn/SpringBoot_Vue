package com.example.demo.dto;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

/**
 * 模糊匹配 Excel 数据 DTO
 */
@Data
public class FuzzyMatchDataDTO {

    @ExcelColumn(name = "id")
    private String id;

    @ExcelColumn(name = "省份")
    private String province;

    @ExcelColumn(name = "名称")
    private String name;
}
