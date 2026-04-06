package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.example.demo.vo.HaoSenCheckDuplicateDataVO;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HaoSenToDuplicateExcel {

    // 默认批次大小
    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 重复数据Excel导出模型
     */
    @Data
    public static class DuplicateDataExcelModel {
        @ExcelProperty("机构类型")
        private String orgType;

        @ExcelProperty("dataId")
        private String dataId;

        @ExcelProperty("原始名称")
        private String originalName;

        @ExcelProperty("原始编码")
        private String dataCode;

        @ExcelProperty("keyid")
        private String keyid;

        @ExcelProperty("省份")
        private String province;

        @ExcelProperty("标准名称")
        private String name;

        @ExcelProperty("历史名称")
        private String nameHistory;

        @ExcelProperty("地址")
        private String address;

        @ExcelProperty("豪森编码")
        private String hsCode;

        @ExcelProperty("添加时间")
        private String addtime;


        @ExcelProperty("是否需易联禁用这条对应关系")
        private String ylRemark;

        @ExcelProperty("登记状态")
        private String signStatus;

        @ExcelProperty("备注")
        private String remark;

        /**
         * 从 HaoSenCheckDuplicateDataVO 转换
         */
        public static DuplicateDataExcelModel fromDuplicateDataVO(HaoSenCheckDuplicateDataVO vo) {
            if (vo == null) return null;

            DuplicateDataExcelModel model = new DuplicateDataExcelModel();

            model.setOrgType(vo.getOrgType() != null ? vo.getOrgType() : "");
            model.setDataId(vo.getDataId() != null ? vo.getDataId() : "");
            model.setOriginalName(vo.getOriginalName() != null ? vo.getOriginalName() : "");
            model.setDataCode(vo.getDataCode() != null ? vo.getDataCode() : "");
            model.setKeyid(vo.getKeyid() != null ? vo.getKeyid() : "");
            model.setProvince(vo.getProvince() != null ? vo.getProvince() : "");
            model.setName(vo.getName() != null ? vo.getName() : "");
            model.setNameHistory(vo.getNameHistory() != null ? vo.getNameHistory() : "");
            model.setAddress(vo.getAddress() != null ? vo.getAddress() : "");
            model.setHsCode(vo.getHsCode() != null ? vo.getHsCode() : "");
            model.setAddtime(vo.getAddtime() != null ? vo.getAddtime() : "");
            model.setYlRemark(vo.getYlRemark() != null ? vo.getYlRemark() : "");
            model.setSignStatus(vo.getSignStatus() != null ? vo.getSignStatus() : "");
            model.setRemark(vo.getRemark() != null ? vo.getRemark() : "");

            return model;
        }
    }

    /**
     * 创建普通样式（表头和数据都使用同样的样式）
     */
    private HorizontalCellStyleStrategy createPlainStyle() {
        // 设置数据行的样式（普通字体，不加粗）
        WriteCellStyle contentStyle = new WriteCellStyle();
        WriteFont contentFont = new WriteFont();
        contentFont.setFontName("宋体");  // 使用宋体，看起来更普通
        contentFont.setFontHeightInPoints((short) 11);
        contentFont.setBold(false);  // 不加粗
        contentStyle.setWriteFont(contentFont);

        // 表头使用同样的样式（不加粗）
        WriteCellStyle headStyle = new WriteCellStyle();
        WriteFont headFont = new WriteFont();
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 11);
        headFont.setBold(true);  // 表头加粗
        headStyle.setWriteFont(headFont);

        return new HorizontalCellStyleStrategy(headStyle, contentStyle);
    }

    /**
     * 导出重复数据到Excel（自动分批次）
     */

    public byte[] exportDuplicateDataExcel(List<HaoSenCheckDuplicateDataVO> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(out, DuplicateDataExcelModel.class)
                     .registerWriteHandler(createPlainStyle())  // 应用普通样式
                     .autoCloseStream(false)
                     .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            // 分批次处理
            int total = dataList.size();
            for (int i = 0; i < total; i += DEFAULT_BATCH_SIZE) {
                int end = Math.min(i + DEFAULT_BATCH_SIZE, total);
                List<HaoSenCheckDuplicateDataVO> batch = dataList.subList(i, end);

                List<DuplicateDataExcelModel> modelBatch = new ArrayList<>(batch.size());
                for (HaoSenCheckDuplicateDataVO data : batch) {
                    DuplicateDataExcelModel model = DuplicateDataExcelModel.fromDuplicateDataVO(data);
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
            throw new RuntimeException("导出重复数据Excel失败", e);
        }
    }
}
