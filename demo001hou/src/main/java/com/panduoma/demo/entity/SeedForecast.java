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
@TableName("seed_forecast")
public class SeedForecast {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_code")
    private String productCode;

    @TableField("stat_mouth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statMouth;

    @TableField("history_consumption")
    private BigDecimal historyConsumption;

    @TableField("expected_consumption")
    private BigDecimal expectedConsumption;

    @TableField("confidence")
    private BigDecimal confidence;

    @TableField("current_inventory")
    private BigDecimal currentInventory;

    @TableField("safety_threshold")
    private BigDecimal safetyThreshold;

    @TableField("gap_quantity")
    private BigDecimal gapQuantity;

    @TableField("alert_level")
    private Integer alertLevel;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
