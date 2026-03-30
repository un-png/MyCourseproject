package com.chy.controller;

import com.chy.pojo.Comment;
import com.chy.pojo.Result;
import com.chy.service.AdvertisementService;
import com.chy.service.ArticleService;
import com.chy.service.CommentService;
import com.chy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AdvertisementService advertisementService;


    @GetMapping("/userAccount")
    public Result userAccount() {
        return Result.success(userService.findAll());
    }

    @GetMapping("/commentAccount")
    public Result commentAccount(){
        return Result.success(commentService.findAll());
    }

    @GetMapping("/articleAccount")
    public Result articleAccount(){
        return Result.success(articleService.findAll());
    }

    @GetMapping("/browseAccount")
    public Result browseAccount(){
        return Result.success(advertisementService.findAllBrowse());
    }

    // 内容分布统计
    @GetMapping("/contentDistribution")
    public Result<List<Map<String, Object>>> getContentDistribution() {
        try {
            List<Map<String, Object>> data = articleService.getContentDistribution();
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取内容分布数据失败：" + e.getMessage());
        }
    }

}
