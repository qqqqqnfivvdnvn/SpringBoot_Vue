package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponse;
import com.example.demo.vo.UserLoginData;
import com.example.demo.entity.User;

import java.util.List;

public interface UserService {

    public List<User> findAll();

    public ApiResponse<UserLoginData> login(User user);


    public ApiResponse<User> findByName(User user);


    public int delete(String id);

    public int update(User user);

    public List<User> getUsersByPage(String name, int offset, int limit);



}