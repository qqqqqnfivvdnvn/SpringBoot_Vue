package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 总分院店编码导入 VO
 * Excel 表头：机构类型、易联编码、名称、豪森编码、省份
 */
@Data
public class BranchCodeImportVO {

    public static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "机构类型", "易联编码", "名称", "豪森编码", "省份"
    );

    @ExcelColumn(name = "机构类型")
    private String orgType;

    @ExcelColumn(name = "易联编码")
    private String keyid;

    @ExcelColumn(name = "名称")
    private String name;

    @ExcelColumn(name = "豪森编码")
    private String hsCode;

    @ExcelColumn(name = "省份")
    private String province;
}