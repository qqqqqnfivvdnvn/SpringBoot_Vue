package com.example.demo.mapper;

import com.example.demo.entity.UserProjectRelation;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserProjectRelationMapper {

    int insert(UserProjectRelation relation);

    int delete(@Param("userId") String userId, @Param("projectId") String projectId);

    int setPermission(@Param("userId") String userId, @Param("projectId") String projectId, @Param("hasPermission") Integer hasPermission);

    List<UserProjectRelation> findByUserId(@Param("userId") String userId);

    @MapKey("userId")
    List<Map<String, Object>> findByProjectId(@Param("projectId") String projectId);

    UserProjectRelation findByUserAndProject(@Param("userId") String userId, @Param("projectId") String projectId);
}
