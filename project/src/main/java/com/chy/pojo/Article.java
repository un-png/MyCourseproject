package com.chy.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Article {
    private Integer articleId;
    @NotEmpty
    @Pattern(regexp = "^\\S{1,30}$")
    private String title;
    @NotEmpty
    private String content;
    private String state;//状态
    private String category;//内容分类
    private String author;
    private LocalDateTime createAt;
}
