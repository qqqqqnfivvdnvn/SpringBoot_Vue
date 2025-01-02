package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserPgMapper;
import com.example.demo.service.impl.UserService;
import com.example.demo.vo.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserMapper userMapper;


    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int delete(String id) {
        return userMapper.delete(id);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public List<User> getUsersByPage(String name, int page, int limit) {
        int offset = (page - 1) * limit;
        return userMapper.findByPage(name, offset, limit);
    }

    @Override
    public List<UserMessage> joinByPage(String name, int page, int limit) {
        int offset = (page - 1) * limit;
        return userMapper.joinByPage(name, offset, limit);
    }




}
