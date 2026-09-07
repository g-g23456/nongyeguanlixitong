package com.panduoma.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("farmland_blocks")
public class FarmlandBlock {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("block_code")
    private String blockCode;

    @JsonIgnore
    @TableField("region_id")
    private Long regionId;

    @TableField(exist = false)
    private String regionName;

    @TableField("area")
    private BigDecimal area;

    @TableField("soil_type")
    private String soilType;

    @TableField("suitable_crops")
    private String suitableCrops;

    @TableField("current_crop")
    private String currentCrop;

    @TableField("ownership")
    private String ownership;

    @TableField("planting_period")
    private String plantingPeriod;

    @TableField("status")
    private String status;

    @TableField("stat_mouth")
    private String statMouth;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
