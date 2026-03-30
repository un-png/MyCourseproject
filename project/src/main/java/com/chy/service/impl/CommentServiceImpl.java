package com.chy.service.impl;

import com.chy.mapper.ArticleMapper;
import com.chy.mapper.CommentMapper;
import com.chy.pojo.Article;
import com.chy.pojo.Comment;
import com.chy.pojo.PageBean;
import com.chy.service.CommentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper  commentMapper;

    @Override
    public PageBean<Comment> commentList(Integer pageNum, Integer pageSize, Integer articleId,String title, String content, String state) {
        //创建pageBean对象
        PageBean<Comment> pageBean = new PageBean<>();
        //设置分页参数PageHelper
        PageHelper.startPage(pageNum,pageSize);

        //调用mapper
        List<Comment> cs = commentMapper.commentList(articleId,title,content,state);
        //强转：Page中提供了方法，可以获取PageHelper分页查询后，得到的总记录数和当前页数
        Page<Comment> page= (Page<Comment>) cs;

        //把数据填充到PageBean中
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    @Override
    public void commentStateEdit(Comment comment) {
        commentMapper.commentStateEdit(comment);
    }

    @Override
    public void remove(Integer commentId) {
        commentMapper.remove(commentId);
    }

    @Override
    public Integer findAll() {
        return commentMapper.findAll();
    }
}
