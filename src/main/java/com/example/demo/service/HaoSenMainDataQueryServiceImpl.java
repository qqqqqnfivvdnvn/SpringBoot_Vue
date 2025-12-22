package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.mapper.HaoSenMainDataQueryMapper;
import com.example.demo.service.impl.HaoSenMainDataQueryService;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import com.example.demo.utils.MyBatisUtils;
import com.example.demo.utils.WebToExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class HaoSenMainDataQueryServiceImpl implements HaoSenMainDataQueryService {

    @Autowired
    private HaoSenMainDataQueryMapper mainDataQueryMapper;


    @Override
    public ApiResponseDTO<PageInfo<HaoSenOrganization>> getHospitalList(
            HaoSenHospitalConditionDTO condition,
            int pageNum,
            int pageSize) {

        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HaoSenOrganization> list =
                    mainDataQueryMapper.HospitalQueryByCondition(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));

        } finally {

            PageHelper.clearPage();   // 必须清理 ThreadLocal

        }

    }



    @Override
    public ApiResponseDTO<PageInfo<HaoSenOrganization>> getDrugStoreList(HaoSenDrugStoreConditionDTO condition ,int pageNum,
                                                                         int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HaoSenOrganization> drugStoreList = mainDataQueryMapper.DrugStoreQueryByCondition(condition);
            PageInfo<HaoSenOrganization> haoSenOrganizationPageInfo = new PageInfo<>(drugStoreList);
            return ApiResponseDTO.success(haoSenOrganizationPageInfo);

        } finally {

            PageHelper.clearPage();   // 必须清理 ThreadLocal

        }

    }

    @Override
    public ApiResponseDTO<PageInfo<HaoSenOrganization>> getCompanyList(HaoSenCompanyConditionDTO condition, int pageNum,
                                                                       int pageSize) {

        try {

            PageHelper.startPage(pageNum, pageSize);
            List<HaoSenOrganization> CompanyList = mainDataQueryMapper.CompanyQueryByCondition(condition);
            PageInfo<HaoSenOrganization> haoSenOrganizationPageInfo = new PageInfo<>(CompanyList);

            return ApiResponseDTO.success(haoSenOrganizationPageInfo);

        } finally {
            PageHelper.clearPage();   // 必须清理 ThreadLocal
        }


    }


    //    导出医疗机构excel业务逻辑代码
    @Override
    public ApiResponseDTO<byte[]> exportHospitalList(HaoSenHospitalConditionDTO condition) {

        boolean needLimit = MyBatisUtils.isAllBlank(condition); // 自己写个工具判空

        List<HaoSenOrganization> allHospitalCondition = mainDataQueryMapper.findAllHospitalCondition(condition,needLimit);

        // 调用字段映射
        WebToExcel webToExcel = new WebToExcel();
        byte[] excelBytes = webToExcel.exportConditionToExcel(allHospitalCondition,"医疗机构数据");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);


        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("医疗机构数据.xlsx", StandardCharsets.UTF_8));


        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        return ApiResponseDTO.success(responseEntity.getBody());

    }


    // 导出药店excel业务逻辑代码
    @Override
    public ApiResponseDTO<byte[]> exportDrugStoreList(HaoSenDrugStoreConditionDTO condition) {
        boolean needLimit = MyBatisUtils.isAllBlank(condition); // 自己写个工具判空
        List<HaoSenOrganization> allDrugStoreCondition = mainDataQueryMapper.findAllDrugStoreCondition(condition,needLimit);

        // 调用字段映射
        WebToExcel webToExcel = new WebToExcel();
        byte[] excelBytes = webToExcel.exportConditionToExcel(allDrugStoreCondition,"药店数据");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);


        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("药店数据.xlsx", StandardCharsets.UTF_8));


        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        return ApiResponseDTO.success(responseEntity.getBody());




    }

    @Override
    public ApiResponseDTO<byte[]> exportCompanyData(HaoSenCompanyConditionDTO condition) {
        boolean needLimit = MyBatisUtils.isAllBlank(condition); // 自己写个工具判空
        List<HaoSenOrganization> allCompanyCondition = mainDataQueryMapper.findAllCompanyCondition(condition, needLimit);
        // 调用字段映射
        WebToExcel webToExcel = new WebToExcel();
        byte[] excelBytes = webToExcel.exportConditionToExcel(allCompanyCondition,"商业数据");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);


        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("商业数据.xlsx", StandardCharsets.UTF_8));


        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        return ApiResponseDTO.success(responseEntity.getBody());



    }


}
