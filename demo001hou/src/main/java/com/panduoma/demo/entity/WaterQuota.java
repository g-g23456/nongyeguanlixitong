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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("water_quota")
public class WaterQuota {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("region_id")
    private Long regionId;

    @TableField(exist = false)
    private String regionName;

    @TableField("period")
    private String period;

    @TableField("quota")
    private BigDecimal quota;

    @TableField("used")
    private BigDecimal used;

    @TableField("remain")
    private BigDecimal remain;

    @TableField("usage_rate")
    private BigDecimal usageRate;

    @TableField("status")
    private String status;

    @TableField("ai_evaluate")
    private String aiEvaluate;

    @TableField("stat_month")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statMonth;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
