package com.chy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedBack {
    private Integer feedbackId;
    private String feedbackType;
    private String title;
    private String content;
    private String user;
    private String state;
    private LocalDateTime submitTime;
    private String  reply;
}
