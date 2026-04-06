package com.example.demo.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 恒瑞数据比对关系 VO
 */
@Data
public class HrOrgRelationVO {
    private String province;
    private String businessLicenseName;
    private String keyId;
    private String name;
    private String address;
    private LocalDateTime addTime;
    private LocalDateTime updateTime;
}
