package com.example.demo.utils;

import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.vo.HaoSenAppealDataVO;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class WebToExcel {


    public byte[] exportToExcel(List<HaoSenAppealDataVO> dataList) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            // 创建工作表
            XSSFSheet sheet = workbook.createSheet("申诉数据");

            // 创建表头
            String[] headers = {
                    "批次编号", "data_id", "数据类型", "原始数据编码", "原始数据名称",
                    "省份", "original_address", "经销商", "申诉备注", "申诉解决",
                    "机构类型", "keyid", "医院名称", "历史名称", "省",
                    "省ID", "市", "市ID", "区县", "区县ID",
                    "地址", "等级", "等次", "所有制", "类别",
                    "总分院kid", "总分院名称", "军队医院", "登记号", "有效期",
                    "诊疗科室", "法人代表", "统一社会信用代码", "经营方式", "经营范围",
                    "总分店kid", "总分店名称", "成立时间", "注册资金", "企业类型",
                    "登记状态", "所属行业", "登记机关"
            };



            // 创建表头行
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }


            // 填充数据
            int rowNum = 1;
            for (HaoSenAppealDataVO data : dataList) {

                XSSFRow row = sheet.createRow(rowNum++);
                int colNum = 0;
                row.createCell(colNum++).setCellValue(data.getBatchCode() != null ? data.getBatchCode() : "");
                row.createCell(colNum++).setCellValue(data.getDataId() != null ? data.getDataId() : "");
                row.createCell(colNum++).setCellValue(data.getDataType() != null ? data.getDataType() : "");
                row.createCell(colNum++).setCellValue(data.getDataCode() != null ? data.getDataCode() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalName() != null ? data.getOriginalName() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalProvince() != null ? data.getOriginalProvince() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalAddress() != null ? data.getOriginalAddress() : "");
                row.createCell(colNum++).setCellValue(data.getCompanyName() != null ? data.getCompanyName() : "");
                row.createCell(colNum++).setCellValue(data.getAppealRemark() != null ? data.getAppealRemark() : "");
                row.createCell(colNum++).setCellValue(data.getSolveRemark() != null ? data.getSolveRemark() : "");
                row.createCell(colNum++).setCellValue(data.getInstitutionType() != null ? data.getInstitutionType() : "");
                row.createCell(colNum++).setCellValue(data.getKeyid() != null ? data.getKeyid() : "");
                row.createCell(colNum++).setCellValue(data.getName() != null ? data.getName() : "");
                row.createCell(colNum++).setCellValue(data.getNameHistory() != null ? data.getNameHistory() : "");
                row.createCell(colNum++).setCellValue(data.getProvince() != null ? data.getProvince() : "");
                row.createCell(colNum++).setCellValue(data.getProvinceid() != null ? data.getProvinceid() : "");
                row.createCell(colNum++).setCellValue(data.getCity() != null ? data.getCity() : "");
                row.createCell(colNum++).setCellValue(data.getCityid() != null ? data.getCityid() : "");
                row.createCell(colNum++).setCellValue(data.getArea() != null ? data.getArea() : "");
                row.createCell(colNum++).setCellValue(data.getAreaid() != null ? data.getAreaid() : "");
                row.createCell(colNum++).setCellValue(data.getAddress() != null ? data.getAddress() : "");
                row.createCell(colNum++).setCellValue(data.getLevel() != null ? data.getLevel() : "");
                row.createCell(colNum++).setCellValue(data.getGrade() != null ? data.getGrade() : "");
                row.createCell(colNum++).setCellValue(data.getPublicflag() != null ? data.getPublicflag() : "");
                row.createCell(colNum++).setCellValue(data.getClassify() != null ? data.getClassify() : "");
                row.createCell(colNum++).setCellValue(data.getGeneralBranchKid() != null ? data.getGeneralBranchKid() : "");
                row.createCell(colNum++).setCellValue(data.getGeneralBranchName() != null ? data.getGeneralBranchName() : "");
                row.createCell(colNum++).setCellValue(data.getMilitaryHos() != null ? data.getMilitaryHos() : "");
                row.createCell(colNum++).setCellValue(data.getRegcode() != null ? data.getRegcode() : "");
                row.createCell(colNum++).setCellValue(data.getValidity() != null ? data.getValidity() : "");
                row.createCell(colNum++).setCellValue(data.getSubjects() != null ? data.getSubjects() : "");
                row.createCell(colNum++).setCellValue(data.getLegalperson() != null ? data.getLegalperson() : "");
                row.createCell(colNum++).setCellValue(data.getUsci() != null ? data.getUsci() : "");
                row.createCell(colNum++).setCellValue(data.getOperation() != null ? data.getOperation() : "");
                row.createCell(colNum++).setCellValue(data.getScope() != null ? data.getScope() : "");
                row.createCell(colNum++).setCellValue(data.getMainBranchKid() != null ? data.getMainBranchKid() : "");
                row.createCell(colNum++).setCellValue(data.getMainBranchName() != null ? data.getMainBranchName() : "");
                row.createCell(colNum++).setCellValue(data.getCreateDate() != null ? data.getCreateDate() : "");
                row.createCell(colNum++).setCellValue(data.getRegistCapi() != null ? data.getRegistCapi() : "");
                row.createCell(colNum++).setCellValue(data.getEconKind() != null ? data.getEconKind() : "");
                row.createCell(colNum++).setCellValue(data.getSignStatus() != null ? data.getSignStatus() : "");
                row.createCell(colNum++).setCellValue(data.getIndustry() != null ? data.getIndustry() : "");
                row.createCell(colNum++).setCellValue(data.getBelong() != null ? data.getBelong() : "");

            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }


    public  byte[] exportConditionToExcel(List<HaoSenOrganization> allHospitalCondition,String sheetName){

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            // 创建工作表
            XSSFSheet sheet = workbook.createSheet(sheetName);

            // 创建表头
            String[] headers = {
                    "批次编号", "data_id", "数据类型", "原始数据编码", "原始数据名称",
                    "省份", "original_address", "经销商", "申诉备注", "申诉解决",
                    "机构类型", "keyid", "医院名称", "历史名称", "省",
                    "省ID", "市", "市ID", "区县", "区县ID",
                    "地址", "等级", "等次", "所有制", "类别",
                    "总分院kid", "总分院名称", "军队医院", "登记号", "有效期",
                    "诊疗科室", "法人代表", "统一社会信用代码", "经营方式", "经营范围",
                    "总分店kid", "总分店名称", "成立时间", "注册资金", "企业类型",
                    "登记状态", "所属行业", "登记机关","豪森编码","库中状态"
            };



            // 创建表头行
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }


            // 填充数据
            int rowNum = 1;
            for (HaoSenOrganization data : allHospitalCondition) {

                XSSFRow row = sheet.createRow(rowNum++);
                int colNum = 0;
                row.createCell(colNum++).setCellValue(data.getBatchCode() != null ? data.getBatchCode() : "");
                row.createCell(colNum++).setCellValue(data.getDataId() != null ? data.getDataId() : "");
                row.createCell(colNum++).setCellValue(data.getDataType() != null ? data.getDataType() : "");
                row.createCell(colNum++).setCellValue(data.getDataCode() != null ? data.getDataCode() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalName() != null ? data.getOriginalName() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalProvince() != null ? data.getOriginalProvince() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalAddress() != null ? data.getOriginalAddress() : "");
                row.createCell(colNum++).setCellValue(data.getCompanyName() != null ? data.getCompanyName() : "");
                row.createCell(colNum++).setCellValue(data.getAppealRemark() != null ? data.getAppealRemark() : "");
                row.createCell(colNum++).setCellValue(data.getSolveRemark() != null ? data.getSolveRemark() : "");
                row.createCell(colNum++).setCellValue(data.getOrgType() != null ? data.getOrgType() : "");
                row.createCell(colNum++).setCellValue(data.getKeyid() != null ? data.getKeyid() : "");
                row.createCell(colNum++).setCellValue(data.getName() != null ? data.getName() : "");
                row.createCell(colNum++).setCellValue(data.getNameHistory() != null ? data.getNameHistory() : "");
                row.createCell(colNum++).setCellValue(data.getProvince() != null ? data.getProvince() : "");
                row.createCell(colNum++).setCellValue(data.getProvinceId() != null ? data.getProvinceId() : "");
                row.createCell(colNum++).setCellValue(data.getCity() != null ? data.getCity() : "");
                row.createCell(colNum++).setCellValue(data.getCityId() != null ? data.getCityId() : "");
                row.createCell(colNum++).setCellValue(data.getArea() != null ? data.getArea() : "");
                row.createCell(colNum++).setCellValue(data.getAreaId() != null ? data.getAreaId() : "");
                row.createCell(colNum++).setCellValue(data.getAddress() != null ? data.getAddress() : "");
                row.createCell(colNum++).setCellValue(data.getLevel() != null ? data.getLevel() : "");
                row.createCell(colNum++).setCellValue(data.getGrade() != null ? data.getGrade() : "");
                row.createCell(colNum++).setCellValue(data.getPublicflag() != null ? data.getPublicflag() : "");
                row.createCell(colNum++).setCellValue(data.getClassify() != null ? data.getClassify() : "");
                row.createCell(colNum++).setCellValue(data.getGeneralBranchKid() != null ? data.getGeneralBranchKid() : "");
                row.createCell(colNum++).setCellValue(data.getGeneralBranchName() != null ? data.getGeneralBranchName() : "");
                row.createCell(colNum++).setCellValue(data.getMilitaryHos() != null ? data.getMilitaryHos() : "");
                row.createCell(colNum++).setCellValue(data.getRegcode() != null ? data.getRegcode() : "");
                row.createCell(colNum++).setCellValue(data.getValidity() != null ? data.getValidity() : "");
                row.createCell(colNum++).setCellValue(data.getSubjects() != null ? data.getSubjects() : "");
                row.createCell(colNum++).setCellValue(data.getLegalperson() != null ? data.getLegalperson() : "");
                row.createCell(colNum++).setCellValue(data.getUsci() != null ? data.getUsci() : "");
                row.createCell(colNum++).setCellValue(data.getOperation() != null ? data.getOperation() : "");
                row.createCell(colNum++).setCellValue(data.getScope() != null ? data.getScope() : "");
                row.createCell(colNum++).setCellValue(data.getMainBranchKid() != null ? data.getMainBranchKid() : "");
                row.createCell(colNum++).setCellValue(data.getMainBranchName() != null ? data.getMainBranchName() : "");
                row.createCell(colNum++).setCellValue(data.getCreateDate() != null ? data.getCreateDate() : "");
                row.createCell(colNum++).setCellValue(data.getRegistCapi() != null ? data.getRegistCapi() : "");
                row.createCell(colNum++).setCellValue(data.getEconKind() != null ? data.getEconKind() : "");
                row.createCell(colNum++).setCellValue(data.getSignStatus() != null ? data.getSignStatus() : "");
                row.createCell(colNum++).setCellValue(data.getIndustry() != null ? data.getIndustry() : "");
                row.createCell(colNum++).setCellValue(data.getBelong() != null ? data.getBelong() : "");
                row.createCell(colNum++).setCellValue(data.getHsCode() != null ? data.getHsCode() : "");
                row.createCell(colNum++).setCellValue(
                        java.util.Optional.ofNullable(data.getStatus())
                                .map(s ->
                                        "1".equals(s) ? "数据正常" :
                                                "2".equals(s) ? "数据作废" :
                                                        "3".equals(s) ? "无法清洗" :
                                                                "4".equals(s) ? "禁用客户" :
                                                                        "5".equals(s) ? "数据重复" : s)   // 不在枚举，用原值
                                .orElse("")                         // null 返回空串
                );
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }

    }




}
