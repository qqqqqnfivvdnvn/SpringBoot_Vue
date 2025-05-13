package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

@Data
public class HaoSenInputAppealDataVO {

    @ExcelColumn(name = "批次编号")
    private String batchCode;

    @ExcelColumn(name = "data_id")
    private String dataId;

    @ExcelColumn(name = "机构类型")
    private String dataType;

    @ExcelColumn(name = "原始数据编码")
    private String dataCode;

    @ExcelColumn(name = "原始数据名称")
    private String originalName;

    @ExcelColumn(name = "省份")
    private String originalProvince;

    @ExcelColumn(name = "original_address")
    private String originalAddress;

    @ExcelColumn(name = "经销商")
    private String companyName;

    @ExcelColumn(name = "申诉备注")
    private String appealRemark;

    @ExcelColumn(name = "申诉解决")
    private String solveRemark;
    @ExcelColumn(name = "机构类型")
    private String institutionType;
    @ExcelColumn(name = "keyid")
    private String keyid;
    @ExcelColumn(name = "医院名称")
    private String name;
    @ExcelColumn(name = "历史名称")
    private String nameHistory;
    @ExcelColumn(name = "省")
    private String province;
    @ExcelColumn(name = "省ID")
    private String provinceid;
    @ExcelColumn(name = "市")
    private String city;
    @ExcelColumn(name = "市ID")
    private String cityid;
    @ExcelColumn(name = "区县")
    private String area;
    @ExcelColumn(name = "区县ID")
    private String areaid;
    @ExcelColumn(name = "地址")
    private String kuAddress;
    @ExcelColumn(name = "等级")
    private String level;
    @ExcelColumn(name = "等次")
    private String grade;
    @ExcelColumn(name = "所有制")
    private String publicflag;
    @ExcelColumn(name = "类别")
    private String classify;
    @ExcelColumn(name = "总分院kid")
    private String generalBranchKid;
    @ExcelColumn(name = "总分院名称")
    private String generalBranchName;
    @ExcelColumn(name = "军队医院")
    private String militaryHos;
    @ExcelColumn(name = "登记号")
    private String regcode;
    @ExcelColumn(name = "有效期")
    private String validity;
    @ExcelColumn(name = "诊疗科室")
    private String subjects;
    @ExcelColumn(name = "法人代表")
    private String legalperson;
    @ExcelColumn(name = "统一社会信用代码")
    private String usci;
    @ExcelColumn(name = "经营方式")
    private String operation;
    @ExcelColumn(name = "经营范围")
    private String scope;
    @ExcelColumn(name = "总分店kid")
    private String mainBranchKid;
    @ExcelColumn(name = "总分店名称")
    private String mainBranchName;
    @ExcelColumn(name = "成立时间")
    private String createDate;
    @ExcelColumn(name = "注册资金")
    private String registCapi;
    @ExcelColumn(name = "企业类型")
    private String econKind;
    @ExcelColumn(name = "登记状态")
    private String signStatus;
    @ExcelColumn(name = "所属行业")
    private String industry;
    @ExcelColumn(name = "登记机关")
    private String belong;

}
