package com.example.demo.service;

import com.example.demo.dto.ProjectPermissionDTO;
import com.example.demo.entity.UserProjectRelation;

import java.util.List;
import java.util.Map;

public interface UserProjectRelationService {

    boolean addPermission(String userId, String projectId, String permissionType, String creatorId);

    boolean removePermission(String userId, String projectId);

    boolean updatePermission(String userId, String projectId, String permissionType);

    List<UserProjectRelation> getPermissionsByUserId(String userId);

    List<Map<String, Object>> getPermissionsByProjectId(String projectId);

    String getUserPermission(String userId, String projectId);
}
