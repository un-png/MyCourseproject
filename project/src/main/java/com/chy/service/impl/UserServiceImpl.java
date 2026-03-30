package com.chy.service.impl;

import com.chy.mapper.UserMapper;
import com.chy.pojo.User;
import com.chy.service.UserService;
import com.chy.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public String login(String role,String username, String password) {
        User user = findByUserName(username);
        System.out.println(user);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!role.equals(user.getRole())) {
            throw new RuntimeException("用户角色错误");
        }

        if (!user.getRole().equals("管理员")) {
            throw new RuntimeException("用户权限错误");
        }
        
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT token
        return JwtUtil.generateToken(username);
    }

    @Override
    public Integer findAll() {
        return userMapper.findAll();
    }

    @Override
    public Map<String, String> getUserInfoFromToken(String token) {
        String username = JwtUtil.getUsernameFromToken(token);
        User user = findByUserName(username);

        if (user == null) {
            throw new RuntimeException("无法根据token获取用户信息");
        }

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        return userInfo;
    }
}
