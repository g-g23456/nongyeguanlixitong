package com.panduoma.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("water_analysis")
public class WaterAnalysis {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("region_id")
    private Long regionId;

    @TableField("year")
    private Integer year;

    @TableField("analysis_result")
    private String analysisResult;
}
