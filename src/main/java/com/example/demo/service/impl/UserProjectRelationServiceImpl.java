package com.example.demo.service.impl;

import com.example.demo.dto.ProjectPermissionDTO;
import com.example.demo.entity.UserProjectRelation;
import com.example.demo.mapper.UserProjectRelationMapper;
import com.example.demo.service.UserProjectRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserProjectRelationServiceImpl implements UserProjectRelationService {

    @Autowired
    private UserProjectRelationMapper userProjectRelationMapper;

    @Override
    @Transactional
    public boolean addPermission(String userId, String projectId, String permissionType, String creatorId) {
        UserProjectRelation existing = userProjectRelationMapper.findByUserAndProject(userId, projectId);
        if (existing != null) {
            return false;
        }

        UserProjectRelation relation = new UserProjectRelation();
        relation.setUserId(userId);
        relation.setProjectId(projectId);
        relation.setPermissionType(permissionType);
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
    public boolean updatePermission(String userId, String projectId, String permissionType) {
        return userProjectRelationMapper.updatePermission(userId, projectId, permissionType) > 0;
    }

    @Override
    public List<UserProjectRelation> getPermissionsByUserId(String userId) {
        return userProjectRelationMapper.findByUserId(userId);
    }

    @Override
    public List<UserProjectRelation> getPermissionsByProjectId(String projectId) {
        return userProjectRelationMapper.findByProjectId(projectId);
    }

    @Override
    public String getUserPermission(String userId, String projectId) {
        UserProjectRelation relation = userProjectRelationMapper.findByUserAndProject(userId, projectId);
        return relation != null ? relation.getPermissionType() : null;
    }
}
