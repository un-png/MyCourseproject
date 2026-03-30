package com.chy.mapper;

import com.chy.pojo.Advertisement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdvertisementMapper {
    //添加广告
    void add(Advertisement advertisement);
    //分页查询广告
    List<Advertisement> advertisementList( String title, String position, String type, String status);
    //修改内容
    void advertisementEdit(Advertisement advertisement);
    //删除广告
    @Delete("delete from advertisement where id = #{advertisementId}")
    void remove(Integer advertisementId);

    @Select("select sum(click_count) from advertisement")
    Integer findAllBrowse();
}
