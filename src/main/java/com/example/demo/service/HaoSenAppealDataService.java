package com.example.demo.service;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenAppealConditionDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.vo.HaoSenOrganizationVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HaoSenAppealDataService {

    public ApiResponseDTO<PageInfo<HaoSenOrganizationVO>> getAppealData(HaoSenAppealConditionDTO condition, int pageNum,
                                                                        int pageSize);


    public ApiResponseDTO<byte[]> exportAppealDataExcel(HaoSenAppealConditionDTO condition);


    public ApiResponseDTO<HaoSenFileMessageDTO> uploadAppealFile(MultipartFile file);


    public ApiResponseDTO<HaoSenFileMessageDTO> handleAppealData(HaoSenOrganizationVO haoSenOrganizationVO );




}
