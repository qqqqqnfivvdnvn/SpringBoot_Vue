package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenAppealConditionDTO;
import com.example.demo.mapper.HaoSenAppealDataMapper;
import com.example.demo.service.impl.HaoSenAppealDataService;
import com.example.demo.vo.HaoSenAppealDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HaoSenAppealDataServiceImpl implements HaoSenAppealDataService {
    @Autowired
    private HaoSenAppealDataMapper appealDataMapper;

    @Override
    public ApiResponseDTO<Page<HaoSenAppealDataVO>> getAppealData(HaoSenAppealConditionDTO condition, Pageable pageable) {
        List<HaoSenAppealDataVO> appealData = appealDataMapper.getAppealData(condition, pageable);
        long count = appealDataMapper.countAppealCondition(condition);
        Page<HaoSenAppealDataVO> page = new PageImpl<>(appealData, pageable, count);
        return ApiResponseDTO.success(page);

    }

    @Override
    public ApiResponseDTO<List<HaoSenAppealDataVO>> exportAppealData() {
        List<HaoSenAppealDataVO> appealData = appealDataMapper.exportAppealData();
        return ApiResponseDTO.success(appealData);

    }
}
