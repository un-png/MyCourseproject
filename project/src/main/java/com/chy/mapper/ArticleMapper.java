package com.chy.mapper;

import com.chy.pojo.Article;
import com.chy.pojo.Comment;
import com.chy.pojo.PageBean;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {
    //新增
    @Insert("insert into articles(title,content,state,category,author,create_at) " +
            "values(#{title},#{content},#{state},#{category},#{author},#{createAt})")
    void add(Article article);

    //分页查询
    List<Article> list(String title, String category, String state);

    //编辑内容
    @Update("update articles set title=#{title},content=#{content},category=#{category},author=#{author} where article_id=#{articleId}")
    void edit(Article article);

    //删除内容
    @Delete("delete from articles where article_id=#{articleId}")
    void remove(Integer articleId);

    //审核列表
    List<Article> auditList(String title, String category,  String state);

    //改变审核状态
    @Update("update articles set state=#{state} where article_id=#{articleId}")
    void auditStateEdit(Article article);

    //查询所有内容
    @Select("select count(*) from articles")
    Integer findAll();

    @Select("select * from articles where state = 1 ")
    List<Article> findAllArticles();

    @Select("SELECT category, COUNT(*) as value FROM articles GROUP BY category")
    List<Map<String, Object>> getContentDistribution();
}
