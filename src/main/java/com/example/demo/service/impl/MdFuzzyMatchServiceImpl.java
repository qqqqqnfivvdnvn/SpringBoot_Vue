package com.example.demo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdFuzzyMatchBatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchFileMessageDTO;
import com.example.demo.entity.MdFuzzyMatchBatch;
import com.example.demo.entity.MdFuzzyMatchSummary;
import com.example.demo.mapper.MdFuzzyMatchMapper;
import com.example.demo.utils.MdFuzzyMatcher;
import com.example.demo.service.MdFuzzyMatchService;
import com.example.demo.vo.MdFuzzyMatchSummaryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 主数据模糊匹配 Service 实现
 */
@Service
public class MdFuzzyMatchServiceImpl implements MdFuzzyMatchService {

    @Autowired
    private MdFuzzyMatchMapper mdFuzzyMatchMapper;

    @Autowired
    private MdFuzzyMatcher mdFuzzyMatcher;

    @Value("${file.upload-dir:/tmp}/fuzzy_file")
    private String uploadDir;

    // 注入 Spring 管理的线程池
    @Autowired
    @Qualifier("batchExecutor")
    private Executor batchExecutorService;

    @Autowired
    @Qualifier("matchExecutor")
    private Executor matchExecutorService;

    @Override
    public ApiResponseDTO<PageInfo<MdFuzzyMatchBatch>> getBatchList(MdFuzzyMatchBatchConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<MdFuzzyMatchBatch> batchList = mdFuzzyMatchMapper.getBatchList(
                    condition.getBatchId(),
                    condition.getStatus(),
                    condition.getStartTime(),
                    condition.getEndTime());
            PageInfo<MdFuzzyMatchBatch> pageInfo = new PageInfo<>(batchList);

            // 为每个批次设置状态描述
            for (MdFuzzyMatchBatch batch : pageInfo.getList()) {
                batch.setStatusDesc(batch.getStatusDesc());
            }

            return ApiResponseDTO.success(pageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadFile(MultipartFile file) {
        MdFuzzyMatchFileMessageDTO fileMessage = new MdFuzzyMatchFileMessageDTO();

        try {
            // 1. 保存文件到服务器
            String originalFilename = file.getOriginalFilename();
            String batchId = UUID.randomUUID().toString();
            String filePath = saveFile(file, batchId);

            // 2. 创建批次记录
            MdFuzzyMatchBatch batch = new MdFuzzyMatchBatch();
            batch.setBatchId(batchId);
            batch.setStatus(0); // 处理中
            batch.setOriginalFilename(originalFilename);
            batch.setMessage("文件已上传，开始处理...");
            batch.setCreateTime(LocalDateTime.now());
            batch.setUpdateTime(LocalDateTime.now());

            mdFuzzyMatchMapper.insertBatch(batch);

            fileMessage.setBatchId(batchId);
            fileMessage.setMessage("文件上传成功，批次 ID: " + batchId);

            // 3. 异步处理文件
            processFileAsync(batchId, filePath);

            return ApiResponseDTO.success(fileMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 保存文件到服务器
     */
    private String saveFile(MultipartFile file, String batchId) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = batchId + ext;

        Path path = Paths.get(uploadDir, newFilename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }

    /**
     * 异步处理文件
     */
    private void processFileAsync(String batchId, String filePath) {
        CompletableFuture.runAsync(() -> {
            try {
                processFile(batchId, filePath);
            } catch (Exception e) {
                e.printStackTrace();
                mdFuzzyMatchMapper.updateBatchStatus(batchId, 2, "处理失败：" + e.getMessage());
            }
        }, batchExecutorService);
    }

    /**
     * 处理文件进行模糊匹配
     */
    private void processFile(String batchId, String filePath) throws Exception {
        List<FuzzyMatchData> dataList = new ArrayList<>();

        // 读取 Excel 文件
        EasyExcel.read(filePath, new FuzzyMatchReadListener(dataList))
                .autoCloseStream(true)
                .ignoreEmptyRow(true)
                .headRowNumber(1)
                .sheet()
                .doRead();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<String> errorMessages = new CopyOnWriteArrayList<>();

        // 使用 CountDownLatch 等待所有线程完成
        CountDownLatch latch = new CountDownLatch(dataList.size());

        // 使用 ThreadPoolTaskExecutor 提交任务
        if (matchExecutorService instanceof ThreadPoolTaskExecutor) {
            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) matchExecutorService;
            // 处理每条数据（多线程）
            for (FuzzyMatchData data : dataList) {
                taskExecutor.execute(() -> {
                    try {
                        MdFuzzyMatchSummary summary = fuzzyMatch(batchId, data);
                        if (summary != null) {
                            mdFuzzyMatchMapper.insertSummary(summary);
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        errorMessages.add("ID: " + data.getId() + " 处理失败：" + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
        } else {
            // 兼容处理：直接使用 Executor
            for (FuzzyMatchData data : dataList) {
                matchExecutorService.execute(() -> {
                    try {
                        MdFuzzyMatchSummary summary = fuzzyMatch(batchId, data);
                        if (summary != null) {
                            mdFuzzyMatchMapper.insertSummary(summary);
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        errorMessages.add("ID: " + data.getId() + " 处理失败：" + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        // 等待所有匹配完成
        latch.await();

        // 汇总结果
        String message = String.format("处理完成：成功 %d 条，失败 %d 条", successCount.get(), failCount.get());
        if (!errorMessages.isEmpty()) {
            String errorMsg = String.join("; ", errorMessages);
            if (errorMsg.length() <= 500) {
                message += "; 详情：" + errorMsg;
            }
        }

        mdFuzzyMatchMapper.updateBatchStatus(batchId, 1, message);
    }

    /**
     * 模糊匹配逻辑
     */
    private MdFuzzyMatchSummary fuzzyMatch(String batchId, FuzzyMatchData data) {
        String originalProvince = data.getProvince();
        String originalName = data.getName();

        MdFuzzyMatchSummary summary = new MdFuzzyMatchSummary();
        summary.setBatchId(batchId);
        summary.setOriginalId(data.getId());
        summary.setOriginalProvince(originalProvince);
        summary.setOriginalName(originalName);
        summary.setCreateTime(LocalDateTime.now());
        summary.setUpdateTime(LocalDateTime.now());

        // 使用 MdFuzzyMatcher 进行模糊匹配
        Map<String, Object> matchResult = mdFuzzyMatcher.match(originalProvince, originalName);

        if (matchResult != null) {
            summary.setKeyid((String) matchResult.get("keyid"));
            summary.setName((String) matchResult.get("name"));
            summary.setRemark((String) matchResult.get("remark"));
        } else {
            summary.setRemark("未找到匹配数据");
        }

        return summary;
    }

    @Override
    public ApiResponseDTO<PageInfo<MdFuzzyMatchSummary>> getSummaryList(MdFuzzyMatchConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<MdFuzzyMatchSummary> summaryList = mdFuzzyMatchMapper.getSummaryList(condition);
            PageInfo<MdFuzzyMatchSummary> pageInfo = new PageInfo<>(summaryList);
            return ApiResponseDTO.success(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("获取数据失败：" + e.getMessage());
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportBatch(String batchId) {
        try {
            MdFuzzyMatchConditionDTO condition = new MdFuzzyMatchConditionDTO();
            condition.setBatchId(batchId);
            List<MdFuzzyMatchSummary> summaryList = mdFuzzyMatchMapper.getSummaryList(condition);

            if (summaryList == null || summaryList.isEmpty()) {
                return ApiResponseDTO.error("没有数据可导出");
            }

            // 将 Entity 转换为 VO
            List<MdFuzzyMatchSummaryVO> voList = new ArrayList<>();
            for (MdFuzzyMatchSummary summary : summaryList) {
                MdFuzzyMatchSummaryVO vo = new MdFuzzyMatchSummaryVO();
                vo.setOriginalId(summary.getOriginalId());
                vo.setOriginalProvince(summary.getOriginalProvince());
                vo.setOriginalName(summary.getOriginalName());
                vo.setKeyid(summary.getKeyid());
                vo.setName(summary.getName());
                vo.setNamehistory(summary.getNamehistory());
                vo.setProvince(summary.getProvince());
                vo.setCityname(summary.getCityname());
                vo.setAreaname(summary.getAreaname());
                vo.setAddress(summary.getAddress());
                vo.setPrincipal(summary.getPrincipal());
                vo.setLegalperson(summary.getLegalperson());
                vo.setSign_status(summary.getSign_status());
                vo.setStatus(summary.getStatus());
                voList.add(vo);
            }

            // 使用 EasyExcel 导出
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                EasyExcel.write(baos, MdFuzzyMatchSummaryVO.class)
                        .sheet("模糊匹配结果")
                        .doWrite(voList);
                return ApiResponseDTO.success(baos.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportSummary(MdFuzzyMatchConditionDTO condition) {
        try {
            List<MdFuzzyMatchSummary> summaryList = mdFuzzyMatchMapper.getSummaryList(condition);

            if (summaryList == null || summaryList.isEmpty()) {
                return ApiResponseDTO.error("没有数据可导出");
            }

            // 将 Entity 转换为 VO
            List<MdFuzzyMatchSummaryVO> voList = new ArrayList<>();
            for (MdFuzzyMatchSummary summary : summaryList) {
                MdFuzzyMatchSummaryVO vo = new MdFuzzyMatchSummaryVO();
                vo.setOriginalId(summary.getOriginalId());
                vo.setOriginalProvince(summary.getOriginalProvince());
                vo.setOriginalName(summary.getOriginalName());
                vo.setKeyid(summary.getKeyid());
                vo.setName(summary.getName());
                vo.setNamehistory(summary.getNamehistory());
                vo.setProvince(summary.getProvince());
                vo.setCityname(summary.getCityname());
                vo.setAreaname(summary.getAreaname());
                vo.setAddress(summary.getAddress());
                vo.setPrincipal(summary.getPrincipal());
                vo.setLegalperson(summary.getLegalperson());
                vo.setSign_status(summary.getSign_status());
                vo.setStatus(summary.getStatus());
                voList.add(vo);
            }

            // 使用 EasyExcel 导出
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                EasyExcel.write(baos, MdFuzzyMatchSummaryVO.class)
                        .sheet("模糊匹配汇总")
                        .doWrite(voList);
                return ApiResponseDTO.success(baos.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }

    /**
     * 模糊匹配数据监听器
     */
    private static class FuzzyMatchReadListener implements ReadListener<Map<Integer, String>> {
        private final List<FuzzyMatchData> resultList;
        private Map<Integer, String> headerMap;

        public FuzzyMatchReadListener(List<FuzzyMatchData> resultList) {
            this.resultList = resultList;
        }

        @Override
        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
            this.headerMap = new HashMap<>();
            for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                String headerName = entry.getValue().getStringValue();
                if (headerName != null) {
                    headerMap.put(entry.getKey(), headerName.trim());
                }
            }
        }

        @Override
        public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
            if (rowData == null || headerMap == null) {
                return;
            }

            FuzzyMatchData data = new FuzzyMatchData();
            for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                String headerName = headerMap.get(entry.getKey());
                String value = entry.getValue() != null ? entry.getValue().trim() : "";

                if ("id".equalsIgnoreCase(headerName) || "ID".equals(headerName)) {
                    data.setId(value);
                } else if ("省份".equals(headerName) || "province".equalsIgnoreCase(headerName)) {
                    data.setProvince(value);
                } else if ("名称".equals(headerName) || "name".equalsIgnoreCase(headerName)) {
                    data.setName(value);
                }
            }

            // 校验：必须包含 id、省份、名称
            if (data.getId() != null && !data.getId().isEmpty()
                && data.getProvince() != null && !data.getProvince().isEmpty()
                && data.getName() != null && !data.getName().isEmpty()) {
                resultList.add(data);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 所有数据解析完成
        }
    }

    /**
     * 模糊匹配数据临时类
     */
    private static class FuzzyMatchData {
        private String id;
        private String province;
        private String name;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
