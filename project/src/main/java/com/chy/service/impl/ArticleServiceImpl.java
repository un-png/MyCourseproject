package com.chy.service.impl;

import com.chy.mapper.ArticleMapper;
import com.chy.pojo.Article;
import com.chy.pojo.PageBean;
import com.chy.service.ArticleService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void add(Article article) {
        //补充属性值
        article.setCreateAt(LocalDateTime.now());
        articleMapper.add(article);
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, String title, String category, String state) {
        //创建pageBean对象
        PageBean<Article> pageBean = new PageBean<>();
        //设置分页参数PageHelper
        PageHelper.startPage(pageNum,pageSize);

        //调用mapper
        List<Article> as = articleMapper.list(title,category,state);
        //强转：Page中提供了方法，可以获取PageHelper分页查询后，得到的总记录数和当前页数
        Page<Article> page= (Page<Article>) as;

        //把数据填充到PageBean中
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    @Override
    public void edit(Article article) {
        articleMapper.edit(article);
    }

    @Override
    public void remove(Integer articleId) {
        articleMapper.remove(articleId);
    }

    @Override
    public PageBean<Article> auditList(Integer pageNum, Integer pageSize, String title, String category, String state) {
        //创建pageBean对象
        PageBean<Article> pageBean = new PageBean<>();
        //设置分页参数PageHelper
        PageHelper.startPage(pageNum,pageSize);

        //调用mapper
        List<Article> as = articleMapper.auditList(title,category,state);
        //强转：Page中提供了方法，可以获取PageHelper分页查询后，得到的总记录数和当前页数
        Page<Article> page= (Page<Article>) as;

        //把数据填充到PageBean中
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    @Override
    public void auditStateEdit(Article article) {
        articleMapper.auditStateEdit(article);
    }

    @Override
    public Integer findAll() {
        return articleMapper.findAll();
    }

    @Override
    public List<Map<String, Object>> getContentDistribution() {
        return articleMapper.getContentDistribution();
    }
}
