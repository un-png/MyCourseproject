package com.chy.controller;

import com.chy.pojo.Advertisement;
import com.chy.pojo.Article;
import com.chy.pojo.PageBean;
import com.chy.pojo.Result;
import com.chy.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advertisement")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @RequestMapping("/add")
    public Result add(@RequestBody Advertisement advertisement){
        advertisementService.add(advertisement);
        return Result.success(advertisement);
    }

    @GetMapping("/advertisementList")
    public Result<PageBean<Advertisement>> advertisementList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status){
        PageBean<Advertisement> pageBean = advertisementService.advertisementList(pageNum, pageSize, title, position, type, status);
        return Result.success(pageBean);
    }

    @PostMapping("/advertisementEdit")
    public Result advertisementEdit(@RequestBody Advertisement advertisement){
        advertisementService.advertisementEdit(advertisement);
        return Result.success(advertisement);
    }

    @DeleteMapping("/remove")
    public Result remove(@RequestParam Integer advertisementId){
        advertisementService.remove(advertisementId);
        return Result.success(advertisementId);
    }



}
