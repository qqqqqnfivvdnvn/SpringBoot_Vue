package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.example.demo.vo.HrMonitoringDataVO;
import com.example.demo.vo.HrOrgRelationVO;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 恒瑞项目 Excel 导出工具类
 */
public class HrToExcel {

    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 数据汇总导出模型
     */
    @Data
    public static class MonitoringDataExcelModel {
        @ExcelProperty("序号")
        private String serialNo;

        @ExcelProperty("监测年份")
        private String monitoringYear;

        @ExcelProperty("ID")
        private String id;

        @ExcelProperty("商品ID")
        private String productId;

        @ExcelProperty("商品名")
        private String productName;

        @ExcelProperty("平台")
        private String platform;

        @ExcelProperty("通用名")
        private String genericName;

        @ExcelProperty("品名简称")
        private String shortName;

        @ExcelProperty("规格")
        private String specification;

        @ExcelProperty("网店价格（元）")
        private String onlineStorePrice;

        @ExcelProperty("盒数")
        private String boxQuantity;

        @ExcelProperty("单盒价")
        private String unitPricePerBox;

        @ExcelProperty("店铺名称")
        private String storeName;

        @ExcelProperty("营业执照名称")
        private String businessLicenseName;

        @ExcelProperty("省")
        private String province;

        @ExcelProperty("市")
        private String city;

        @ExcelProperty("销量")
        private String salesVolume;

        @ExcelProperty("分类")
        private String category;

        @ExcelProperty("新增时间")
        private String createdTime;

        @ExcelProperty("爬取时间")
        private String crawledTime;

        @ExcelProperty("反复次数")
        private String repeatedCount;

        @ExcelProperty("发货地")
        private String shippingOrigin;

        @ExcelProperty("地址")
        private String address;

        @ExcelProperty("链接")
        private String url;

        @ExcelProperty("备注")
        private String remarks;

        @ExcelProperty("keyid")
        private String keyId;

        @ExcelProperty("标准名称")
        private String name;

        @ExcelProperty("省份标化")
        private String standardizedProvince;

        public static MonitoringDataExcelModel fromVO(HrMonitoringDataVO vo) {
            if (vo == null) return null;

            MonitoringDataExcelModel model = new MonitoringDataExcelModel();
            model.setSerialNo(vo.getSerialNo());
            model.setMonitoringYear(vo.getMonitoringYear());
            model.setId(vo.getId());
            model.setProductId(vo.getProductId());
            model.setProductName(vo.getProductName());
            model.setPlatform(vo.getPlatform());
            model.setGenericName(vo.getGenericName());
            model.setShortName(vo.getShortName());
            model.setSpecification(vo.getSpecification());
            model.setOnlineStorePrice(vo.getOnlineStorePrice());
            model.setBoxQuantity(vo.getBoxQuantity());
            model.setUnitPricePerBox(vo.getUnitPricePerBox());
            model.setStoreName(vo.getStoreName());
            model.setBusinessLicenseName(vo.getBusinessLicenseName());
            model.setProvince(vo.getProvince());
            model.setCity(vo.getCity());
            model.setSalesVolume(vo.getSalesVolume());
            model.setCategory(vo.getCategory());
            model.setCreatedTime(vo.getCreatedTime());
            model.setCrawledTime(vo.getCrawledTime());
            model.setRepeatedCount(vo.getRepeatedCount());
            model.setShippingOrigin(vo.getShippingOrigin());
            model.setAddress(vo.getAddress());
            model.setUrl(vo.getUrl());
            model.setRemarks(vo.getRemarks());
            model.setKeyId(vo.getKeyId());
            model.setName(vo.getName());
            model.setStandardizedProvince(vo.getStandardizedProvince());
            return model;
        }
    }

    /**
     * 比对关系导出模型
     */
    @Data
    public static class OrgRelationExcelModel {
        @ExcelProperty("省份")
        private String province;

        @ExcelProperty("营业执照名称")
        private String businessLicenseName;

        @ExcelProperty("KeyID")
        private String keyId;

        @ExcelProperty("标准名称")
        private String name;

        @ExcelProperty("地址")
        private String address;

        public static OrgRelationExcelModel fromVO(HrOrgRelationVO vo) {
            if (vo == null) return null;

            OrgRelationExcelModel model = new OrgRelationExcelModel();
            model.setProvince(vo.getProvince());
            model.setBusinessLicenseName(vo.getBusinessLicenseName());
            model.setKeyId(vo.getKeyId());
            model.setName(vo.getName());
            model.setAddress(vo.getAddress());
            return model;
        }
    }

    /**
     * 创建普通样式
     */
    private HorizontalCellStyleStrategy createPlainStyle() {
        WriteCellStyle contentStyle = new WriteCellStyle();
        WriteFont contentFont = new WriteFont();
        contentFont.setFontName("宋体");
        contentFont.setFontHeightInPoints((short) 11);
        contentFont.setBold(false);
        contentStyle.setWriteFont(contentFont);

        WriteCellStyle headStyle = new WriteCellStyle();
        WriteFont headFont = new WriteFont();
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 11);
        headFont.setBold(true);
        headStyle.setWriteFont(headFont);

        return new HorizontalCellStyleStrategy(headStyle, contentStyle);
    }

    /**
     * 导出数据汇总到 Excel
     */
    public byte[] exportMonitoringDataExcel(List<HrMonitoringDataVO> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(out, MonitoringDataExcelModel.class)
                     .registerWriteHandler(createPlainStyle())
                     .autoCloseStream(false)
                     .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            int total = dataList.size();
            for (int i = 0; i < total; i += DEFAULT_BATCH_SIZE) {
                int end = Math.min(i + DEFAULT_BATCH_SIZE, total);
                List<HrMonitoringDataVO> batch = dataList.subList(i, end);

                List<MonitoringDataExcelModel> modelBatch = new ArrayList<>(batch.size());
                for (HrMonitoringDataVO vo : batch) {
                    MonitoringDataExcelModel model = MonitoringDataExcelModel.fromVO(vo);
                    if (model != null) {
                        modelBatch.add(model);
                    }
                }

                excelWriter.write(modelBatch, writeSheet);
                modelBatch.clear();
            }

            excelWriter.finish();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("导出数据汇总 Excel 失败", e);
        }
    }

    /**
     * 导出比对关系到 Excel
     */
    public byte[] exportOrgRelationExcel(List<HrOrgRelationVO> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(out, OrgRelationExcelModel.class)
                     .registerWriteHandler(createPlainStyle())
                     .autoCloseStream(false)
                     .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            List<OrgRelationExcelModel> modelList = new ArrayList<>(dataList.size());
            for (HrOrgRelationVO vo : dataList) {
                OrgRelationExcelModel model = OrgRelationExcelModel.fromVO(vo);
                if (model != null) {
                    modelList.add(model);
                }
            }

            excelWriter.write(modelList, writeSheet);
            excelWriter.finish();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("导出比对关系 Excel 失败", e);
        }
    }
}
