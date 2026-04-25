package com.example.demo.entity;

import lombok.Data;

/**
 * 豪森客户数据实体类
 * 对应数据库表：test..haosen_name_addr
 */
@Data
public class HaoSenKeHuData {

    /**
     * 主键 ID
     */
    private String keyid;

    /**
     * 名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 字段信息（JSON 或其他格式）
     */
    private String field;

    /**
     * 备注
     */
    private String remark;

    /**
     * 添加时间
     */
    private String addTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 是否互联网医院
     */
    private String internetHos;

    /**
     * 互联网医院类别
     */
    private String internetHosClassify;

    /**
     * 医院大类
     */
    private String bigClassify;

    /**
     * 豪森编码
     */
    private String hsCode;

    /**
     * 原始编码（豪森线下编码表使用）
     */
    private String dataCode;

    /**
     * 原始名称（豪森线下编码表使用）
     */
    private String originalName;

    /**
     * 备注（复数形式，用于豪森编码表）
     */
    private String remarks;

    /**
     * 成员单位（豪森医疗联盟表使用）
     */
    private String memberUnit;

    /**
     * 成员单位 keyid（豪森医疗联盟表使用）
     */
    private String memberUnitKeyid;

    /**
     * 牵头单位（豪森医疗联盟表使用）
     */
    private String leadingUnit;

    /**
     * 牵头单位 keyid（豪森医疗联盟表使用）
     */
    private String leadingUnitKeyid;

    /**
     * 类型（豪森医疗联盟表使用）
     */
    private String cooperationType;

    /**
     * 省份（豪森医疗联盟表使用）
     */
    private String province;

    /**
     * 数据返回类型（豪森返回编码表使用）
     */
    private String feedbackType;

    /**
     * 批次编号（豪森返回编码表使用）
     */
    private String batchCode;

    /**
     * 状态（豪森返回编码表使用）
     */
    private String status;
}
