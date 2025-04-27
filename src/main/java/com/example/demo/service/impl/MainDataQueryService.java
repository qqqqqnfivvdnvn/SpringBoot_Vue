package com.example.demo.service.impl;
import com.example.demo.dto.ApiResponse;
import org.springframework.data.domain.Page;
import com.example.demo.entity.Hospital;
import com.example.demo.vo.HospitalCondition;
import org.springframework.data.domain.Pageable;

public interface MainDataQueryService {

    public ApiResponse<Page<Hospital>> getHospitalList(HospitalCondition condition, Pageable pageable);

}
