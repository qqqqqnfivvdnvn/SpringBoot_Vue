package com.example.demo.service.impl;

import com.example.demo.mapper.MdLocationBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 地理位置批次状态更新服务（独立事务）
 */
@Service
public class MdLocationBatchStatusService {

    @Autowired
    private MdLocationBatchMapper batchMapper;

    /**
     * 更新批次状态（使用独立事务，避免被主事务回滚）
     *
     * @param batchId 批次 ID
     * @param status  状态：0-处理中 1-已处理 2-处理失败
     * @param message 状态信息
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateBatchStatus(String batchId, Integer status, String message) {
        batchMapper.updateStatus(batchId, status, message);
    }
}
