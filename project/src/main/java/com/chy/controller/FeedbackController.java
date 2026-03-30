package com.chy.controller;

import com.chy.pojo.Comment;
import com.chy.pojo.FeedBack;
import com.chy.pojo.PageBean;
import com.chy.pojo.Result;
import com.chy.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/feedbackList")
    public Result<PageBean<FeedBack>> feedbackList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer feedbackId,
            @RequestParam(required = false) String feedbackType,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String state                                                ) {
        PageBean  <FeedBack> pageBean = feedbackService.feedbackList(pageNum,pageSize,feedbackId,feedbackType,title,state);
        return Result.success(pageBean);
    }

    //反馈状态修改
    @PostMapping("/feedbackStateEdit")
    public Result feedbackStateEdit(@RequestBody FeedBack feedback) {
        feedbackService.feedbackStateEdit(feedback);
        return Result.success(feedback);
    }
}
