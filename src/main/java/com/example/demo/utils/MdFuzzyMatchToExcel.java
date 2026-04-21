package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.example.demo.entity.MdFuzzyMatchSummary;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 主数据模糊匹配 Excel 导出工具类
 */
@Component
public class MdFuzzyMatchToExcel {

    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 模糊匹配导出模型
     */
    @Data
    public static class MdFuzzyMatchExcelModel {
        @ExcelProperty("id")
        private String originalId;

        @ExcelProperty("批次ID")
        private String batchId;

        @ExcelProperty("省份")
        private String originalProvince;

        @ExcelProperty("名称")
        private String originalName;

        @ExcelProperty("keyid")
        private String keyid;

        @ExcelProperty("标准名称")
        private String name;

        @ExcelProperty("历史名称")
        private String namehistory;

        @ExcelProperty("省")
        private String province;

        @ExcelProperty("市")
        private String cityname;

        @ExcelProperty("区")
        private String areaname;

        @ExcelProperty("地址")
        private String address;

        @ExcelProperty("负责人")
        private String principal;

        @ExcelProperty("法人")
        private String legalperson;

        @ExcelProperty("登记状态")
        private String sign_status;

        @ExcelProperty("大库状态")
        private String status;

        public static MdFuzzyMatchExcelModel fromEntity(MdFuzzyMatchSummary summary) {
            if (summary == null) return null;

            MdFuzzyMatchExcelModel model = new MdFuzzyMatchExcelModel();
            model.setOriginalId(summary.getOriginalId());
            model.setBatchId(summary.getBatchId());
            model.setOriginalProvince(summary.getOriginalProvince());
            model.setOriginalName(summary.getOriginalName());
            model.setKeyid(summary.getKeyid());
            model.setName(summary.getName());
            model.setNamehistory(summary.getNamehistory());
            model.setProvince(summary.getProvince());
            model.setCityname(summary.getCityname());
            model.setAreaname(summary.getAreaname());
            model.setAddress(summary.getAddress());
            model.setPrincipal(summary.getPrincipal());
            model.setLegalperson(summary.getLegalperson());
            model.setSign_status(summary.getSign_status());
            model.setStatus(summary.getStatus());
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
     * 导出模糊匹配数据到 Excel（使用 Entity 列表）
     */
    public byte[] exportFuzzyMatchExcelFromEntity(List<MdFuzzyMatchSummary> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(out, MdFuzzyMatchExcelModel.class)
                     .registerWriteHandler(createPlainStyle())
                     .autoCloseStream(false)
                     .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            int total = dataList.size();
            for (int i = 0; i < total; i += DEFAULT_BATCH_SIZE) {
                int end = Math.min(i + DEFAULT_BATCH_SIZE, total);
                List<MdFuzzyMatchSummary> batch = dataList.subList(i, end);

                List<MdFuzzyMatchExcelModel> modelBatch = new ArrayList<>(batch.size());
                for (MdFuzzyMatchSummary summary : batch) {
                    MdFuzzyMatchExcelModel model = MdFuzzyMatchExcelModel.fromEntity(summary);
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
            throw new RuntimeException("导出模糊匹配数据 Excel 失败", e);
        }
    }
}
