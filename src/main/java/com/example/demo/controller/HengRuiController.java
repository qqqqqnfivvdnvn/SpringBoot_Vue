package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HrBatchConditionDTO;
import com.example.demo.dto.HrMonitoringDataConditionDTO;
import com.example.demo.dto.HrOrgRelationConditionDTO;
import com.example.demo.service.HrBatchService;
import com.example.demo.service.HrMonitoringDataService;
import com.example.demo.service.HrOrgRelationService;
import com.example.demo.vo.HrBatchVO;
import com.example.demo.vo.HrMonitoringDataVO;
import com.example.demo.vo.HrOrgRelationVO;
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
 * 恒瑞项目 Controller
 */
@Controller
@RequestMapping("/hengrui")
public class HengRuiController {

    @Autowired
    private HrBatchService batchService;

    @Autowired
    private HrMonitoringDataService monitoringDataService;

    @Autowired
    private HrOrgRelationService orgRelationService;

    /**
     * 功能 1：批次管理 - 获取批次列表
     */
    @GetMapping("/batch/getBatchList")
    public ResponseEntity<ApiResponseDTO<PageInfo<HrBatchVO>>> getBatchList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            HrBatchConditionDTO condition) {
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
    public ResponseEntity<byte[]> exportBatch(@RequestParam String batchId) {
        HrBatchConditionDTO condition = new HrBatchConditionDTO();
        condition.setBatchId(batchId);
        ApiResponseDTO<byte[]> response = batchService.exportBatchExcel(condition);

        if (response.getCode() != 200) {
            return ResponseEntity.badRequest().build();
        }

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("恒瑞批次数据_" + batchId + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        return ResponseEntity.ok().headers(headers).body(response.getData());
    }

    /**
     * 功能 2：数据汇总 - 获取数据列表
     */
    @GetMapping("/monitoring/getDataList")
    public ResponseEntity<ApiResponseDTO<PageInfo<HrMonitoringDataVO>>> getDataList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            HrMonitoringDataConditionDTO condition) {
        return ResponseEntity.ok(monitoringDataService.getDataList(condition, pageNum, pageSize));
    }

    /**
     * 功能 2：数据汇总 - 导出数据
     */
    @GetMapping("/monitoring/exportData")
    public ResponseEntity<byte[]> exportData(HrMonitoringDataConditionDTO condition) {
        ApiResponseDTO<byte[]> response = monitoringDataService.exportDataExcel(condition);

        if (response.getCode() != 200) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("恒瑞数据汇总.xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        return ResponseEntity.ok().headers(headers).body(response.getData());
    }

    /**
     * 功能 3：数据比对关系 - 获取关系列表
     */
    @GetMapping("/relation/getRelationList")
    public ResponseEntity<ApiResponseDTO<PageInfo<HrOrgRelationVO>>> getRelationList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            HrOrgRelationConditionDTO condition) {
        return ResponseEntity.ok(orgRelationService.getRelationList(condition, pageNum, pageSize));
    }

    /**
     * 功能 3：数据比对关系 - 删除关系
     */
    @PostMapping("/relation/delete")
    public ResponseEntity<ApiResponseDTO<String>> deleteRelation(@RequestParam String businessLicenseName) {
        return ResponseEntity.ok(orgRelationService.deleteRelation(businessLicenseName));
    }

    /**
     * 功能 3：数据比对关系 - 更新关系
     */
    @PostMapping("/relation/update")
    public ResponseEntity<ApiResponseDTO<String>> updateRelation(@RequestBody HrOrgRelationVO relation) {
        return ResponseEntity.ok(orgRelationService.updateRelation(relation));
    }

    /**
     * 功能 3：数据比对关系 - 新增关系
     */
    @PostMapping("/relation/add")
    public ResponseEntity<ApiResponseDTO<String>> addRelation(@RequestBody HrOrgRelationVO relation) {
        return ResponseEntity.ok(orgRelationService.addRelation(relation));
    }

    /**
     * 功能 3：数据比对关系 - 导出关系数据
     */
    @GetMapping("/relation/exportRelation")
    public ResponseEntity<byte[]> exportRelation(HrOrgRelationConditionDTO condition) {
        ApiResponseDTO<byte[]> response = orgRelationService.exportRelationExcel(condition);

        if (response.getCode() != 200) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("恒瑞比对关系.xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        return ResponseEntity.ok().headers(headers).body(response.getData());
    }

    /**
     * 功能 3：数据比对关系 - 导入关系数据
     */
    @PostMapping("/relation/importRelation")
    public ResponseEntity<ApiResponseDTO<String>> importRelation(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(orgRelationService.importRelationExcel(file));
    }
}
