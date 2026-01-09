package com.example.demo.controller;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.vo.UserLoginDataVO;
import com.example.demo.entity.Users;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;
import java.util.Map;



@RestController

@RequestMapping("/user")

public class UserController {

    @Autowired
    private UserService userService;



    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<UserLoginDataVO>> login(@RequestBody Users user) {
        System.out.println(user);
        ApiResponseDTO<UserLoginDataVO> result = userService.login(user);
        return ResponseEntity.ok(result);

    }



    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<Users>> registerUser(@RequestBody Users user) {
        ApiResponseDTO<Users> result = userService.register(user);

        return ResponseEntity.ok(result);


    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        // 清除安全上下文
        SecurityContextHolder.clearContext();

        // 使当前会话失效
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 清除客户端Cookie（如果有）
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        // 返回成功响应
        return ResponseEntity.ok()
                .body(Map.of(
                        "code", 200,
                        "message", "登出成功"
                ));
    }



    @GetMapping("/alluser")
    public String getUser() throws JsonProcessingException {
        List<Users> userList = userService.findAll();
        // 使用 Jackson 将列表转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userList);
    }


    @GetMapping("/deleteuser")
    public String insertUser(String id) throws JsonProcessingException {
        int i = userService.delete(id);
        if (i == 0) {
            return "删除条数" + i;
        } else {
            return "删除成功" + i;
        }
    }

    @PostMapping("/updateuser")
    public String updateUser(Users user) throws JsonProcessingException {
        int i = userService.update(user);
        if (i == 0) {
            return "更新条数" + i;
        } else {
            return "更新条数" + i;

        }
    }

    @GetMapping("/pageuser")
    public List<Users> getUsers(
            @RequestParam String name,
            @RequestParam int offset,
            @RequestParam int limit
    ) {
        return userService.getUsersByPage(name, offset, limit);
    }




}