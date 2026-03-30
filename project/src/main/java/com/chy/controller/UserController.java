package com.chy.controller;

import com.chy.pojo.Result;
import com.chy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<String> login(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("role") String role) {
        try {
            String token = userService.login(role, username, password);
            System.out.println(token);
            return Result.success(token);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<Map<String, String>> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            Map<String, String> userInfo = userService.getUserInfoFromToken(token);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
} 