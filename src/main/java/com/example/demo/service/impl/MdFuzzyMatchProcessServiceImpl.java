package com.example.demo.service.impl;

import com.example.demo.dto.FuzzyMatchDataDTO;
import com.example.demo.entity.MdFuzzyMatchSummary;
import com.example.demo.mapper.MdFuzzyMatchMapper;
import com.example.demo.utils.MdFuzzyMatcher;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.ReaderExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;

/**
 * 主数据模糊匹配处理 Service - 专门处理多线程模糊匹配逻辑
 */
@Service
public class MdFuzzyMatchProcessServiceImpl {

    @Autowired
    private MdFuzzyMatchMapper mdFuzzyMatchMapper;

    @Autowired
    private MdFuzzyMatcher mdFuzzyMatcher;

    @Autowired
    @Qualifier("matchExecutor")
    private Executor matchExecutorService;

    /**
     * 异步处理文件
     */
    public void processFileAsync(String batchId, String filePath, String dataType, Executor batchExecutorService) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                processFile(batchId, filePath, dataType);
            } catch (Exception e) {
                e.printStackTrace();
                mdFuzzyMatchMapper.updateBatchStatus(batchId, 2, "处理失败：" + e.getMessage());
            }
        }, batchExecutorService);
    }

    /**
     * 处理文件进行模糊匹配
     */
    public void processFile(String batchId, String filePath, String dataType) throws Exception {
        // 1. 验证 Excel 表头
        List<String> expectedHeaders = Arrays.asList("id", "省份", "名称");
        ExcelHeaderValidator.HeaderValidationResult validationResult =
                ExcelHeaderValidator.validate(filePath, expectedHeaders);

        // 如果表头验证失败，更新 message 并返回
        if (!validationResult.isValid()) {
            mdFuzzyMatchMapper.updateBatchStatus(batchId, 2, "表头验证失败：" + validationResult.getMessage());
            return;
        }

        // 2. 流式读取 Excel，边读边提交到线程池处理（避免全量数据驻留内存）
        ReaderExcel readerExcel = new ReaderExcel();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<String> errorMessages = new CopyOnWriteArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        readerExcel.readExcelStreaming(filePath, FuzzyMatchDataDTO.class, batch -> {
            // 每批数据提交到线程池异步处理
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                processBatch(batch, batchId, dataType, successCount, failCount, errorMessages);
            }, matchExecutorService);
            futures.add(future);
        }, ReaderExcel.BATCH_SIZE_3_FIELDS);

        // 等待所有批次处理完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 汇总结果
        String message = String.format("处理完成：已匹配上 %d 条，未匹配上 %d 条", successCount.get(), failCount.get());
        if (!errorMessages.isEmpty()) {
            String errorMsg = String.join("; ", errorMessages);
            if (errorMsg.length() <= 500) {
                message += "; 详情：" + errorMsg;
            }
        }

        mdFuzzyMatchMapper.updateBatchStatus(batchId, 1, message);
    }

    /**
     * 处理一批数据
     */
    private void processBatch(List<FuzzyMatchDataDTO> batch, String batchId, String dataType,
                              AtomicInteger successCount, AtomicInteger failCount, List<String> errorMessages) {
        for (FuzzyMatchDataDTO data : batch) {
            try {
                MdFuzzyMatchSummary summary = fuzzyMatch(batchId, dataType, data);
                mdFuzzyMatchMapper.insertSummary(summary);
                if (summary.getKeyid() != null) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            } catch (Exception e) {
                failCount.incrementAndGet();
                errorMessages.add("ID: " + data.getId() + " 处理失败：" + e.getMessage());
            }
        }
    }

    /**
     * 模糊匹配逻辑
     */
    private MdFuzzyMatchSummary fuzzyMatch(String batchId, String dataType, FuzzyMatchDataDTO data) {
        String originalProvince = data.getProvince();
        String originalName = data.getName();

        MdFuzzyMatchSummary summary = new MdFuzzyMatchSummary();
        summary.setBatchId(batchId);
        summary.setDataType(dataType); // 设置数据类型
        summary.setOriginalId(data.getId());
        summary.setOriginalProvince(originalProvince);
        summary.setOriginalName(originalName);
        summary.setCreateTime(java.time.LocalDateTime.now());
        summary.setUpdateTime(java.time.LocalDateTime.now());

        // 根据 dataType 选择匹配方法
        Map<String, Object> matchResult;
        if ("hospital".equals(dataType)) {
            // 医院匹配 - 只匹配 hospital 表
            matchResult = mdFuzzyMatcher.matchHospital(originalProvince, originalName);
        } else if ("drugstore".equals(dataType)) {
            // 药店匹配 - 只匹配 company 表
            matchResult = mdFuzzyMatcher.matchDrugStore(originalProvince, originalName);
        } else {
            // 默认匹配（不区分类型，匹配所有表）
            matchResult = mdFuzzyMatcher.match(originalProvince, originalName);
        }

        if (matchResult != null) {
            summary.setKeyid((String) matchResult.get("keyid"));
            summary.setName((String) matchResult.get("name"));
            summary.setRemark((String) matchResult.get("remark"));
        } else {
            summary.setRemark("未找到匹配数据");
        }

        return summary;
    }
}
