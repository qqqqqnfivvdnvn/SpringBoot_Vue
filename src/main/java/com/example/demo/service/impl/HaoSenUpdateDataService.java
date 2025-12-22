package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.vo.HaoSenAppealDataVO;
import org.springframework.web.multipart.MultipartFile;

public interface HaoSenUpdateDataService {


    public ApiResponseDTO<HaoSenFileMessageDTO> uploadUpdateFile(MultipartFile file);

    public ApiResponseDTO<HaoSenAppealDataVO> findDaKuData(String keyid);




    public ApiResponseDTO<HaoSenFileMessageDTO> updateOneUpdateData(HaoSenOrganization haoSenOrganization);




}
