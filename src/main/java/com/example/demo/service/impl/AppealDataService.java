package com.example.demo.service.impl;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.AppealConditionDTO;
import com.example.demo.vo.AppealDataVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppealDataService {

    public ApiResponseDTO<Page<AppealDataVO>> getAppealData(AppealConditionDTO condition, Pageable pageable);
}
