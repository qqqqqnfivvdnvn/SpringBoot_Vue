package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserPgMapper;
import com.example.demo.service.impl.UserPgService;
import com.example.demo.vo.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPgServicelmpl implements UserPgService {

    @Autowired
    private UserPgMapper userPgMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    public void insertUserPg() {
        List<User> userMapperList = userMapper.findAll();
        for (User user : userMapperList) {
            userPgMapper.insertUser(user);
        }
    }

    @Override
    public void insertJoinUser(String name, int offset, int limit) {
        List<UserMessage>  joinList = userMapper.joinByPage(name, offset, limit);
        for (UserMessage userMessage : joinList) {
            userPgMapper.insertJoinUser(userMessage);
        }

    }


}
