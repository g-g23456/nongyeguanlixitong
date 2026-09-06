package com.panduoma.demo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_data")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    @JsonProperty("userId")
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("name")
    private String name;

    @TableField("role")
    private String role;

    @TableField("role_name")
    @JsonProperty("roleName")
    private String role_name;

    @TableField("department")
    private String department;

    @TableField("status")
    private String status;

}
