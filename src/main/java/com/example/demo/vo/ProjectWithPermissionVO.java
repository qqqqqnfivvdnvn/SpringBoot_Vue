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
    private String permissionType; // 当前用户对该项目的权限
}
