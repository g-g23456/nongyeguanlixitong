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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("equipment")
public class Equipment {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("equipment_code")
    private String equipmentCode;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("region_id")
    private Long regionId;

    @TableField("efficiency")
    private String efficiency;

    @TableField("status")
    private String status;

    @TableField("stat_month")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statMonth;

    @TableField("score")
    private Integer score;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
