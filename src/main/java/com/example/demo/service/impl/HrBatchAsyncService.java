package com.example.demo.service.impl;


import com.example.demo.entity.HrMonitoringData;
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

/**
 * 批次异步处理服务
 */
@Service
public class HrBatchAsyncService {

    @Autowired
    private HrMonitoringDataMapper monitoringDataMapper;

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

            // 2. 解析 Excel
            ReaderExcel reader = new ReaderExcel();
            List<HrMonitoringDataVO> dataList = reader.readExcel(filePath.toString(), HrMonitoringDataVO.class);

            if (dataList == null || dataList.isEmpty()) {
                batchStatusService.updateBatchStatus(batchId, 2, "文件内容为空");
                throw new RuntimeException("文件内容为空");
            }

            // 3. 清空临时表
            monitoringDataMapper.deleteByBatchId(batchId);

            // 4. 转换数据并插入临时表
            List<HrMonitoringData> insertList = new ArrayList<>();
            for (HrMonitoringDataVO vo : dataList) {
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
                insertList.add(entity);
            }

            // 分批插入，每批 50 条（SQL Server 最多 2100 个参数，每条记录 28 个字段，50*28=1400）
            int batchSize = 50;
            for (int i = 0; i < insertList.size(); i += batchSize) {
                int end = Math.min(i + batchSize, insertList.size());
                List<HrMonitoringData> subList = insertList.subList(i, end);
                monitoringDataMapper.batchInsert(subList);
            }

            // 5. 执行数据转移（包含复杂的 SQL 逻辑）、同步比对关系、更新批次状态为已处理
            // 这三个操作在同一个 SQL 事务中原子性执行
            monitoringDataMapper.transferFromTemp(batchId);

        } catch (Exception e) {
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
