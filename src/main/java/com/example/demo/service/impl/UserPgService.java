package com.example.demo.service.impl;

import com.example.demo.entity.User;

import java.util.List;

public interface UserPgService {

    void insertUserPg();

    void insertJoinUser(String name, int offset, int limit);

}
