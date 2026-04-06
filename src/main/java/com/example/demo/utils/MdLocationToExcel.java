package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.example.demo.vo.MdLocationExportVO;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 主数据地理位置项目 Excel 导出工具类
 */
public class MdLocationToExcel {

    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 地理位置导出模型
     */
    @Data
    public static class MdLocationExcelModel {
        @ExcelProperty("id")
        private String id;

        @ExcelProperty("名称")
        private String originalName;

        @ExcelProperty("省份")
        private String originalProvince;

        @ExcelProperty("地址")
        private String originalAddress;

        @ExcelProperty("经纬度")
        private String lngLat;

        @ExcelProperty("高德省份")
        private String provName;

        @ExcelProperty("高德省 id")
        private String provId;

        @ExcelProperty("高德市")
        private String cityName;

        @ExcelProperty("高德市 id")
        private String cityId;

        @ExcelProperty("高德区县")
        private String countyName;

        @ExcelProperty("高德区县 id")
        private String countyId;

        public static MdLocationExcelModel fromVO(MdLocationExportVO vo) {
            if (vo == null) return null;

            MdLocationExcelModel model = new MdLocationExcelModel();
            model.setId(vo.getId());
            model.setOriginalName(vo.getOriginalName());
            model.setOriginalProvince(vo.getOriginalProvince());
            model.setOriginalAddress(vo.getOriginalAddress());
            model.setLngLat(vo.getLngLat());
            model.setProvName(vo.getProvName());
            model.setProvId(vo.getProvId());
            model.setCityName(vo.getCityName());
            model.setCityId(vo.getCityId());
            model.setCountyName(vo.getCountyName());
            model.setCountyId(vo.getCountyId());
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
     * 导出地理位置数据到 Excel
     */
    public byte[] exportLocationExcel(List<MdLocationExportVO> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(out, MdLocationExcelModel.class)
                     .registerWriteHandler(createPlainStyle())
                     .autoCloseStream(false)
                     .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            int total = dataList.size();
            for (int i = 0; i < total; i += DEFAULT_BATCH_SIZE) {
                int end = Math.min(i + DEFAULT_BATCH_SIZE, total);
                List<MdLocationExportVO> batch = dataList.subList(i, end);

                List<MdLocationExcelModel> modelBatch = new ArrayList<>(batch.size());
                for (MdLocationExportVO vo : batch) {
                    MdLocationExcelModel model = MdLocationExcelModel.fromVO(vo);
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
            throw new RuntimeException("导出地理位置数据 Excel 失败", e);
        }
    }
}
