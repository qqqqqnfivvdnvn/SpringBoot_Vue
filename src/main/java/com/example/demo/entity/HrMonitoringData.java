package com.example.demo.entity;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 恒瑞数据汇总表
 */
@Data
public class HrMonitoringData {
    private String batchId;

    @ExcelColumn(name = "序号")
    private String serialNo;

    @ExcelColumn(name = "监测年份")
    private String monitoringYear;

    @ExcelColumn(name = "ID")
    private String id;

    @ExcelColumn(name = "商品ID")
    private String productId;

    @ExcelColumn(name = "商品名")
    private String productName;

    @ExcelColumn(name = "平台")
    private String platform;

    @ExcelColumn(name = "通用名")
    private String genericName;

    @ExcelColumn(name = "链接")
    private String url;

    @ExcelColumn(name = "规格")
    private String specification;

    @ExcelColumn(name = "网店价格（元）")
    private String onlineStorePrice;

    @ExcelColumn(name = "盒数")
    private String boxQuantity;

    @ExcelColumn(name = "单盒价")
    private String unitPricePerBox;

    @ExcelColumn(name = "店铺名称")
    private String storeName;

    @ExcelColumn(name = "营业执照名称")
    private String businessLicenseName;

    @ExcelColumn(name = "省")
    private String province;

    @ExcelColumn(name = "市")
    private String city;

    @ExcelColumn(name = "销量")
    private String salesVolume;

    @ExcelColumn(name = "分类")
    private String category;

    @ExcelColumn(name = "新增时间")
    private String createdTime;

    @ExcelColumn(name = "爬取时间")
    private String crawledTime;

    @ExcelColumn(name = "反复次数")
    private String repeatedCount;

    @ExcelColumn(name = "发货地")
    private String shippingOrigin;

    @ExcelColumn(name = "keyid")
    private String keyId;

    @ExcelColumn(name = "标准名称")
    private String name;

    @ExcelColumn(name = "地址")
    private String address;

    @ExcelColumn(name = "省份标化")
    private String standardizedProvince;

    @ExcelColumn(name = "品名简称")
    private String shortName;

    @ExcelColumn(name = "备注")
    private String remarks;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
