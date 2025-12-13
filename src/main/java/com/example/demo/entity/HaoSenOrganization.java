package com.example.demo.entity;

import lombok.Data;

@Data

public class HaoSenOrganization {
    // ========== 三个实体类都有的字段 ==========
    private String batchCode;
    private String dataCode;
    private String dataId;
    private String originalName;
    private String elianId;
    private String name;
    private String nameHistory;
    private String province;
    private String provinceId;
    private String city;
    private String cityId;
    private String area;
    private String areaId;
    private String address;
    private String remarks;
    private String updatetime;
    private String status;
    private String dataType;
    private String keyid;
    private String hsCode;
    private String addtime;
    private String isInsert;
    private String repeatId;
    private String appealDataId;
    private String field1;
    private String appealRemark;
    private String solveRemark;

    // ========== 医院和药店共有的字段 ==========
    private String usci;
    private String ybcode;
    private String validity;
    private String isCity;

    // ========== 医院特有字段 ==========
    private String orgType;
    private String level;
    private String grade;
    private String publicflag;
    private String hosClass;
    private String hosBigClass;
    private String class1;
    private String class2;
    private String class3;
    private String class4;
    private String class5;
    private String generalBranch;
    private String militaryHos;
    private String internetHos;
    private String medUnionCommunity;
    private String regcode;
    private String menzhen;
    private String bedCount;
    private String medicalCount;
    private String subjects;
    private String legalperson;
    private String kuAddress;
    private String generalBranchKid;
    private String generalBranchName;
    private String internetHosclassify;
    private String originalProvince;
    private String originalAddress;
    private String companyName;

    // ========== 药店特有字段 ==========
    private String historyName;
    private String pharmacyAture;
    private String operation;
    private String scope;
    private String location;
    private String mainBranch;
    private String twoChannels;
    private String isInternet;
    private String dtp;
    private String licenseNumber;
    private String mztc;
    private String createDate;
    private String registCapi;
    private String operName;
    private String isYb;
    private String mainBranchKid;
    private String mainBranchName;
    private String signStatus;
    private String hosInside;
    private String hosOutside;

    // ========== 公司特有字段 ==========
    private String creditCode;
    private String econKind;
    private String industry;
    private String belong;
    private String classify;


}
