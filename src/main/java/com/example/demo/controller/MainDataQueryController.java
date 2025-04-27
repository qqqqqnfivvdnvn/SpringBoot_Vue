package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Hospital;
import com.example.demo.service.impl.MainDataQueryService;
import com.example.demo.vo.HospitalCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

@RequestMapping("/mainData")

public class MainDataQueryController {

    @Autowired
    private MainDataQueryService mainDataQueryService;


    @GetMapping("/getHospitalData")
    public ResponseEntity<ApiResponse<Page<Hospital>>> getHospital(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "1") int size,
                                                                   @ModelAttribute HospitalCondition hospitalCondition) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<Page<Hospital>> hospitalList = mainDataQueryService.getHospitalList(hospitalCondition, pageable);
        return  ResponseEntity.ok(hospitalList);
    }


}
