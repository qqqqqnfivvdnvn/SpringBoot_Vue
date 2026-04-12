package com.example.demo.vo;

import lombok.Data;

@Data
public class ProjectWithPermissionVO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private String routeName;
    private String addtime;
    private String userId;
    private String userName;
    private boolean hasPermission; // 当前用户是否有该项目权限
}
