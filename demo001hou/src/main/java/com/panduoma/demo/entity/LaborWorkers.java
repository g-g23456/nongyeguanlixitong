package com.panduoma.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("labor_workers")
public class LaborWorkers {

    @TableId(value = "labor_id")
    private String laborId;

    @TableField("labor_name")
    private String laborName;

    @TableField("work_type")
    private String workType;

    @TableField("skill_level")
    private String skillLevel;

    @TableField("region_id")
    private Long regionId;

    @TableField("daily_salary")
    private BigDecimal dailySalary;

    @TableField("available_time")
    private String availableTime;

    @TableField("work_status")
    private String workStatus;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
