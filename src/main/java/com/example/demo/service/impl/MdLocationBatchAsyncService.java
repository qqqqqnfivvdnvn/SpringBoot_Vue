package com.example.demo.service.impl;

import com.example.demo.entity.MdLocation;
import com.example.demo.mapper.MdLocationBatchMapper;
import com.example.demo.mapper.MdLocationMapper;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.GaoDeMapUtil;
import com.example.demo.utils.ReaderExcel;
import com.example.demo.vo.MdLocationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 地理位置批次异步处理服务
 */
@Service
public class MdLocationBatchAsyncService {

    @Autowired
    private MdLocationMapper locationMapper;

    @Autowired
    private MdLocationBatchMapper batchMapper;

    @Autowired
    private MdLocationBatchStatusService batchStatusService;

    /**
     * 异步处理批次数据
     */
    @Async("batchExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processBatchData(String batchId, String filePath, String originalFilename) {
        try {
            // 1. 验证表头
            ExcelHeaderValidator.HeaderValidationResult headerResult =
                    ExcelHeaderValidator.validate(
                            filePath.toString(),
                            MdLocationVO.EXPECTED_HEADERS
                    );

            if (headerResult == null || !headerResult.isValid()) {
                String errorMsg = headerResult != null && headerResult.getMessage() != null
                    ? headerResult.getMessage() : "表头验证失败，请检查 Excel 表头格式";
                batchStatusService.updateBatchStatus(batchId, 2, errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // 2. 解析 Excel
            ReaderExcel reader = new ReaderExcel();
            List<MdLocationVO> dataList = reader.readExcel(filePath.toString(), MdLocationVO.class);

            if (dataList == null || dataList.isEmpty()) {
                batchStatusService.updateBatchStatus(batchId, 2, "文件内容为空");
                throw new RuntimeException("文件内容为空");
            }

            // 3. 清空临时数据（删除该批次的旧数据）
            locationMapper.deleteByBatchId(batchId);

            // 4. 转换数据并调用高德地图 API 获取地理位置信息
            List<MdLocation> insertList = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (MdLocationVO vo : dataList) {
                MdLocation entity = new MdLocation();
                entity.setBatchId(batchId);
                // 设置 Excel 中的 id
                if (vo.getOriginalId() != null && !vo.getOriginalId().trim().isEmpty()) {
                    entity.setId(vo.getOriginalId().trim());
                }
                entity.setOriginalName(vo.getOriginalName());
                entity.setOriginalProvince(vo.getOriginalProvince());
                entity.setOriginalAddress(vo.getOriginalAddress());

                // 调用高德地图 API 获取地理位置信息
                try {
                    Map<String, Object> geoResult = GaoDeMapUtil.getAreaLatLng(
                            vo.getOriginalProvince(),
                            vo.getOriginalAddress()
                    );

                    if ("10000".equals(geoResult.get("infocode"))) {
                        // 成功
                        entity.setAreaName((String) geoResult.get("area"));
                        entity.setAreaId((String) geoResult.get("areaid"));
                        entity.setLngLat((String) geoResult.get("location"));
                        entity.setProvince((String) geoResult.get("province"));
                        entity.setCity((String) geoResult.get("city"));
                        successCount++;
                    } else {
                        // API 调用失败，保留原始数据
                        entity.setAreaName(null);
                        entity.setAreaId(null);
                        entity.setLngLat(null);
                        entity.setProvince(vo.getOriginalProvince());
                        entity.setCity(null);
                        failCount++;
                    }
                } catch (Exception e) {
                    // 异常时保留原始数据
                    entity.setProvince(vo.getOriginalProvince());
                    failCount++;
                }

                insertList.add(entity);

                // 每 50 条批量插入一次
                if (insertList.size() >= 50) {
                    locationMapper.batchInsert(insertList);
                    insertList.clear();
                }
            }

            // 插入剩余数据
            if (!insertList.isEmpty()) {
                locationMapper.batchInsert(insertList);
            }

            // 5. 更新批次状态为已处理
            String message = "处理完成，成功：" + successCount + "条，失败：" + failCount + "条";
            batchStatusService.updateBatchStatus(batchId, 1, message);

        } catch (Exception e) {
            // 处理失败
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 200) {
                errorMsg = errorMsg.substring(0, 200) + "...";
            }
            batchStatusService.updateBatchStatus(batchId, 2, "处理失败：" + errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}
