package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;

import com.example.demo.dto.HaoSenDuplicateConditionDtO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.vo.HaoSenCheckDuplicateDataVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

public interface HaoSenDuplicateDataService {

    ApiResponseDTO<PageInfo<HaoSenCheckDuplicateDataVO>> getDuplicateData(HaoSenDuplicateConditionDtO condition, int pageNum, int pageSize);

    ApiResponseDTO<Integer> updateDuplicateData();

    ApiResponseDTO<PageInfo<HaoSenCheckDuplicateDataVO>> getDuplicateDataByCondition(HaoSenDuplicateConditionDtO condition, int pageNum, int pageSize);

    ApiResponseDTO<byte[]> exportDuplicateData(HaoSenDuplicateConditionDtO condition);

    ApiResponseDTO<HaoSenFileMessageDTO> uploadDuplicateData(MultipartFile file);

}
