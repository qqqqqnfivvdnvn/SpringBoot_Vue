package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenDuplicateConditionDtO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.service.HaoSenDuplicateDataService;
import com.example.demo.vo.HaoSenCheckDuplicateDataVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/haosen/duplicateData")
public class HaoSenDuplicateDataController {

    @Autowired
    private HaoSenDuplicateDataService haoSenDuplicateDataService;

//    查看重复数据
    @GetMapping("/getDuplicateData")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenCheckDuplicateDataVO>>> getDuplicateData(HaoSenDuplicateConditionDtO condition, @RequestParam(defaultValue = "1") int pageNum,
                                                                                                 @RequestParam(defaultValue = "20") int pageSize) {

        return ResponseEntity.ok(haoSenDuplicateDataService.getDuplicateData(condition, pageNum, pageSize));
    }


//    更新重复数据的状态
    @GetMapping("/updateDuplicateData")
    public ResponseEntity<ApiResponseDTO<Integer>> updateDuplicateData() {
        return ResponseEntity.ok(haoSenDuplicateDataService.updateDuplicateData());
    }





//    查看需要客户确认的重复数据
    @GetMapping("/getDuplicateDataByCondition")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenCheckDuplicateDataVO>>> getDuplicateDataByCondition(HaoSenDuplicateConditionDtO condition, @RequestParam(defaultValue = "1") int pageNum,
                                                                                                          @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(haoSenDuplicateDataService.getDuplicateDataByCondition(condition, pageNum, pageSize));
    }

    // 导出需要客户确认的重复数据
    @GetMapping("/exportDuplicateData")
    public ResponseEntity<ApiResponseDTO<byte[]>> exportDuplicateData(HaoSenDuplicateConditionDtO condition) {
        return ResponseEntity.ok(haoSenDuplicateDataService.exportDuplicateData(condition));
    }

    //上传客户确认后的文件
    @PostMapping ("/uploadDuplicateData")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> uploadDuplicateData(@RequestParam("file") MultipartFile file)  {
        return ResponseEntity.ok(haoSenDuplicateDataService.uploadDuplicateData(file));

    }





}
