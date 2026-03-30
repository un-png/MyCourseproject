package com.chy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Advertisement {
    private Integer advertisementId;// 广告id
    private String title;//  广告标题
    private String position;//  广告位置
    private String type;//  广告类型
    private String mediaUrl;//  媒体资源
    private String link;//  链接
    private String state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String targetUser;
    private String status;
    private Integer clickCount;
    private LocalDateTime createAt;
}
