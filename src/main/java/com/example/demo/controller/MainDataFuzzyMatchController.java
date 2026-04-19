package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdFuzzyMatchBatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchFileMessageDTO;
import com.example.demo.entity.MdFuzzyMatchBatch;
import com.example.demo.entity.MdFuzzyMatchSummary;
import com.example.demo.service.MdFuzzyMatchService;
import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 主数据项目 - 模糊匹配 Controller
 */
@Controller
@RequestMapping("/maindata/fuzzymatch")
public class MainDataFuzzyMatchController {

    @Autowired
    private MdFuzzyMatchService mdFuzzyMatchService;

    /**
     * 获取批次列表
     */
    @GetMapping("/getbatchlist")
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
    @PostMapping("/uploadfile")
    @ResponseBody
    public ResponseEntity<ApiResponseDTO<MdFuzzyMatchFileMessageDTO>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(mdFuzzyMatchService.uploadFile(file));
    }

    /**
     * 获取汇总数据列表
     */
    @GetMapping("/getsummarylist")
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
    @GetMapping("/exportbatch")
    public ResponseEntity<byte[]> exportBatch(@RequestParam String batchId) {
        ApiResponseDTO<byte[]> response = mdFuzzyMatchService.exportBatch(batchId);

        if (response.getCode() != 200) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("模糊匹配结果_" + batchId + "_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        return ResponseEntity.ok().headers(headers).body(response.getData());
    }

    /**
     * 导出汇总数据
     */
    @GetMapping("/exportsummary")
    public ResponseEntity<byte[]> exportSummary(MdFuzzyMatchConditionDTO condition) {
        ApiResponseDTO<byte[]> response = mdFuzzyMatchService.exportSummary(condition);

        if (response.getCode() != 200) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("模糊匹配汇总_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        return ResponseEntity.ok().headers(headers).body(response.getData());
    }
}
