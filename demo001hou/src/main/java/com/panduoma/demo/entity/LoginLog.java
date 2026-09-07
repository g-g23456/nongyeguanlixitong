package com.panduoma.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("login_logs")
public class LoginLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long logId;

    @TableField("user_id")
    private Long uId;

    @TableField("role")
    private String role;

    @TableField("created_time")
    private LocalDateTime loginTime;

    @TableField("logout_time")
    private LocalDateTime logoutTime;
}
