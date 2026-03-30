package com.chy.service.impl;

import com.chy.mapper.FeedbackMapper;
import com.chy.pojo.Comment;
import com.chy.pojo.FeedBack;
import com.chy.pojo.PageBean;
import com.chy.service.FeedbackService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;
    @Override
    public PageBean<FeedBack> feedbackList(Integer pageNum, Integer pageSize, Integer feedbackId, String feedbackType, String title, String state) {
        //创建pageBean对象
        PageBean<FeedBack> pageBean = new PageBean<>();
        //设置分页参数PageHelper
        PageHelper.startPage(pageNum,pageSize);

        //调用mapper
        List<FeedBack> cs = feedbackMapper.feedbackList(feedbackId,feedbackType,title,state);
        //强转：Page中提供了方法，可以获取PageHelper分页查询后，得到的总记录数和当前页数
        Page<FeedBack> page= (Page<FeedBack>) cs;

        //把数据填充到PageBean中
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    @Override
    public void feedbackStateEdit(FeedBack feedback) {
        feedbackMapper.feedbackStateEdit(feedback);
    }
}
