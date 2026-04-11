# 权限管理功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现基于角色的项目访问权限控制，让不同用户可以看到不同的项目

**Architecture:**
- 在 Users 表添加 role 字段区分管理员/普通用户
- 新建 user_project_relation 表实现用户与项目的多对多关联
- 后端拦截器获取当前用户，根据角色过滤项目列表
- 管理员可访问所有项目并分配权限，普通用户只能访问授权项目

**Tech Stack:** Spring Boot 3.4.1, Java 21, MyBatis Plus, SQL Server, Vue 3, Element Plus

**权限类型（简化版）:**
- `write` - 读写权限
- `admin` - 管理权限（可分配权限）

---

## 数据库变更

### 1. Users 表添加角色字段

**文件：** `src/main/resources/db/add_user_role.sql`

```sql
-- 给用户表添加角色字段
IF NOT EXISTS (
    SELECT * FROM sys.columns
    WHERE object_id = OBJECT_ID(N'[dbo].[Users]')
    AND name = 'role'
)
BEGIN
    ALTER TABLE [dbo].[Users] ADD [role] NVARCHAR(20) NOT NULL DEFAULT 'user';
    PRINT 'Users 表添加 role 字段成功';
END
ELSE
BEGIN
    PRINT 'role 字段已存在';
END
GO

-- 添加索引提高查询效率
IF NOT EXISTS (
    SELECT * FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'[dbo].[Users]')
    AND name = 'IX_Users_role'
)
BEGIN
    CREATE INDEX [IX_Users_role] ON [dbo].[Users] ([role]);
    PRINT '创建 Users.role 索引成功';
END
GO
```

### 2. 创建用户 - 项目关联表

**文件：** `src/main/resources/db/create_user_project_relation.sql`

```sql
-- 用户 - 项目关联表 (支持多对多关系)
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_project_relation]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[user_project_relation] (
        [id] INT IDENTITY(1,1) PRIMARY KEY,
        [user_id] NVARCHAR(64) NOT NULL,
        [project_id] NVARCHAR(64) NOT NULL,
        [permission_type] NVARCHAR(20) NOT NULL DEFAULT 'write', -- write:读写，admin:管理
        [create_time] DATETIME NOT NULL DEFAULT GETDATE(),
        [creator_id] NVARCHAR(64),
        CONSTRAINT [UK_user_project] UNIQUE ([user_id], [project_id])
    );

    -- 创建索引
    CREATE INDEX [IX_user_project_relation_user_id] ON [dbo].[user_project_relation] ([user_id]);
    CREATE INDEX [IX_user_project_relation_project_id] ON [dbo].[user_project_relation] ([project_id]);

    PRINT '创建表 user_project_relation 成功';
END
ELSE
BEGIN
    PRINT '表 user_project_relation 已存在';
END
GO
```

---

## 后端实现

### Task 1: 创建权限相关的 Entity 和 DTO

**Files:**
- Create: `src/main/java/com/example/demo/entity/UserProjectRelation.java`
- Create: `src/main/java/com/example/demo/dto/ProjectPermissionDTO.java`
- Create: `src/main/java/com/example/demo/vo/ProjectWithPermissionVO.java`

- [ ] **Step 1: 创建 UserProjectRelation 实体类**

```java
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
    private String permissionType; // write, admin
    private String createTime;
    private String creatorId;
}
```

- [ ] **Step 2: 创建 ProjectPermissionDTO**

```java
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPermissionDTO {
    private String projectId;
    private String userId;
    private String permissionType;
}
```

- [ ] **Step 3: 创建 ProjectWithPermissionVO**

```java
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
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/demo/entity/UserProjectRelation.java
git add src/main/java/com/example/demo/dto/ProjectPermissionDTO.java
git add src/main/java/com/example/demo/vo/ProjectWithPermissionVO.java
git commit -m "feat: 添加权限管理相关实体类和 DTO"
```

---

### Task 2: 创建 Mapper 接口和 XML

**Files:**
- Create: `src/main/java/com/example/demo/mapper/UserProjectRelationMapper.java`
- Create: `src/main/resources/mapper/UserProjectRelationMapper.xml`
- Modify: `src/main/java/com/example/demo/mapper/UserMapper.java`
- Modify: `src/main/resources/mapper/UserMapper.xml`

- [ ] **Step 1: 创建 UserProjectRelationMapper 接口**

```java
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
```

- [ ] **Step 2: 创建 UserProjectRelationMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.demo.mapper.UserProjectRelationMapper">

    <insert id="insert" parameterType="com.example.demo.entity.UserProjectRelation">
        INSERT INTO [user_project_relation] ([user_id], [project_id], [permission_type], [create_time], [creator_id])
        VALUES (#{userId}, #{projectId}, #{permissionType}, GETDATE(), #{creatorId})
    </insert>

    <delete id="delete">
        DELETE FROM [user_project_relation] WHERE [user_id] = #{userId} AND [project_id] = #{projectId}
    </delete>

    <select id="findByUserId" resultType="com.example.demo.entity.UserProjectRelation">
        SELECT * FROM [user_project_relation] WHERE [user_id] = #{userId} ORDER BY [create_time] DESC
    </select>

    <select id="findByProjectId" resultType="com.example.demo.entity.UserProjectRelation">
        SELECT * FROM [user_project_relation] WHERE [project_id] = #{projectId} ORDER BY [create_time] DESC
    </select>

    <select id="findByUserAndProject" resultType="com.example.demo.entity.UserProjectRelation">
        SELECT * FROM [user_project_relation] WHERE [user_id] = #{userId} AND [project_id] = #{projectId}
    </select>

    <update id="updatePermission">
        UPDATE [user_project_relation] SET [permission_type] = #{permissionType}
        WHERE [user_id] = #{userId} AND [project_id] = #{projectId}
    </update>

</mapper>
```

- [ ] **Step 3: 修改 Users 实体添加 role 字段**

修改 `src/main/java/com/example/demo/entity/Users.java`:

```java
// 在现有字段后添加
private String role;
```

- [ ] **Step 4: 修改 UserMapper.xml 添加 role 查询**

```xml
<select id="findByPage" resultType="com.example.demo.entity.Users">
    SELECT id, username, password, email, addtime, role
    FROM haosen_project..users
    WHERE username LIKE CONCAT('%', #{name}, '%')
    ORDER BY id
    OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY
</select>
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/demo/mapper/UserProjectRelationMapper.java
git add src/main/resources/mapper/UserProjectRelationMapper.xml
git add src/main/java/com/example/demo/entity/Users.java
git add src/main/resources/mapper/UserMapper.xml
git commit -m "feat: 添加 UserProjectRelationMapper 和 role 字段支持"
```

---

### Task 3: 创建 Service 层

**Files:**
- Create: `src/main/java/com/example/demo/service/UserProjectRelationService.java`
- Create: `src/main/java/com/example/demo/service/impl/UserProjectRelationServiceImpl.java`

- [ ] **Step 1: 创建 Service 接口**

```java
package com.example.demo.service;

import com.example.demo.dto.ProjectPermissionDTO;
import com.example.demo.entity.UserProjectRelation;

import java.util.List;

public interface UserProjectRelationService {

    boolean addPermission(String userId, String projectId, String permissionType, String creatorId);

    boolean removePermission(String userId, String projectId);

    boolean updatePermission(String userId, String projectId, String permissionType);

    List<UserProjectRelation> getPermissionsByUserId(String userId);

    List<UserProjectRelation> getPermissionsByProjectId(String projectId);

    String getUserPermission(String userId, String projectId);
}
```

- [ ] **Step 2: 创建 Service 实现类**

```java
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
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/example/demo/service/UserProjectRelationService.java
git add src/main/java/com/example/demo/service/impl/UserProjectRelationServiceImpl.java
git commit -m "feat: 添加 UserProjectRelationService"
```

---

### Task 4: 修改 ProjectsService 实现权限过滤

**Files:**
- Modify: `src/main/java/com/example/demo/service/ProjectsService.java`
- Modify: `src/main/java/com/example/demo/service/impl/ProjectsServiceImpl.java`

- [ ] **Step 1: 修改 ProjectsService 接口**

```java
// 添加新方法
public ApiResponseDTO<List<ProjectWithPermissionVO>> getProjectsForUser(String userId);

public ApiResponseDTO<List<Projects>> getAllProjects(); // 保留给管理员
```

- [ ] **Step 2: 修改 ProjectsServiceImpl 实现**

```java
@Override
public ApiResponseDTO<List<ProjectWithPermissionVO>> getProjectsForUser(String userId) {
    try {
        Users user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResponseDTO.error("用户不存在", null);
        }

        List<ProjectWithPermissionVO> result;

        if ("admin".equals(user.getRole())) {
            // 管理员可以看到所有项目
            List<Projects> allProjects = projectsMapper.selectAll();
            result = allProjects.stream().map(p -> {
                ProjectWithPermissionVO vo = convertToVO(p);
                vo.setPermissionType("admin");
                return vo;
            }).collect(Collectors.toList());
        } else {
            // 普通用户只能看到有权限的项目
            List<UserProjectRelation> relations = userProjectRelationMapper.findByUserId(userId);
            Set<String> projectIds = relations.stream()
                .map(UserProjectRelation::getProjectId)
                .collect(Collectors.toSet());

            // 加上自己创建的项目
            List<Projects> ownedProjects = projectsMapper.findByUserId(userId);
            for (Projects p : ownedProjects) {
                projectIds.add(p.getId());
            }

            result = new ArrayList<>();
            for (String projectId : projectIds) {
                Projects project = projectsMapper.selectById(projectId);
                if (project != null) {
                    ProjectWithPermissionVO vo = convertToVO(project);
                    String permType = userProjectRelationMapper.getUserPermission(userId, projectId);
                    vo.setPermissionType(permType != null ? permType : "owner");
                    result.add(vo);
                }
            }
        }

        return ApiResponseDTO.success(result);
    } catch (Exception e) {
        e.printStackTrace();
        return ApiResponseDTO.error("获取项目失败：" + e.getMessage(), null);
    }
}

private ProjectWithPermissionVO convertToVO(Projects project) {
    ProjectWithPermissionVO vo = new ProjectWithPermissionVO();
    vo.setId(project.getId());
    vo.setName(project.getName());
    vo.setDescription(project.getDescription());
    vo.setIcon(project.getIcon());
    vo.setColor(project.getColor());
    vo.setRouteName(project.getRouteName());
    vo.setAddtime(project.getAddtime());
    vo.setUserId(project.getUserId());
    vo.setUserName(project.getUserName());
    return vo;
}
```

- [ ] **Step 3: 添加依赖注入**

```java
@Autowired
private UserProjectRelationMapper userProjectRelationMapper;

@Autowired
private UserMapper userMapper;
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/demo/service/ProjectsService.java
git add src/main/java/com/example/demo/service/impl/ProjectsServiceImpl.java
git commit -m "feat: 实现基于用户角色的项目列表过滤"
```

---

### Task 5: 创建权限管理 Controller

**Files:**
- Create: `src/main/java/com/example/demo/controller/PermissionController.java`

- [ ] **Step 1: 创建 PermissionController**

```java
package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.ProjectPermissionDTO;
import com.example.demo.entity.UserProjectRelation;
import com.example.demo.service.UserProjectRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private UserProjectRelationService userProjectRelationService;

    /**
     * 给项目添加用户权限
     */
    @PostMapping("/grant")
    public ResponseEntity<ApiResponseDTO<Boolean>> grantPermission(@RequestBody ProjectPermissionDTO dto) {
        boolean success = userProjectRelationService.addPermission(
            dto.getUserId(),
            dto.getProjectId(),
            dto.getPermissionType(),
            "system" // TODO: 从登录信息获取
        );

        if (success) {
            return ResponseEntity.ok(ApiResponseDTO.success(true, "授权成功"));
        } else {
            return ResponseEntity.ok(ApiResponseDTO.error("该用户已有此项目权限", false));
        }
    }

    /**
     * 移除项目权限
     */
    @PostMapping("/revoke")
    public ResponseEntity<ApiResponseDTO<Boolean>> revokePermission(@RequestBody ProjectPermissionDTO dto) {
        boolean success = userProjectRelationService.removePermission(dto.getUserId(), dto.getProjectId());

        if (success) {
            return ResponseEntity.ok(ApiResponseDTO.success(true, "权限已移除"));
        } else {
            return ResponseEntity.ok(ApiResponseDTO.error("权限记录不存在", false));
        }
    }

    /**
     * 更新项目权限
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponseDTO<Boolean>> updatePermission(@RequestBody ProjectPermissionDTO dto) {
        boolean success = userProjectRelationService.updatePermission(
            dto.getUserId(),
            dto.getProjectId(),
            dto.getPermissionType()
        );

        if (success) {
            return ResponseEntity.ok(ApiResponseDTO.success(true, "权限已更新"));
        } else {
            return ResponseEntity.ok(ApiResponseDTO.error("权限记录不存在", false));
        }
    }

    /**
     * 获取项目的所有用户权限
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponseDTO<List<UserProjectRelation>>> getProjectPermissions(
        @PathVariable String projectId) {
        List<UserProjectRelation> permissions = userProjectRelationService.getPermissionsByProjectId(projectId);
        return ResponseEntity.ok(ApiResponseDTO.success(permissions));
    }

    /**
     * 检查用户对项目的权限
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> checkPermission(
        @RequestParam String userId,
        @RequestParam String projectId) {

        String permission = userProjectRelationService.getUserPermission(userId, projectId);
        Map<String, Object> result = new HashMap<>();
        result.put("hasPermission", permission != null);
        result.put("permissionType", permission);

        return ResponseEntity.ok(ApiResponseDTO.success(result));
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/example/demo/controller/PermissionController.java
git commit -m "feat: 添加权限管理 API 接口"
```

---

### Task 6: 修改 ProjectsController 使用新的权限过滤

**Files:**
- Modify: `src/main/java/com/example/demo/controller/ProjectsController.java`

- [ ] **Step 1: 修改 getAllProjects 为管理员接口，添加 getMyProjects 给普通用户**

```java
package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;
import com.example.demo.service.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectsController {

    @Autowired
    private ProjectsService projectsService;

    @PostMapping("/addProject")
    public ResponseEntity<ApiResponseDTO<Projects>> addProject(@RequestBody Projects project){
        System.out.println(project);
        ApiResponseDTO<Projects> response = projectsService.addProject(project);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户有权限的项目列表（带权限信息）
     */
    @GetMapping("/getMyProjects")
    public ResponseEntity<ApiResponseDTO<List>> getMyProjects(@RequestParam String userId) {
        ApiResponseDTO<List> response = projectsService.getProjectsForUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有项目（管理员专用）
     */
    @GetMapping("/getAllProjects")
    public ResponseEntity<ApiResponseDTO<List<Projects>>> getAllProjects() {
        ApiResponseDTO<List<Projects>> response = projectsService.getAllProjects();
        return ResponseEntity.ok(response);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/example/demo/controller/ProjectsController.java
git commit -m "feat: 添加 getMyProjects 接口支持权限过滤"
```

---

## 前端实现

### Task 7: 修改 HomeView.vue 使用新的权限 API

**Files:**
- Modify: `vue_demo/src/components/HomeView.vue`

- [ ] **Step 1: 修改 getProjects 方法调用新接口**

```javascript
// 获取用户有权限的项目
const getProjects = async () => {
  try {
    const userId = userData.value.userid;
    const response = await axios.get(`/api/projects/getMyProjects?userId=${userId}`);
    projects.value = response.data.data || [];
  } catch (error) {
    console.error('获取项目失败:', error);
    showToastMessage('加载项目列表失败', 'error');
  }
}
```

- [ ] **Step 2: 修改 addNewProject 方法传递 userId**

```javascript
const addNewProject = async () => {
  if (!newProject.value.name.trim()) {
    showToastMessage('请输入项目名称', 'warning');
    return;
  }

  try {
    await axios.post('/api/projects/addProject', {
      name: newProject.value.name.trim(),
      description: newProject.value.description,
      icon: newProject.value.icon,
      color: newProject.value.color,
      userId: userData.value.userid,
      userName: userData.value.username
    });

    // 项目创建后自动添加创建者的权限记录
    const newProjectRes = await axios.get('/api/projects/getMyProjects?userId=' + userData.value.userid);
    const latestProject = newProjectRes.data.data[0];

    await axios.post('/api/permission/grant', {
      projectId: latestProject.id,
      userId: userData.value.userid,
      permissionType: 'admin'
    });

    showToastMessage('项目添加成功');
    showDialog.value = false;
    getProjects();
  } catch (error) {
    console.error('添加项目失败:', error);
    showToastMessage('添加项目失败，请重试', 'error');
  }
}
```

- [ ] **Step 3: Commit**

```bash
git add vue_demo/src/components/HomeView.vue
git commit -m "feat: 修改项目列表 API 调用支持权限过滤"
```

---

### Task 8: 创建权限管理组件

**Files:**
- Create: `vue_demo/src/components/layout/PermissionManager.vue`

- [ ] **Step 1: 创建权限管理对话框组件**

```vue
<template>
  <div class="permission-manager">
    <el-dialog
      v-model="dialogVisible"
      title="项目权限管理"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="project-info">
        <h4>{{ project.name }}</h4>
        <p>{{ project.description }}</p>
      </div>

      <div class="permission-list">
        <h5>已授权用户</h5>
        <el-table :data="userPermissions" style="width: 100%">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="permissionType" label="权限类型">
            <template #default="scope">
              <el-tag :type="getPermissionTagType(scope.row.permissionType)">
                {{ getPermissionLabel(scope.row.permissionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-select
                v-model="scope.row.permissionType"
                size="small"
                @change="updatePermission(scope.row)"
              >
                <el-option label="读写" value="write" />
                <el-option label="管理" value="admin" />
              </el-select>
              <el-button
                type="danger"
                size="small"
                @click="removePermission(scope.row)"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="add-user">
        <h5>添加用户</h5>
        <el-form :inline="true">
          <el-form-item label="用户">
            <el-select v-model="selectedUserId" placeholder="选择用户" filterable>
              <el-option
                v-for="user in availableUsers"
                :key="user.id"
                :label="user.username"
                :value="user.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="权限">
            <el-select v-model="selectedPermission">
              <el-option label="读写" value="write" />
              <el-option label="管理" value="admin" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addPermission">添加</el-button>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  project: Object
})

const dialogVisible = defineEmits(['update:visible'])[0]
const userPermissions = ref([])
const availableUsers = ref([])
const selectedUserId = ref('')
const selectedPermission = ref('read')

// 获取权限标签颜色
const getPermissionTagType = (type) => {
  const map = { write: 'warning', admin: 'danger' }
  return map[type] || 'info'
}

// 获取权限标签文字
const getPermissionLabel = (type) => {
  const map = { write: '读写', admin: '管理', owner: '所有者' }
  return map[type] || type
}

// 获取项目权限列表
const loadPermissions = async () => {
  try {
    const res = await axios.get(`/api/permission/project/${props.project.id}`)
    userPermissions.value = res.data.data || []
  } catch (error) {
    console.error('获取权限列表失败:', error)
  }
}

// 获取所有用户
const loadUsers = async () => {
  try {
    const res = await axios.get('/api/user/alluser')
    availableUsers.value = res.data || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 添加权限
const addPermission = async () => {
  if (!selectedUserId.value) {
    ElMessage.warning('请选择用户')
    return
  }

  try {
    await axios.post('/api/permission/grant', {
      projectId: props.project.id,
      userId: selectedUserId.value,
      permissionType: selectedPermission.value
    })
    ElMessage.success('添加成功')
    loadPermissions()
    selectedUserId.value = ''
  } catch (error) {
    ElMessage.error('添加失败：' + (error.response?.data?.message || error.message))
  }
}

// 更新权限
const updatePermission = async (row) => {
  try {
    await axios.post('/api/permission/update', {
      projectId: props.project.id,
      userId: row.userId,
      permissionType: row.permissionType
    })
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 移除权限
const removePermission = async (row) => {
  try {
    await axios.post('/api/permission/revoke', {
      projectId: props.project.id,
      userId: row.userId
    })
    ElMessage.success('移除成功')
    loadPermissions()
  } catch (error) {
    ElMessage.error('移除失败')
  }
}

// 初始化
loadPermissions()
loadUsers()
</script>

<style scoped>
.permission-manager {
  padding: 10px;
}

.project-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.project-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.project-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.permission-list,
.add-user {
  margin-bottom: 20px;
}

.permission-list h5,
.add-user h5 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add vue_demo/src/components/layout/PermissionManager.vue
git commit -m "feat: 添加项目权限管理对话框组件"
```

---

### Task 9: 在项目卡片添加权限管理入口

**Files:**
- Modify: `vue_demo/src/components/HomeView.vue`

- [ ] **Step 1: 在卡片添加权限管理按钮（仅管理员或项目创建者可见）**

```vue
<!-- 在 card-body 内添加 -->
<div class="card-footer">
  <font-awesome-icon :icon="['fas', 'calendar-alt']" />
  <span>{{ project.addtime }}</span>
  <button
    v-if="project.permissionType === 'admin' || project.permissionType === 'owner'"
    class="permission-btn"
    @click.stop="openPermissionManager(project)"
  >
    <font-awesome-icon :icon="['fas', 'user-shield']" />
  </button>
</div>

<!-- 添加权限管理对话框引用 -->
<PermissionManager
  v-if="currentProject"
  :project="currentProject"
  v-model:visible="permissionDialogVisible"
/>
```

- [ ] **Step 2: 添加相关逻辑**

```javascript
import PermissionManager from './layout/PermissionManager.vue'

const currentProject = ref(null)
const permissionDialogVisible = ref(false)

const openPermissionManager = (project) => {
  currentProject.value = project
  permissionDialogVisible.value = true
}
```

- [ ] **Step 3: 添加按钮样式**

```css
.permission-btn {
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
  border: none;
  color: #fff;
  padding: 4px 8px;
  border-radius: 6px;
  cursor: pointer;
  margin-left: auto;
  transition: all 0.2s;
}

.permission-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(124, 92, 191, 0.4);
}
```

- [ ] **Step 4: Commit**

```bash
git add vue_demo/src/components/HomeView.vue
git commit -m "feat: 在项目卡片添加权限管理入口"
```

---

## 数据库初始化

### Task 10: 执行数据库脚本

**Files:**
- Execute: `src/main/resources/db/add_user_role.sql`
- Execute: `src/main/resources/db/create_user_project_relation.sql`

- [ ] **Step 1: 运行 SQL 脚本添加 role 字段**

在 SQL Server Management Studio 中执行：
```sql
-- 执行 add_user_role.sql 内容
```

- [ ] **Step 2: 运行 SQL 脚本创建关联表**

```sql
-- 执行 create_user_project_relation.sql 内容
```

- [ ] **Step 3: 验证表结构**

```sql
-- 验证 Users 表
SELECT COLUMN_NAME, DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Users'
ORDER BY ORDINAL_POSITION;

-- 验证 user_project_relation 表
SELECT COLUMN_NAME, DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'user_project_relation'
ORDER BY ORDINAL_POSITION;
```

---

## 测试验证

### Task 11: 功能测试

- [ ] **Step 1: 测试管理员用户**

1. 使用管理员账号登录
2. 验证可以看到所有项目
3. 创建新项目，验证自动获得权限
4. 打开项目权限管理，添加其他用户
5. 验证可以修改/移除权限

- [ ] **Step 2: 测试普通用户**

1. 使用普通用户账号登录
2. 验证只能看到被授权的项目
3. 验证无法看到权限管理按钮

- [ ] **Step 3: 测试权限变更**

1. 管理员给用户 A 授权项目 X 的只读权限
2. 用户 A 登录，验证可以看到项目 X
3. 管理员移除权限
4. 用户 A 刷新，验证项目 X 消失

---

## 后续优化建议

1. **登录拦截器集成** - 在 LoginInterceptor 中解析用户角色并注入到请求上下文
2. **项目级别操作权限控制** - 基于 permissionType 控制增删改操作
3. **用户界面优化** - 添加专门的权限管理页面
4. **审计日志** - 记录权限变更历史
5. **批量授权** - 支持一次性给多个用户授权

---

## 文件清单

### 新增文件
- `src/main/resources/db/add_user_role.sql`
- `src/main/resources/db/create_user_project_relation.sql`
- `src/main/java/com/example/demo/entity/UserProjectRelation.java`
- `src/main/java/com/example/demo/dto/ProjectPermissionDTO.java`
- `src/main/java/com/example/demo/vo/ProjectWithPermissionVO.java`
- `src/main/java/com/example/demo/mapper/UserProjectRelationMapper.java`
- `src/main/resources/mapper/UserProjectRelationMapper.xml`
- `src/main/java/com/example/demo/service/UserProjectRelationService.java`
- `src/main/java/com/example/demo/service/impl/UserProjectRelationServiceImpl.java`
- `src/main/java/com/example/demo/controller/PermissionController.java`
- `vue_demo/src/components/layout/PermissionManager.vue`

### 修改文件
- `src/main/java/com/example/demo/entity/Users.java`
- `src/main/resources/mapper/UserMapper.xml`
- `src/main/java/com/example/demo/service/ProjectsService.java`
- `src/main/java/com/example/demo/service/impl/ProjectsServiceImpl.java`
- `src/main/java/com/example/demo/controller/ProjectsController.java`
- `vue_demo/src/components/HomeView.vue`
