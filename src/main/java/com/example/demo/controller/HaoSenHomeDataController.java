package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.service.HaoSenHomeDataService;
import com.example.demo.vo.HaoSenAppealUpdateDataVO;
import com.example.demo.vo.HaoSenBarDataVO;
import com.example.demo.vo.HaoSenDetailsDataVO;
import com.example.demo.vo.PieDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller

@RequestMapping("/homeData")


public class HaoSenHomeDataController {
    @Autowired
    private HaoSenHomeDataService homeDataService;


    @PostMapping("/getHomeData")
    public ResponseEntity<ApiResponseDTO<HaoSenDetailsDataVO>> getDataCount() {
        return ResponseEntity.ok(homeDataService.getDataDetails());
    }

    @PostMapping("/getCleanBarData")
    public ResponseEntity<ApiResponseDTO<List<HaoSenBarDataVO>>> getCleanBarData() {
        return ResponseEntity.ok(homeDataService.getCleanBarData());
    }

    @PostMapping("/getMainPieData")
    public ResponseEntity<ApiResponseDTO<List<PieDataVO>>> getMainPieData() {
        return ResponseEntity.ok(homeDataService.getMainPieData());

    }

    @PostMapping("/getBranchBarData")
    public ResponseEntity<ApiResponseDTO<List<PieDataVO>>> getBranchBarData() {
        return ResponseEntity.ok(homeDataService.getBranchBarData());

    }

    @PostMapping("/getAppealUpdateData")
    public ResponseEntity<ApiResponseDTO<List<HaoSenAppealUpdateDataVO>>> getAppealUpdateData() {
        return ResponseEntity.ok(homeDataService.getAppealUpdateData());

    }

}
