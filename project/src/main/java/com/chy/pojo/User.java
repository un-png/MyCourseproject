package com.chy.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class User {
    private Integer userId;
    private String username;
    @JsonIgnore//忽略字段
    private String password;
    private String role;
    private Date createdAt;

}
