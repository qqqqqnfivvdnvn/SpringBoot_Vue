package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller

@RequestMapping("/homeData")


public class HomeDataController {
    @Autowired
    private HomeDataService homeDataService;


    @PostMapping("/getHomeData")
    public ResponseEntity<ApiResponse<DetailsData>> getDataCount() {
        return ResponseEntity.ok(homeDataService.getDataDetails());
    }

    @PostMapping("/getBarData")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBarData() {
        return ResponseEntity.ok(homeDataService.getBarData());
    }

}
