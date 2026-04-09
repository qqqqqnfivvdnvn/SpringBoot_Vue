package com.example.demo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 恒瑞批次导出数据 VO
 */
@Data
public class HrBatchExportVO {

    @ExcelProperty(value = "序号", index = 0)
    private String serialNo;

    @ExcelProperty(value = "监测年份", index = 1)
    private String monitoringYear;

    @ExcelProperty(value = "ID", index = 2)
    private String id;

    @ExcelProperty(value = "商品ID", index = 3)
    private String productId;

    @ExcelProperty(value = "商品名", index = 4)
    private String productName;

    @ExcelProperty(value = "平台", index = 5)
    private String platform;

    @ExcelProperty(value = "通用名", index = 6)
    private String genericName;

    @ExcelProperty(value = "品名简称", index = 7)
    private String shortName;

    @ExcelProperty(value = "规格", index = 8)
    private String specification;

    @ExcelProperty(value = "网店价格（元）", index = 9)
    private String onlineStorePrice;

    @ExcelProperty(value = "盒数", index = 10)
    private String boxQuantity;

    @ExcelProperty(value = "单盒价", index = 11)
    private String unitPricePerBox;

    @ExcelProperty(value = "店铺名称", index = 12)
    private String storeName;

    @ExcelProperty(value = "营业执照名称", index = 13)
    private String businessLicenseName;

    @ExcelProperty(value = "省", index = 14)
    private String province;

    @ExcelProperty(value = "市", index = 15)
    private String city;

    @ExcelProperty(value = "销量", index = 16)
    private String salesVolume;

    @ExcelProperty(value = "分类", index = 17)
    private String category;

    @ExcelProperty(value = "新增时间", index = 18)
    private String createdTime;

    @ExcelProperty(value = "爬取时间", index = 19)
    private String crawledTime;

    @ExcelProperty(value = "反复次数", index = 20)
    private String repeatedCount;

    @ExcelProperty(value = "发货地", index = 21)
    private String shippingOrigin;

    @ExcelProperty(value = "keyid", index = 22)
    private String keyId;

    @ExcelProperty(value = "标准名称", index = 23)
    private String name;

    @ExcelProperty(value = "地址", index = 24)
    private String address;

    @ExcelProperty(value = "链接", index = 25)
    private String url;

    @ExcelProperty(value = "省份标化", index = 26)
    private String standardizedProvince;

    @ExcelProperty(value = "备注", index = 27)
    private String remarks;
}
