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
@TableName("resource_util_trend")
public class ResourceUtilTrend {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("stat_mouth")
    private String statMouth;

    @TableField("land_util_rate")
    private BigDecimal landUtilRate;

    @TableField("water_util_rate")
    private BigDecimal waterUtilRate;

    @TableField("material_util_rate")
    private BigDecimal materialUtilRate;

    @TableField("labor_util_rate")
    private BigDecimal laborUtilRate;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
