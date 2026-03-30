package com.chy.service;

import com.chy.pojo.Article;
import com.chy.pojo.PageBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ArticleService {
    //新增内容
    void add(Article article);
    //分页查询
    PageBean<Article> list(Integer pageNum, Integer pageSize, String title,String category, String state);
    //编辑内容
    void edit(Article article);

    void remove(Integer articleId);

    //审核列表
    PageBean<Article> auditList(Integer pageNum, Integer pageSize, String title, String category, String state);
    void auditStateEdit(Article article);
    Integer findAll();
    // 获取内容分布数据
    List<Map<String, Object>> getContentDistribution();
}
