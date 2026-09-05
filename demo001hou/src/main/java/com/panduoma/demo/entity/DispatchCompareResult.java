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
@TableName("dispatch_compare_result")
public class DispatchCompareResult {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("yield_benefit")
    private BigDecimal yieldBenefit;

    @TableField("risk_control")
    private BigDecimal riskControl;

    @TableField("soil_protect")
    private BigDecimal soilProtect;

    @TableField("labor_util")
    private BigDecimal laborUtil;

    @TableField("material_efficiency")
    private BigDecimal materialEfficiency;

    @TableField("water_save")
    private BigDecimal waterSave;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
