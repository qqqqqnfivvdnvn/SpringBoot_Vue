package com.example.demo.service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.HaoSenCompany;
import com.example.demo.entity.HaoSenDrugStore;
import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.mapper.HaoSenMainDataQueryMapper;
import com.example.demo.service.impl.HaoSenMainDataQueryService;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import com.example.demo.utils.MyBatisUtils;
import com.example.demo.utils.WebToExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

        PageHelper.startPage(pageNum, pageSize);

        List<HaoSenOrganization> list =
                mainDataQueryMapper.HospitalQueryByCondition(condition);

        PageInfo<HaoSenOrganization> pageInfo = new PageInfo<>(list);

        return ApiResponseDTO.success(pageInfo);
    }


    @Override
    public ApiResponseDTO<Page<HaoSenDrugStore>> getDrugStoreList(HaoSenDrugStoreConditionDTO condition, Pageable pageable) {
        List<HaoSenDrugStore> drugStores = mainDataQueryMapper.DrugStoreQueryByCondition(condition, pageable);
        long total = mainDataQueryMapper.countDrugStoreCondition(condition);
        Page<HaoSenDrugStore> page = new PageImpl<>(drugStores, pageable, total);
        return ApiResponseDTO.success(page);
    }

    @Override
    public ApiResponseDTO<Page<HaoSenCompany>> getCompanyList(HaoSenCompanyConditionDTO condition, Pageable pageable) {
        List<HaoSenCompany> companies = mainDataQueryMapper.CompanyQueryByCondition(condition,pageable);
        long total = mainDataQueryMapper.countCompanyCondition(condition);
        Page<HaoSenCompany> page = new PageImpl<>(companies, pageable, total);
        return ApiResponseDTO.success(page);
    }


    //    导出医疗机构excel业务逻辑代码
    @Override
    public ApiResponseDTO<byte[]> exportHospitalList(HaoSenHospitalConditionDTO condition) {

        boolean empty = MyBatisUtils.isAllBlank(condition); // 自己写个工具判空


        List<HaoSenOrganization> allHospitalCondition = mainDataQueryMapper.findAllHospitalCondition(condition,empty);
        // 调用字段映射
        WebToExcel webToExcel = new WebToExcel();
        byte[] excelBytes = webToExcel.exportHospitalConditionToExcel(allHospitalCondition);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);


        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("医疗机构数据.xlsx", StandardCharsets.UTF_8));


        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        return ApiResponseDTO.success(responseEntity.getBody());

    }


}
