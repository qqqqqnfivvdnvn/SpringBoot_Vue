package com.example.demo.vo;

import com.example.demo.annotation.ExcelColumn;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 主数据地理位置批次 VO
 */
@Data
public class MdLocationBatchVO {
    private String batchId;
    private String createTime;
    private String updateTime;
    private Integer status;
    private String statusDesc;
    private String originalFilename;
    private String message;
    private Integer dataCount;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (this.status == null) return "未知";
        switch (this.status) {
            case 0: return "处理中";
            case 1: return "已处理";
            case 2: return "处理失败";
            default: return "未知";
        }
    }
}
