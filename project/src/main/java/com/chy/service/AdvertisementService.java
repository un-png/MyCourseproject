package com.chy.service;

import com.chy.pojo.Advertisement;
import com.chy.pojo.PageBean;

public interface AdvertisementService {
    // 添加广告
    void add(Advertisement advertisement);
    // 列表查询广告
    PageBean<Advertisement> advertisementList(Integer pageNum, Integer pageSize, String title, String position, String type, String status);
    //编辑内容
    void advertisementEdit(Advertisement advertisement);
    //删除广告
    void remove(Integer advertisementId);
    //广告浏览量
    Integer findAllBrowse();
}
