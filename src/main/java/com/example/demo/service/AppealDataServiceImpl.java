package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.AppealConditionDTO;
import com.example.demo.mapper.AppealDataMapper;
import com.example.demo.service.impl.AppealDataService;
import com.example.demo.vo.AppealDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AppealDataServiceImpl implements AppealDataService {
    @Autowired
    private AppealDataMapper appealDataMapper;

    @Override
    public ApiResponseDTO<Page<AppealDataVO>> getAppealData(AppealConditionDTO condition, Pageable pageable) {
        List<AppealDataVO> appealData = appealDataMapper.getAppealData(condition, pageable);
        long count = appealDataMapper.countAppealCondition(condition);
        Page<AppealDataVO> page = new PageImpl<>(appealData, pageable, count);
        return ApiResponseDTO.success(page);

    }
}
