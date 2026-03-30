package com.chy.mapper;

import com.chy.pojo.FeedBack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FeedbackMapper {

    // 查询反馈列表
    List<FeedBack> feedbackList(Integer feedbackId, String feedbackType, String title, String state);

    // 修改反馈状态
    @Update("update feedback set state = #{state},reply=#{reply} where feedback_id = #{feedbackId}")
    void feedbackStateEdit(FeedBack feedback);
}
