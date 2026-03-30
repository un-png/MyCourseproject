package com.chy.mapper;

import com.chy.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from users where username = #{username}")
    // 根据用户名查询用户信息
    User findByUserName(String username);
    //  查询用户总数
    @Select("select count(*) from users where role!='管理员'")
    Integer findAll();

    // 登录
//    @Select("select * from user where username = #{username} and password = #{password}")
//    void login(String username, String password);
}
