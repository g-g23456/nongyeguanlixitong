package com.panduoma.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("login_log")
public class LoginLog {

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @TableField("u_id")
    private Long uId;

    @TableField("role")
    private String role;

    @TableField("login_time")
    private LocalDateTime loginTime;

    @TableField("logout_time")
    private LocalDateTime logoutTime;
}
