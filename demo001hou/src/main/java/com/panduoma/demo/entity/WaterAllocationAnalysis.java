package com.panduoma.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@TableName("water_allocation_analysis")
public class WaterAllocationAnalysis {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private String taskId;

    @TableField("task_name")
    private String taskName;

    @TableField("alloc_period")
    private String allocPeriod;

    @TableField("optimize_target")
    private String optimizeTarget;

    @JsonIgnore
    @TableField("region_id")
    private Long regionId;

    @TableField("region_name")
    private String regionName;

    @TableField("total_quota")
    private BigDecimal totalQuota;

    @TableField("theoretical_demand")
    private BigDecimal theoreticalDemand;

    @TableField("traditional_usage")
    private BigDecimal traditionalUsage;

    @TableField("water_saving_rate")
    private BigDecimal waterSavingRate;

    @TableField("yield_increase_rate")
    private BigDecimal yieldIncreaseRate;

    @TableField("unit")
    private String unit;

    @TableField("water_saving_irrigation")
    private BigDecimal waterSavingIrrigation;

    @TableField("sustainability")
    private BigDecimal sustainability;

    @TableField("utilization_rate")
    private BigDecimal utilizationRate;

    @TableField("balance")
    private BigDecimal balance;

    @TableField("yield_guarantee")
    private BigDecimal yieldGuarantee;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
