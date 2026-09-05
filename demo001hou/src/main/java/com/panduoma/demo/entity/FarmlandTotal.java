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
@TableName("farmland_total")
public class FarmlandTotal {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("total_quota")
    private BigDecimal totalQuota;

    @TableField("used_quota")
    private BigDecimal usedQuota;

    @TableField("residue_quota")
    private BigDecimal residueQuota;

    @TableField("reused_quota")
    private BigDecimal reusedQuota;
}
