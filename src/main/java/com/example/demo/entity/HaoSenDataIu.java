package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("liuxiang_haosen.haosen_data_iu")
public class HaoSenDataIu {

    @TableId(
            value = "data_id",
            type = IdType.AUTO
    )
    private String dataId;              // data_id VARCHAR(200) PRIMARY KEY

    private String appealRemark;        // appeal_remark TEXT

    private String distributor;         // distributor TEXT

    private String originalDataName;    // original_data_name TEXT

    private String originalDataAddress; // original_data_address TEXT

    private String originalProvince;    // original_province TEXT

    private String keyid;               // keyid VARCHAR(200)

    private String orgName;             // org_name TEXT

    private String historyName;         // history_name TEXT

    private String province;            // province TEXT

    private String provinceId;          // province_id TEXT

    private String city;                // city TEXT

    private String cityId;              // city_id TEXT

    private String areaName;            // area_name TEXT

    private String areaId;              // area_id TEXT

    private String address;             // address TEXT

    private String orgLevel;            // org_level TEXT

    private String orgGrade;            // org_grade TEXT

    private String orgOwnerForm;        // org_owner_form TEXT

    private String orgFiveProperty;     // org_five_property TEXT

    private String superiorUnitKid;     // superior_unit_kid TEXT

    private String branchKid;           // branch_kid TEXT

    private String isPla;               // is_pla TEXT

    private String regCode;             // reg_code TEXT

    private String validity;            // validity TEXT

    private String medicalSubjects;     // medical_subjects TEXT

    private String orgLegalPerson;      // org_legal_person TEXT

    private String creditCode;          // credit_code TEXT

    private String businessModel;       // business_model TEXT

    private String scope;               // scope TEXT

    private String createDate;          // create_date TEXT

    private String registCapi;          // regist_capi TEXT

    private String econKind;            // econ_kind TEXT

    private String regStatus;           // reg_status TEXT

    private String industry;            // industry TEXT

    private String approvaOrg;          // approva_org TEXT

    private String superiorUnitName;    // superior_unit_name TEXT

    private String branchName;          // branch_name TEXT

    private String baseTypeName;        // base_type_name TEXT
}