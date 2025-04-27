package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.AppealUpdateData;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import com.example.demo.vo.PieData;
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
    public ResponseEntity<ApiResponse<DetailsData>> getDataCount() {
        return ResponseEntity.ok(homeDataService.getDataDetails());
    }

    @PostMapping("/getCleanBarData")
    public ResponseEntity<ApiResponse<List<BarData>>> getCleanBarData() {
        return ResponseEntity.ok(homeDataService.getCleanBarData());
    }

    @PostMapping("/getMainPieData")
    public ResponseEntity<ApiResponse<List<PieData>>> getMainPieData() {
        return ResponseEntity.ok(homeDataService.getMainPieData());

    }

    @PostMapping("/getBranchBarData")
    public ResponseEntity<ApiResponse<List<PieData>>> getBranchBarData() {
        return ResponseEntity.ok(homeDataService.getBranchBarData());

    }

    @PostMapping("/getAppealUpdateData")
    public ResponseEntity<ApiResponse<List<AppealUpdateData>>> getAppealUpdateData() {
        return ResponseEntity.ok(homeDataService.getAppealUpdateData());

    }

}
