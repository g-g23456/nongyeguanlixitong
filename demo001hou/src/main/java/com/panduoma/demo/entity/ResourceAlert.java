package com.panduoma.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("resource_alert")
public class ResourceAlert {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("alert_type")
    private String alertType;

    @TableField("region_name")
    private String regionName;

    @TableField("target_name")
    private String targetName;

    @TableField("alert_level")
    private String alertLevel;

    @TableField("alert_content")
    private String alertContent;

    @TableField("suggest_action")
    private String suggestAction;

    @TableField("is_handle")
    private Integer isHandle;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("handle_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;
}
