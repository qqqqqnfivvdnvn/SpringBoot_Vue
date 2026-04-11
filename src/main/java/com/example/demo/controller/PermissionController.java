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
            ApiResponseDTO<Boolean> response = ApiResponseDTO.success(true);
            response.setMsg("授权成功");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(ApiResponseDTO.error("该用户已有此项目权限"));
        }
    }

    /**
     * 移除项目权限
     */
    @PostMapping("/revoke")
    public ResponseEntity<ApiResponseDTO<Boolean>> revokePermission(@RequestBody ProjectPermissionDTO dto) {
        boolean success = userProjectRelationService.removePermission(dto.getUserId(), dto.getProjectId());

        if (success) {
            ApiResponseDTO<Boolean> response = ApiResponseDTO.success(true);
            response.setMsg("权限已移除");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(ApiResponseDTO.error("权限记录不存在"));
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
            ApiResponseDTO<Boolean> response = ApiResponseDTO.success(true);
            response.setMsg("权限已更新");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(ApiResponseDTO.error("权限记录不存在"));
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
