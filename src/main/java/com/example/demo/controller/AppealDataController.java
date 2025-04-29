package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.AppealConditionDTO;
import com.example.demo.service.impl.AppealDataService;
import com.example.demo.vo.AppealDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/appealData")

public class AppealDataController {

    @Autowired
    private AppealDataService appealDataService;

    @RequestMapping("/getAppealData")
    public ResponseEntity<ApiResponseDTO<Page<AppealDataVO>>> getAppealData(AppealConditionDTO condition, Pageable pageable) {
        ApiResponseDTO<Page<AppealDataVO>> appealData = appealDataService.getAppealData(condition, pageable);
        return ResponseEntity.ok(appealData);
    }

}
