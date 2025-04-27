package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Hospital;
import com.example.demo.mapper.MainDataQueryMapper;
import com.example.demo.service.impl.MainDataQueryService;
import com.example.demo.vo.HospitalCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainDataQueryServiceImpl implements MainDataQueryService {

    @Autowired
    private MainDataQueryMapper mainDataQueryMapper;

    @Override
    public ApiResponse<Page<Hospital>> getHospitalList(HospitalCondition condition, Pageable pageable) {
        List<Hospital> hospitals = mainDataQueryMapper.HospitalQueryByCondition(condition, pageable);
        long total = mainDataQueryMapper.countHospitalCondition(condition);
        Page<Hospital> page = new PageImpl<>(hospitals, pageable, total);
        return ApiResponse.success(page);
    }


}
