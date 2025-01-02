package com.example.demo.controller;


import com.example.demo.entity.User;
import com.example.demo.service.impl.UserPgService;
import com.example.demo.service.impl.UserService;
import com.example.demo.vo.UserMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController

@RequestMapping("/user")

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserPgService userPgService;

    @GetMapping("/alluser")
    public String getUser() throws JsonProcessingException {
        List<User> userList = userService.findAll();
        // 使用 Jackson 将列表转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userList);
    }

    @PostMapping("/insertuser")
    public String insertUser(User user) throws JsonProcessingException {
        UUID uuid = UUID.randomUUID();
        user.setId(uuid.toString().replace("-", ""));
        int i = userService.insert(user);
        if (i == 0) {
            return "插入条数" + i;
        } else {
            return "插入条数" + i;
        }
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
    public String updateUser(User user) throws JsonProcessingException {
        int i = userService.update(user);
        if (i == 0) {
            return "更新条数" + i;
        } else {
            return "更新条数" + i;
        }
    }

    @GetMapping("/users")
    public List<User> getUsers(
            @RequestParam String name,
            @RequestParam int offset,
            @RequestParam int limit
    ) {
        return userService.getUsersByPage(name, offset, limit);
    }


    @GetMapping("/joinusers")
    public List<UserMessage> joinUsers(
            @RequestParam String name,
            @RequestParam int offset,
            @RequestParam int limit
    ) {
        return userService.joinByPage(name, offset, limit);
    }

    @GetMapping("/insertpg")
    public Void insertPg() throws JsonProcessingException {

        userPgService.insertUserPg();
        return null;
    }

}