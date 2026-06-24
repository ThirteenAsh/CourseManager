package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 节次实体类，对应数据库 period 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Period implements Serializable {

    /** 节次编号，主键 */
    private Integer periodId;

    /** 第几节课 */
    private Integer periodNo;

    /** 上课时间 */
    private LocalTime startTime;

    /** 下课时间 */
    private LocalTime endTime;

    /** 备注，例如上午第1节、下午第2节等 */
    private String remark;
}
