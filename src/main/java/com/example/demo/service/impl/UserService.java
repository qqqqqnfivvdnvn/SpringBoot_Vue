package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.vo.UserLoginDataVO;
import com.example.demo.entity.User;

import java.util.List;

public interface UserService {

    public List<User> findAll();

    public ApiResponseDTO<UserLoginDataVO> login(User user);


    public ApiResponseDTO<User> findByName(User user);


    public int delete(String id);

    public int update(User user);

    public List<User> getUsersByPage(String name, int offset, int limit);



}