package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdLocationBatchConditionDTO;
import com.example.demo.dto.MdLocationConditionDTO;
import com.example.demo.service.MdLocationBatchService;
import com.example.demo.service.MdLocationService;
import com.example.demo.vo.MdLocationBatchVO;
import com.example.demo.vo.MdLocationExportVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 主数据项目 Controller
 */
@Controller
@RequestMapping("/maindata")
public class MainDataController {

    @Autowired
    private MdLocationBatchService batchService;

    @Autowired
    private MdLocationService locationService;

    /**
     * 功能 1：批次管理 - 获取批次列表
     */
    @GetMapping("/batch/getBatchList")
    public ResponseEntity<ApiResponseDTO<PageInfo<MdLocationBatchVO>>> getBatchList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            MdLocationBatchConditionDTO condition) {
        return ResponseEntity.ok(batchService.getBatchList(condition, pageNum, pageSize));
    }

    /**
     * 功能 1：批次管理 - 上传文件
     */
    @PostMapping("/batch/uploadFile")
    public ResponseEntity<ApiResponseDTO<?>> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(batchService.uploadBatchFile(file));
    }

    /**
     * 功能 1：批次管理 - 导出批次数据
     */
    @GetMapping("/batch/exportBatch")
    public ResponseEntity<byte[]> exportBatch(
            @RequestParam(required = false) String batchId) {
        // 批次导出实际是导出地理位置数据
        MdLocationConditionDTO condition = new MdLocationConditionDTO();
        condition.setBatchId(batchId);

        ApiResponseDTO<byte[]> response = locationService.exportDataExcel(condition);

        if (response.getCode() != 200) {
            return ResponseEntity.badRequest().build();
        }

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("主数据地理位置_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        return ResponseEntity.ok().headers(headers).body(response.getData());
    }

    /**
     * 功能 2：数据汇总 - 获取数据列表
     */
    @GetMapping("/location/getDataList")
    public ResponseEntity<ApiResponseDTO<PageInfo<MdLocationExportVO>>> getDataList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            MdLocationConditionDTO condition) {
        return ResponseEntity.ok(locationService.getDataList(condition, pageNum, pageSize));
    }

    /**
     * 功能 2：数据汇总 - 导出数据
     */
    @GetMapping("/location/exportData")
    public ResponseEntity<byte[]> exportData(MdLocationConditionDTO condition) {
        ApiResponseDTO<byte[]> response = locationService.exportDataExcel(condition);

        if (response.getCode() != 200) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("主数据地理位置汇总.xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        return ResponseEntity.ok().headers(headers).body(response.getData());
    }
}
