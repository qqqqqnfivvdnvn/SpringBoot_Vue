package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_project_relation")
public class UserProjectRelation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userId;
    private String projectId;
    private Integer hasPermission;
    private String createTime;
    private String creatorId;
}
