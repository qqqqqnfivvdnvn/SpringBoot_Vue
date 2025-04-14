package com.example.demo.controller;

import com.example.demo.dto.Message;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.DataDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/homeData")


public class HomeDataController {
    @Autowired
    private HomeDataService homeDataService;


    @PostMapping("/getHomeData")
    public ResponseEntity<DataDetails> getDataCount() throws JsonProcessingException {
        return ResponseEntity.ok(homeDataService.getDataDetails());
    }


}
