package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.example.demo.vo.HaoSenCleanDataVO;
import com.example.demo.vo.HaoSenOrganizationVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HaoSenToExcel {

    // 默认批次大小
    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 清洗数据Excel导出模型
     */
    @Data
    public static class CleanDataExcelModel {
        @ExcelProperty("批次编号")
        private String batchCode;

        @ExcelProperty("data_id")
        private String dataId;

        @ExcelProperty("数据类型")
        private String dataType;

        @ExcelProperty("原始数据编码")
        private String dataCode;

        @ExcelProperty("原始数据名称")
        private String originalName;

        @ExcelProperty("省份")
        private String originalProvince;

        @ExcelProperty("原始数据地址")
        private String originalAddress;

        @ExcelProperty("经销商")
        private String companyName;

        @ExcelProperty("申诉备注")
        private String appealRemark;

        @ExcelProperty("申诉解决")
        private String solveRemark;

        @ExcelProperty("机构类型")
        private String orgType;

        @ExcelProperty("keyid")
        private String keyid;

        @ExcelProperty("医院名称")
        private String name;

        @ExcelProperty("历史名称")
        private String nameHistory;

        @ExcelProperty("省")
        private String province;

        @ExcelProperty("省ID")
        private String provinceId;

        @ExcelProperty("市")
        private String city;

        @ExcelProperty("市ID")
        private String cityId;

        @ExcelProperty("区县")
        private String area;

        @ExcelProperty("区县ID")
        private String areaId;

        @ExcelProperty("地址")
        private String address;

        @ExcelProperty("等级")
        private String level;

        @ExcelProperty("等次")
        private String grade;

        @ExcelProperty("所有制")
        private String publicflag;

        @ExcelProperty("类别")
        private String classify;

        @ExcelProperty("总分院kid")
        private String generalBranchKid;

        @ExcelProperty("总分院名称")
        private String generalBranchName;

        @ExcelProperty("军队医院")
        private String militaryHos;

        @ExcelProperty("登记号")
        private String regcode;

        @ExcelProperty("有效期")
        private String validity;

        @ExcelProperty("诊疗科室")
        private String subjects;

        @ExcelProperty("法人代表")
        private String legalperson;

        @ExcelProperty("统一社会信用代码")
        private String usci;

        @ExcelProperty("经营方式")
        private String operation;

        @ExcelProperty("经营范围")
        private String scope;

        @ExcelProperty("总分店kid")
        private String mainBranchKid;

        @ExcelProperty("总分店名称")
        private String mainBranchName;

        @ExcelProperty("成立时间")
        private String createDate;

        @ExcelProperty("注册资金")
        private String registCapi;

        @ExcelProperty("企业类型")
        private String econKind;

        @ExcelProperty("登记状态")
        private String signStatus;

        @ExcelProperty("所属行业")
        private String industry;

        @ExcelProperty("登记机关")
        private String belong;

        @ExcelProperty("豪森编码")
        private String hsCode;

        @ExcelProperty("库中状态")
        private String status;

        /**
         * 从 HaoSenCleanDataVO 转换
         */
        public static CleanDataExcelModel fromCleanDataVO(HaoSenCleanDataVO vo) {
            if (vo == null) return null;

            CleanDataExcelModel model = new CleanDataExcelModel();

            // 设置有的字段
            model.setBatchCode(vo.getBatchCode());
            model.setDataId(vo.getDataId());
            model.setDataType(vo.getDataType());
            model.setDataCode(vo.getDataCode());
            model.setOriginalName(vo.getOriginalName());
            model.setOriginalProvince(vo.getOriginalProvince());
            model.setOriginalAddress(vo.getOriginalAddress());
            model.setCompanyName(vo.getCompanyName());
            model.setHsCode(vo.getHaosenCode());

            // 状态转换
//            if (vo.getStatus() != null) {
//                switch (vo.getStatus()) {
//                    case 1: model.setStatus("数据正常"); break;
//                    case 2: model.setStatus("数据作废"); break;
//                    case 3: model.setStatus("无法清洗"); break;
//                    case 4: model.setStatus("禁用客户"); break;
//                    case 5: model.setStatus("数据重复"); break;
//                    default: model.setStatus(String.valueOf(vo.getStatus()));
//                }
//            } else {
//                model.setStatus("");
//            }

            // 其他字段设为空
            model.setAppealRemark("");
            model.setSolveRemark("");
            model.setOrgType("");
            model.setKeyid("");
            model.setName("");
            model.setNameHistory("");
            model.setProvince("");
            model.setProvinceId("");
            model.setCity("");
            model.setCityId("");
            model.setArea("");
            model.setAreaId("");
            model.setAddress("");
            model.setLevel("");
            model.setGrade("");
            model.setPublicflag("");
            model.setClassify("");
            model.setGeneralBranchKid("");
            model.setGeneralBranchName("");
            model.setMilitaryHos("");
            model.setRegcode("");
            model.setValidity("");
            model.setSubjects("");
            model.setLegalperson("");
            model.setUsci("");
            model.setOperation("");
            model.setScope("");
            model.setMainBranchKid("");
            model.setMainBranchName("");
            model.setCreateDate("");
            model.setRegistCapi("");
            model.setEconKind("");
            model.setSignStatus("");
            model.setIndustry("");
            model.setBelong("");

            return model;
        }
    }

    /**
     * 机构数据Excel导出模型
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class OrganizationExcelModel extends CleanDataExcelModel {

        public static OrganizationExcelModel fromOrganizationVO(HaoSenOrganizationVO vo) {
            if (vo == null) return null;

            OrganizationExcelModel model = new OrganizationExcelModel();

            // 设置所有字段
            model.setBatchCode(vo.getBatchCode());
            model.setDataId(vo.getDataId());
            model.setDataType(vo.getDataType());
            model.setDataCode(vo.getDataCode());
            model.setOriginalName(vo.getOriginalName());
            model.setOriginalProvince(vo.getOriginalProvince());
            model.setOriginalAddress(vo.getOriginalAddress());
            model.setCompanyName(vo.getCompanyName());
            model.setAppealRemark(vo.getAppealRemark() != null ? vo.getAppealRemark() : "");
            model.setSolveRemark(vo.getSolveRemark() != null ? vo.getSolveRemark() : "");
            model.setOrgType(vo.getOrgType() != null ? vo.getOrgType() : "");
            model.setKeyid(vo.getKeyid() != null ? vo.getKeyid() : "");
            model.setName(vo.getName() != null ? vo.getName() : "");
            model.setNameHistory(vo.getNameHistory() != null ? vo.getNameHistory() : "");
            model.setProvince(vo.getProvince() != null ? vo.getProvince() : "");
            model.setProvinceId(vo.getProvinceId() != null ? vo.getProvinceId() : "");
            model.setCity(vo.getCity() != null ? vo.getCity() : "");
            model.setCityId(vo.getCityId() != null ? vo.getCityId() : "");
            model.setArea(vo.getArea() != null ? vo.getArea() : "");
            model.setAreaId(vo.getAreaId() != null ? vo.getAreaId() : "");
            model.setAddress(vo.getAddress() != null ? vo.getAddress() : "");
            model.setLevel(vo.getLevel() != null ? vo.getLevel() : "");
            model.setGrade(vo.getGrade() != null ? vo.getGrade() : "");
            model.setPublicflag(vo.getPublicflag() != null ? vo.getPublicflag() : "");
            model.setClassify(vo.getClassify() != null ? vo.getClassify() : "");
            model.setGeneralBranchKid(vo.getGeneralBranchKid() != null ? vo.getGeneralBranchKid() : "");
            model.setGeneralBranchName(vo.getGeneralBranchName() != null ? vo.getGeneralBranchName() : "");
            model.setMilitaryHos(vo.getMilitaryHos() != null ? vo.getMilitaryHos() : "");
            model.setRegcode(vo.getRegcode() != null ? vo.getRegcode() : "");
            model.setValidity(vo.getValidity() != null ? vo.getValidity() : "");
            model.setSubjects(vo.getSubjects() != null ? vo.getSubjects() : "");
            model.setLegalperson(vo.getLegalperson() != null ? vo.getLegalperson() : "");
            model.setUsci(vo.getUsci() != null ? vo.getUsci() : "");
            model.setOperation(vo.getOperation() != null ? vo.getOperation() : "");
            model.setScope(vo.getScope() != null ? vo.getScope() : "");
            model.setMainBranchKid(vo.getMainBranchKid() != null ? vo.getMainBranchKid() : "");
            model.setMainBranchName(vo.getMainBranchName() != null ? vo.getMainBranchName() : "");
            model.setCreateDate(vo.getCreateDate() != null ? vo.getCreateDate() : "");
            model.setRegistCapi(vo.getRegistCapi() != null ? vo.getRegistCapi() : "");
            model.setEconKind(vo.getEconKind() != null ? vo.getEconKind() : "");
            model.setSignStatus(vo.getSignStatus() != null ? vo.getSignStatus() : "");
            model.setIndustry(vo.getIndustry() != null ? vo.getIndustry() : "");
            model.setBelong(vo.getBelong() != null ? vo.getBelong() : "");
            model.setHsCode(vo.getHsCode() != null ? vo.getHsCode() : "");

            // 状态转换
            if (vo.getStatus() != null) {
                switch (vo.getStatus()) {
                    case "1": model.setStatus("数据正常"); break;
                    case "2": model.setStatus("数据作废"); break;
                    case "3": model.setStatus("无法清洗"); break;
                    case "4": model.setStatus("禁用客户"); break;
                    case "5": model.setStatus("数据重复"); break;
                    default: model.setStatus(vo.getStatus());
                }
            } else {
                model.setStatus("");
            }

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
     * 导出清洗数据到Excel（自动分批次）
     */

    public byte[] exportCleanDataExcel(List<HaoSenCleanDataVO> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(out, CleanDataExcelModel.class)
                     .registerWriteHandler(createPlainStyle())  // 应用普通样式
                     .autoCloseStream(false)
                     .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            // 分批次处理
            int total = dataList.size();
            for (int i = 0; i < total; i += DEFAULT_BATCH_SIZE) {
                int end = Math.min(i + DEFAULT_BATCH_SIZE, total);
                List<HaoSenCleanDataVO> batch = dataList.subList(i, end);

                List<CleanDataExcelModel> modelBatch = new ArrayList<>(batch.size());
                for (HaoSenCleanDataVO data : batch) {
                    CleanDataExcelModel model = CleanDataExcelModel.fromCleanDataVO(data);
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
            throw new RuntimeException("导出清洗数据Excel失败", e);
        }
    }



    /**
     * 导出机构数据到Excel（自动分批次）
     */
    public byte[] exportConditionToExcel(List<HaoSenOrganizationVO> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(out, OrganizationExcelModel.class)
                     .registerWriteHandler(createPlainStyle())  // 应用普通样式
                     .autoCloseStream(false)
                     .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            // 分批次处理
            int total = dataList.size();
            for (int i = 0; i < total; i += DEFAULT_BATCH_SIZE) {
                int end = Math.min(i + DEFAULT_BATCH_SIZE, total);
                List<HaoSenOrganizationVO> batch = dataList.subList(i, end);

                List<OrganizationExcelModel> modelBatch = new ArrayList<>(batch.size());
                for (HaoSenOrganizationVO data : batch) {
                    OrganizationExcelModel model = OrganizationExcelModel.fromOrganizationVO(data);
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
            throw new RuntimeException("导出机构数据Excel失败", e);
        }
    }






}