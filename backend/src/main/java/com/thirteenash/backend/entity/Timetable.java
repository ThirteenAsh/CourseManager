package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 排课实体类，对应数据库 timetable 表
 * 记录具体的排课结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timetable implements Serializable {

    /** 排课编号，主键 */
    private Integer timetableId;

    /** 班级课程任课编号 */
    private Integer cctId;

    /** 星期几：1周一，2周二，3周三，4周四，5周五，6周六，7周日 */
    private Integer weekday;

    /** 节次编号 */
    private Integer periodId;

    /** 上课教室 */
    private String classroom;

    /** 备注 */
    private String remark;
}
