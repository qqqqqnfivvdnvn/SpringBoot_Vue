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
     * 设置用户项目权限（启用/禁用）
     */
    @PostMapping("/set")
    public ResponseEntity<ApiResponseDTO<Boolean>> setPermission(@RequestBody ProjectPermissionDTO dto) {
        System.out.println("=== Controller received === userId: " + dto.getUserId() + ", projectId: " + dto.getProjectId() + ", hasPermission: " + dto.getHasPermission());
        boolean success = userProjectRelationService.setPermission(dto.getUserId(), dto.getProjectId(), dto.getHasPermission());
        System.out.println("=== Controller result: " + success);

        if (success) {
            ApiResponseDTO<Boolean> response = ApiResponseDTO.success(true);
            response.setMsg(dto.getHasPermission() != null && dto.getHasPermission() == 1 ? "权限已启用" : "权限已禁用");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(ApiResponseDTO.error("权限记录不存在"));
        }
    }

    /**
     * 获取项目的所有用户权限
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponseDTO<List<Map<String, Object>>>> getProjectPermissions(
        @PathVariable String projectId) {
        List<Map<String, Object>> permissions = userProjectRelationService.getPermissionsByProjectId(projectId);
        return ResponseEntity.ok(ApiResponseDTO.success(permissions));
    }

    /**
     * 检查用户对项目的权限
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> checkPermission(
        @RequestParam String userId,
        @RequestParam String projectId) {

        boolean hasPermission = userProjectRelationService.hasPermission(userId, projectId);
        Map<String, Object> result = new HashMap<>();
        result.put("hasPermission", hasPermission);

        return ResponseEntity.ok(ApiResponseDTO.success(result));
    }
}
