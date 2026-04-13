package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdFuzzyMatchBatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchFileMessageDTO;
import com.example.demo.entity.MdFuzzyMatchBatch;
import com.example.demo.entity.MdFuzzyMatchSummary;
import com.example.demo.service.MdFuzzyMatchService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 主数据模糊匹配 Controller
 */
@Controller
@RequestMapping("/maindata/fuzzyMatch")
public class MdFuzzyMatchController {

    @Autowired
    private MdFuzzyMatchService mdFuzzyMatchService;

    /**
     * 获取批次列表
     */
    @GetMapping("/getBatchList")
    @ResponseBody
    public ResponseEntity<ApiResponseDTO<PageInfo<MdFuzzyMatchBatch>>> getBatchList(
            MdFuzzyMatchBatchConditionDTO condition,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(mdFuzzyMatchService.getBatchList(condition, pageNum, pageSize));
    }

    /**
     * 上传文件进行模糊匹配
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public ResponseEntity<ApiResponseDTO<MdFuzzyMatchFileMessageDTO>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(mdFuzzyMatchService.uploadFile(file));
    }

    /**
     * 获取汇总数据列表
     */
    @GetMapping("/getSummaryList")
    @ResponseBody
    public ResponseEntity<ApiResponseDTO<PageInfo<MdFuzzyMatchSummary>>> getSummaryList(
            MdFuzzyMatchConditionDTO condition,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(mdFuzzyMatchService.getSummaryList(condition, pageNum, pageSize));
    }

    /**
     * 导出批次数据
     */
    @GetMapping("/exportBatch")
    @ResponseBody
    public ResponseEntity<ApiResponseDTO<byte[]>> exportBatch(
            @RequestParam String batchId) {
        return ResponseEntity.ok(mdFuzzyMatchService.exportBatch(batchId));
    }
}
