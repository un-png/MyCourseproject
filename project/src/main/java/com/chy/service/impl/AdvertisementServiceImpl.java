package com.chy.service.impl;

import com.chy.mapper.AdvertisementMapper;
import com.chy.pojo.Advertisement;
import com.chy.pojo.Article;
import com.chy.pojo.PageBean;
import com.chy.service.AdvertisementService;
import com.chy.service.ArticleService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Override
    public void add(Advertisement  advertisement) {
        advertisement.setCreateAt(LocalDateTime.now());
         advertisementMapper.add(advertisement);
    }

    // 分页查询
    @Override
    public PageBean<Advertisement> advertisementList(Integer pageNum, Integer pageSize, String title, String position, String type, String status) {
        //创建pageBean对象
        PageBean<Advertisement> pageBean = new PageBean<>();
        //设置分页参数PageHelper
        PageHelper.startPage(pageNum,pageSize);

        //调用mapper
        List<Advertisement> as = advertisementMapper.advertisementList(title, position, type, status);
        //强转：Page中提供了方法，可以获取PageHelper分页查询后，得到的总记录数和当前页数
        Page<Advertisement> page= (Page<Advertisement>) as;

        //把数据填充到PageBean中
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    @Override
    public void advertisementEdit(Advertisement advertisement) {
        advertisementMapper.advertisementEdit(advertisement);
    }

    @Override
    public void remove(Integer advertisementId) {
        advertisementMapper.remove(advertisementId);
    }

    @Override
    public Integer findAllBrowse() {
        return advertisementMapper.findAllBrowse();
    }
}
