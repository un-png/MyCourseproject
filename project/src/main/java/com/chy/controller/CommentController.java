package com.chy.controller;

import com.chy.pojo.Article;
import com.chy.pojo.Comment;
import com.chy.pojo.PageBean;
import com.chy.pojo.Result;
import com.chy.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /*
    * 评论模块
    * */

    //评论列表
    @GetMapping("/commentList")
    public Result<PageBean<Comment>> commentList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer articleId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String state                                                ) {
        PageBean  <Comment> pageBean = commentService.commentList(pageNum,pageSize,articleId,title,content,state);
        return Result.success(pageBean);
    }

    //改变审核状态
    @PostMapping("/commentStateEdit")
    public Result commentStateEdit(@RequestBody Comment comment){
        commentService.commentStateEdit(comment);
        return Result.success(comment);
    }

    //删除评论
    @DeleteMapping("/remove")
    public Result remove(@RequestParam Integer commentId){
        commentService.remove(commentId);
        return Result.success(commentId);
    }

}
