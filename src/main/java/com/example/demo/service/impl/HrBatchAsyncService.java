package com.example.demo.service.impl;


import com.example.demo.entity.HrMonitoringData;
import com.example.demo.mapper.HrBatchMapper;
import com.example.demo.mapper.HrMonitoringDataMapper;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.ReaderExcel;
import com.example.demo.vo.HrMonitoringDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批次异步处理服务
 */
@Service
public class HrBatchAsyncService {

    @Autowired
    private HrMonitoringDataMapper monitoringDataMapper;

    @Autowired
    private HrBatchMapper batchMapper;

    @Autowired
    private HrBatchStatusService batchStatusService;

    @Async("batchExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processBatchData(String batchId, String filePath, String originalFilename) {
        try {
            // 1. 验证表头
            ExcelHeaderValidator.HeaderValidationResult headerResult =
                    ExcelHeaderValidator.validate(
                            filePath.toString(),
                            HrMonitoringDataVO.EXPECTED_HEADERS
                    );

            if (headerResult == null || !headerResult.isValid()) {
                String errorMsg = headerResult != null && headerResult.getMessage() != null
                    ? headerResult.getMessage() : "表头验证失败，请检查 Excel 表头格式";
                batchStatusService.updateBatchStatus(batchId, 2, errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // 2. 清空临时表
            monitoringDataMapper.deleteAllTemp();

            // 3. 流式读取 Excel，边读边转换边插入（避免全量数据驻留内存）
            ReaderExcel reader = new ReaderExcel();
            AtomicInteger totalCount = new AtomicInteger(0);

            reader.readExcelStreaming(filePath.toString(), HrMonitoringDataVO.class, batch -> {
                List<HrMonitoringData> entities = new ArrayList<>();
                for (HrMonitoringDataVO vo : batch) {
                    HrMonitoringData entity = new HrMonitoringData();
                    entity.setBatchId(batchId);
                    entity.setSerialNo(vo.getSerialNo());
                    entity.setMonitoringYear(vo.getMonitoringYear());
                    entity.setId(vo.getId());
                    entity.setProductId(vo.getProductId());
                    entity.setProductName(vo.getProductName());
                    entity.setPlatform(vo.getPlatform());
                    entity.setGenericName(vo.getGenericName());
                    entity.setUrl(vo.getUrl());
                    entity.setSpecification(vo.getSpecification());
                    entity.setOnlineStorePrice(vo.getOnlineStorePrice());
                    entity.setBoxQuantity(vo.getBoxQuantity());
                    entity.setUnitPricePerBox(vo.getUnitPricePerBox());
                    entity.setStoreName(vo.getStoreName());
                    entity.setBusinessLicenseName(vo.getBusinessLicenseName());
                    entity.setProvince(vo.getProvince());
                    entity.setCity(vo.getCity());
                    entity.setSalesVolume(vo.getSalesVolume());
                    entity.setCategory(vo.getCategory());
                    entity.setCreatedTime(vo.getCreatedTime());
                    entity.setCrawledTime(vo.getCrawledTime());
                    entity.setRepeatedCount(vo.getRepeatedCount());
                    entity.setShippingOrigin(vo.getShippingOrigin());
                    entity.setKeyId(vo.getKeyId());
                    entity.setName(vo.getName());
                    entity.setAddress(vo.getAddress());
                    entity.setStandardizedProvince(vo.getStandardizedProvince());
                    entity.setShortName(vo.getShortName());
                    entity.setRemarks(vo.getRemarks());
                    entities.add(entity);
                }
                // 直接批量插入临时表
                monitoringDataMapper.batchInsert(entities);
                totalCount.addAndGet(batch.size());
            }, ReaderExcel.BATCH_SIZE_28_FIELDS);

            // 检查是否有数据
            if (totalCount.get() == 0) {
                batchStatusService.updateBatchStatus(batchId, 2, "文件内容为空");
                throw new RuntimeException("文件内容为空");
            }

            // 4. 执行数据转移（包含复杂的 SQL 逻辑）
            monitoringDataMapper.transferFromTemp(batchId);

            // 5. 更新文件条数（在所有数据处理完成后执行）
            batchMapper.updateFileCount(batchId, totalCount.get());

        } catch (Exception e) {
            System.out.println("处理失败：" + e.getMessage());
            // 处理失败，使用独立事务更新状态为 2（避免被主事务回滚）
            String errorMsg = e.getMessage();
            // 截取简短的错误信息（最多 100 字符）
            if (errorMsg != null && errorMsg.length() > 100) {
                errorMsg = errorMsg.substring(0, 100) + "...";
            }
            batchStatusService.updateBatchStatus(batchId, 2, "处理失败：" + errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}
