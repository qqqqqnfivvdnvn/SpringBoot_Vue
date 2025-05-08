package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.vo.UserLoginDataVO;
import com.example.demo.entity.Users;

import java.util.List;

public interface UserService {

    public List<Users> findAll();

    public ApiResponseDTO<UserLoginDataVO> login(Users user);


    public ApiResponseDTO<Users> register(Users user);


    public int delete(String id);

    public int update(Users user);

    public List<Users> getUsersByPage(String name, int offset, int limit);



}