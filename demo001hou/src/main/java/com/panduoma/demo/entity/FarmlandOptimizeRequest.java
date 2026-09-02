package com.panduoma.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmlandOptimizeRequest {

    /** 优化目标 */
    private String goal;

    /** 约束条件 */
    private String constraintMode;

    /** 年份 */
    private Integer year;
}
