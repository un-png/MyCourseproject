package com.chy.service;

import com.chy.pojo.Comment;
import com.chy.pojo.PageBean;

public interface CommentService {
    //分页列表查询
    PageBean<Comment> commentList(Integer pageNum, Integer pageSize, Integer articleId,String title,String content, String state);
    //修改审核状态
    void commentStateEdit(Comment comment);

    void remove(Integer commentId);

    Integer findAll();
}
