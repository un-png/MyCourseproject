package com.chy.pojo;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    private Integer commentId;
    private Integer articleId;
    private String title;
    private String content;
    @NotEmpty
    private String user;
    @NotEmpty
    private String state;
    private LocalDateTime createAt;
}
