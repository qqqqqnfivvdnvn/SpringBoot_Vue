package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.AppealUpdateDataVO;
import com.example.demo.vo.BarDataVO;
import com.example.demo.vo.DetailsDataVO;
import com.example.demo.vo.PieDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller

@RequestMapping("/homeData")


public class HomeDataController {
    @Autowired
    private HomeDataService homeDataService;


    @PostMapping("/getHomeData")
    public ResponseEntity<ApiResponseDTO<DetailsDataVO>> getDataCount() {
        return ResponseEntity.ok(homeDataService.getDataDetails());
    }

    @PostMapping("/getCleanBarData")
    public ResponseEntity<ApiResponseDTO<List<BarDataVO>>> getCleanBarData() {
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
    public ResponseEntity<ApiResponseDTO<List<AppealUpdateDataVO>>> getAppealUpdateData() {
        return ResponseEntity.ok(homeDataService.getAppealUpdateData());

    }

}
