package com.example.demo.service.impl;

import com.example.demo.dto.ProjectPermissionDTO;
import com.example.demo.entity.UserProjectRelation;
import com.example.demo.mapper.UserProjectRelationMapper;
import com.example.demo.service.UserProjectRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UserProjectRelationServiceImpl implements UserProjectRelationService {

    @Autowired
    private UserProjectRelationMapper userProjectRelationMapper;

    @Override
    @Transactional
    public boolean addPermission(String userId, String projectId, String creatorId) {
        UserProjectRelation existing = userProjectRelationMapper.findByUserAndProject(userId, projectId);
        if (existing != null) {
            return false;
        }

        UserProjectRelation relation = new UserProjectRelation();
        relation.setUserId(userId);
        relation.setProjectId(projectId);
        relation.setHasPermission(1);
        relation.setCreatorId(creatorId);

        return userProjectRelationMapper.insert(relation) > 0;
    }

    @Override
    @Transactional
    public boolean removePermission(String userId, String projectId) {
        return userProjectRelationMapper.delete(userId, projectId) > 0;
    }

    @Override
    @Transactional
    public boolean setPermission(String userId, String projectId, Integer hasPermission) {
        System.out.println("=== setPermission === userId: " + userId + ", projectId: " + projectId + ", hasPermission: " + hasPermission);
        int result = userProjectRelationMapper.setPermission(userId, projectId, hasPermission);
        System.out.println("=== setPermission result: " + result);
        return result > 0;
    }

    @Override
    public List<UserProjectRelation> getPermissionsByUserId(String userId) {
        return userProjectRelationMapper.findByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getPermissionsByProjectId(String projectId) {
        return userProjectRelationMapper.findByProjectId(projectId);
    }

    @Override
    public boolean hasPermission(String userId, String projectId) {
        UserProjectRelation relation = userProjectRelationMapper.findByUserAndProject(userId, projectId);
        return relation != null && relation.getHasPermission() != null && relation.getHasPermission() == 1;
    }
}
