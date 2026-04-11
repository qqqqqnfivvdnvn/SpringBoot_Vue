package com.example.demo.mapper;

import com.example.demo.entity.UserProjectRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProjectRelationMapper {

    int insert(UserProjectRelation relation);

    int delete(@Param("userId") String userId, @Param("projectId") String projectId);

    List<UserProjectRelation> findByUserId(@Param("userId") String userId);

    List<UserProjectRelation> findByProjectId(@Param("projectId") String projectId);

    UserProjectRelation findByUserAndProject(@Param("userId") String userId, @Param("projectId") String projectId);

    int updatePermission(@Param("userId") String userId, @Param("projectId") String projectId, @Param("permissionType") String permissionType);
}
