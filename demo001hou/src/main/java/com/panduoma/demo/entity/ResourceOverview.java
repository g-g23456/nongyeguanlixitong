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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("resource_overview")
public class ResourceOverview {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("stat_mouth")
    private String statMouth;

    @TableField("farmland")
    private BigDecimal farmland;

    @TableField("water")
    private BigDecimal water;

    @TableField("material")
    private BigDecimal material;

    @TableField("labor")
    private BigDecimal labor;

    @TableField("machine")
    private BigDecimal machine;
}
