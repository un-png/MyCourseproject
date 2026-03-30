package com.chy.service;

import com.chy.pojo.User;

import java.util.Collection;
import java.util.Map;

public interface UserService {
    User findByUserName(String username);

    String login(String role,String username, String password);

    //  查询所有用户数量
    Integer findAll();

    Map<String, String> getUserInfoFromToken(String token);
}
