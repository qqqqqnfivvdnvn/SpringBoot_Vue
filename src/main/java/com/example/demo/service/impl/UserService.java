package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.entity.User;
import com.example.demo.vo.UserMessage;

import java.util.List;

public interface UserService {

    public List<User> findAll();

    public int insert(User user);

    public int delete(String id);

    public int update(User user);

    public List<User> getUsersByPage(String name, int offset, int limit);

    public List<UserMessage> joinByPage(String name, int offset, int limit);



}