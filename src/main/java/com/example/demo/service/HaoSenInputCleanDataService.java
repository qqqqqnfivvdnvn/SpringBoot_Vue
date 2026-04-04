package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenCleanConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.vo.HaoSenCleanDataVO;
import com.example.demo.vo.HaoSenOrganizationVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;


public interface HaoSenInputCleanDataService {

    public ApiResponseDTO<HaoSenFileMessageDTO> uploadUpdateFile(MultipartFile file);

    public ApiResponseDTO <PageInfo<HaoSenCleanDataVO>> selectHaoSenCleanData(HaoSenCleanConditionDTO condition, int pageNum,
                                                                              int pageSize);


    public  ApiResponseDTO<HaoSenFileMessageDTO> cleanHaoSenData(HaoSenOrganizationVO haoSenOrganizationVO);

    public ApiResponseDTO<byte[]> exportCleanDataList(HaoSenCleanConditionDTO condition);




}
