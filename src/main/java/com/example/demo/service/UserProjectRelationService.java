package com.example.demo.service;

import com.example.demo.dto.ProjectPermissionDTO;
import com.example.demo.entity.UserProjectRelation;

import java.util.List;
import java.util.Map;

public interface UserProjectRelationService {

    boolean addPermission(String userId, String projectId, String creatorId);

    boolean removePermission(String userId, String projectId);

    boolean setPermission(String userId, String projectId, Integer hasPermission);

    List<UserProjectRelation> getPermissionsByUserId(String userId);

    List<Map<String, Object>> getPermissionsByProjectId(String projectId);

    boolean hasPermission(String userId, String projectId);
}
