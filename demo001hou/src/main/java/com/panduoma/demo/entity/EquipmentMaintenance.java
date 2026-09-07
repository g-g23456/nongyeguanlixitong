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
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("equipment_maintenance")
public class EquipmentMaintenance {

    @TableId(value = "cost_id", type = IdType.AUTO)
    private Long costId;

    @TableField("stat_mouth")
    private String statMouth;

    @TableField("total_cost")
    private BigDecimal totalCost;

    @TableField("plan_count")
    private Integer planCount;

    @TableField("manage_count")
    private Integer manageCount;

    @TableField("proceed_count")
    private Integer proceedCount;

    @TableField("overdue_count")
    private Integer overdueCount;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
