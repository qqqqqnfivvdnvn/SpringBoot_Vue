package com.example.demo.service.impl;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenAppealConditionDTO;
import com.example.demo.vo.HaoSenAppealDataVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HaoSenAppealDataService {

    public ApiResponseDTO<Page<HaoSenAppealDataVO>> getAppealData(HaoSenAppealConditionDTO condition, Pageable pageable);

    public ApiResponseDTO<List<HaoSenAppealDataVO>> exportAppealData();

}
