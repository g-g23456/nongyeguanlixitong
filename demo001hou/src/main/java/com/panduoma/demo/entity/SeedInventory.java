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
@TableName("seed_inventory")
public class SeedInventory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("sku")
    private String sku;

    @TableField("product_name")
    private String productName;

    @TableField("category")
    private String category;

    @TableField("inventory")
    private BigDecimal inventory;

    @TableField("safety_threshold")
    private BigDecimal safetyThreshold;

    @TableField("status")
    private String status;

    @TableField("stat_month")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statMonth;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
