package com.example.demo.service.impl;

import com.example.demo.mapper.HrBatchMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 恒瑞批次状态更新服务（独立事务）
 */
@Service
public class HrBatchStatusService {

    @Autowired
    private HrBatchMapper batchMapper;

    /**
     * 更新批次状态（使用 REQUIRES_NEW 确保不被其他事务回滚影响）
     * @param batchId 批次 ID
     * @param status 状态：0-处理中，1-已处理，2-处理失败
     * @param message 消息
     */
    @DS("master_sqlserver")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateBatchStatus(String batchId, Integer status, String message) {
        batchMapper.updateStatusWithMessage(batchId, status, message);
    }
}
