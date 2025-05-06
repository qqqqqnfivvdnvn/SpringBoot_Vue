package com.example.demo.vo;

import lombok.Data;

@Data
public class AppealDataVO {
    private String batchCode;          // batch_code → batchCode
    private String dataId;             // data_id → dataId
    private String dataType;           // data_type → dataType
    private String dataCode;           // data_code → dataCode
    private String originalName;       // original_name → originalName
    private String originalProvince;   // original_province → originalProvince
    private String originalAddress;
    private String companyName;        // company_name → companyName
    private String appealRemark;       // appeal_remark → appealRemark
    private String solveRemark;        // solve_remark → solveRemark
    private String institutionType;    // institution_type → institutionType
    private String keyid;              // keyid → keyId (更符合驼峰规则)
    private String name;
    private String nameHistory;        // name_history → nameHistory
    private String provicne;           // provicne → province (修正拼写错误)
    private String provicneid;         // provicneid → provinceId (修正拼写错误)
    private String city;
    private String cityid;             // cityid → cityId
    private String area;
    private String areaid;             // areaid → areaId
    private String kuAddress;          // ku_address → kuAddress
    private String level;
    private String grade;
    private String publicflag;         // publicflag → publicFlag
    private String classify;
    private String generalBranchKid;   // general_branch_kid → generalBranchKid
    private String generalBranchName;  // general_branch_name → generalBranchName
    private String militaryHos;        // military_hos → militaryHos
    private String regcode;            // regcode → regCode
    private String validity;
    private String subjects;
    private String legalperson;        // legalperson → legalPerson
    private String usci;               // USCI 是专有名词，可保持原样
    private String operation;
    private String scope;
    private String mainBranchKid;      // main_branch_kid → mainBranchKid
    private String mainBranchName;     // main_branch_name → mainBranchName
    private String createDate;         // create_date → createDate
    private String registCapi;         // regist_capi → registCapi
    private String econKind;           // econ_kind → econKind
    private String signStatus;         // sign_status → signStatus
    private String industry;
    private String belong;


}