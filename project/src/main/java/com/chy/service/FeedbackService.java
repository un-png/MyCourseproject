package com.chy.service;


import com.chy.pojo.FeedBack;
import com.chy.pojo.PageBean;

public interface FeedbackService {

    //  反馈列表
    PageBean<FeedBack> feedbackList(Integer pageNum, Integer pageSize, Integer feedbackId, String feedbackType, String title, String state);

    //修改反馈状态
    void feedbackStateEdit(FeedBack feedback);
}
