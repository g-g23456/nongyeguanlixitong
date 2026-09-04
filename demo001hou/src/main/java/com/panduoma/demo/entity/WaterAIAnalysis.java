package com.panduoma.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("water_ai_analysis")
public class WaterAIAnalysis {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("region_id")
    private Long regionId;

    @TableField("request")
    private String request;

    @TableField("result")
    private String result;

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
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String regionName;
}
