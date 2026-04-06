package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class HaoSenDuplicateDataVO {
//    机构类型	dataId	原始名称	原始编码	keyid	省份	标准名称	历史名称	地址	豪森编码	添加时间	是否需易联禁用这条对应关系	登记状态	备注

    public static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "机构类型", "dataId", "原始名称", "原始编码", "keyid", "省份", "标准名称", "历史名称", "地址", "豪森编码", "添加时间", "是否需易联禁用这条对应关系", "登记状态", "备注"
    );

    @ExcelColumn(name = "机构类型")
    private String orgType;

    @ExcelColumn(name = "dataId")
    private String dataId;

    @ExcelColumn(name = "原始名称")
    private String originalName;

    @ExcelColumn(name = "原始编码")
    private String dataCode;

    @ExcelColumn(name = "keyid")
    private String keyid;

    @ExcelColumn(name = "省份")
    private String province;

    @ExcelColumn(name = "标准名称")
    private String name;

    @ExcelColumn(name = "豪森编码")
    private String hsCode;

    @ExcelColumn(name = "添加时间")
    private String addtime;

    @ExcelColumn(name = "历史名称")
    private String nameHistory;

    @ExcelColumn(name = "地址")
    private String address;

    @ExcelColumn(name = "是否需易联禁用这条对应关系")
    private String ylRemark;

    @ExcelColumn(name = "登记状态")
    private String signStatus;

    @ExcelColumn(name = "备注")
    private String remark;



}
