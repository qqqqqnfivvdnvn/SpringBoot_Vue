package com.example.demo.service;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.entity.HaoSenOrganization;
import com.github.pagehelper.PageInfo;
import com.example.demo.dto.HaoSenHospitalConditionDTO;

public interface HaoSenMainDataQueryService {

    public ApiResponseDTO<PageInfo<HaoSenOrganization>> getHospitalList(HaoSenHospitalConditionDTO condition,  int pageNum,
                                                                        int pageSize);

    public ApiResponseDTO<PageInfo<HaoSenOrganization>> getDrugStoreList(HaoSenDrugStoreConditionDTO condition, int pageNum,
                                                                         int pageSize);


    public ApiResponseDTO<PageInfo<HaoSenOrganization>> getCompanyList(HaoSenCompanyConditionDTO condition,int pageNum,
                                                                       int pageSize);


    public ApiResponseDTO<byte[]> exportHospitalList(HaoSenHospitalConditionDTO condition);


    public ApiResponseDTO<byte[]> exportDrugStoreList(HaoSenDrugStoreConditionDTO condition);



    public ApiResponseDTO<byte[]> exportCompanyData(HaoSenCompanyConditionDTO condition);



}
