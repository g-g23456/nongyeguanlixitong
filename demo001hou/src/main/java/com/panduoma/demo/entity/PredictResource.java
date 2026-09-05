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
@TableName("predict_resource")
public class PredictResource {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("stat_month")
    private String statMonth;

    @TableField("region_id")
    private Long regionId;

    @TableField("water_demand")
    private BigDecimal waterDemand;

    @TableField("fertilizer_demand")
    private BigDecimal fertilizerDemand;

    @TableField("labor_demand")
    private Integer laborDemand;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
