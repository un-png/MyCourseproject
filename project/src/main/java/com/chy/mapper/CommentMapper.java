package com.chy.mapper;

import com.chy.pojo.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> commentList(Integer articleId,String title, String content, String state);

    @Update("update comment set state = #{state} where comment_id = #{commentId}")
    void commentStateEdit(Comment comment);

    @Delete("delete from comment where comment_id = #{commentId}")
    void remove(Integer commentId);

    //查询所有评论数量
    @Select("select count(*) from comment")
    Integer findAll();
}
