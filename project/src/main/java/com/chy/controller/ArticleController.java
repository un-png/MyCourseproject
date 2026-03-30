package com.chy.controller;

import com.chy.pojo.Article;
import com.chy.pojo.PageBean;
import com.chy.pojo.Result;
import com.chy.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/add")
    public Result add(@RequestBody @Validated Article article){//实体校验注解
        articleService.add(article);
        return Result.success(article);
    }

    //分页查询
    @GetMapping("/list")
    public Result <PageBean <Article>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String state){
        PageBean  <Article> pageBean = articleService.list(pageNum,pageSize,title,category,state);
        return Result.success(pageBean);
    }

    //编辑内容
    @PostMapping("/edit")
    public Result edit(@RequestBody @Validated Article article){
        articleService.edit(article);
        return Result.success(article);
    }

    //删除内容
    @DeleteMapping("/remove")
    public Result remove(@RequestParam Integer articleId){
        articleService.remove(articleId);
        return Result.success(articleId);
    }

    /*
    审核内容模块
    */

    //审核列表
    @GetMapping("/auditList")
    public Result<PageBean<Article>> auditList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String state                                                ) {
            PageBean  <Article> pageBean = articleService.auditList(pageNum,pageSize,title,category,state);
            return Result.success(pageBean);
    }
    //改变审核状态
    @PostMapping("/auditStateEdit")
    public Result auditStateEdit(@RequestBody Article article){
        articleService.auditStateEdit(article);
        return Result.success(article);
    }
}
