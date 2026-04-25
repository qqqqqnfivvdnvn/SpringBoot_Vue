package com.example.demo.dto;

import lombok.Data;

/**
 * 豪森客户数据查询条件 DTO
 */
@Data
public class HaoSenKeHuDataConditionDTO {

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
     * 备注
     */
    private String remark;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 更新开始时间
     */
    private String updateStartTime;

    /**
     * 更新结束时间
     */
    private String updateEndTime;

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
     * 备注（复数形式）
     */
    private String remarks;

    /**
     * 查询类型：nameaddr-名称地址，internethos-互联网医院，bigclassify-医院大类，hscode-豪森编码
     */
    private String queryType;

    /**
     * 成员单位（豪森医疗联盟表使用）
     */
    private String memberUnit;

    /**
     * 牵头单位（豪森医疗联盟表使用）
     */
    private String leadingUnit;

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
