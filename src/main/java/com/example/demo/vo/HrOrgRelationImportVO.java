package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 恒瑞比对关系导入 VO
 */
@Data
public class HrOrgRelationImportVO {

    public static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "营业执照名称", "省份标化", "keyid", "标准名称", "地址"
    );

    @ExcelColumn(name = "营业执照名称")
    private String businessLicenseName;

    @ExcelColumn(name = "省份标化")
    private String province;

    @ExcelColumn(name = "keyid")
    private String keyId;

    @ExcelColumn(name = "标准名称")
    private String name;

    @ExcelColumn(name = "地址")
    private String address;
}
