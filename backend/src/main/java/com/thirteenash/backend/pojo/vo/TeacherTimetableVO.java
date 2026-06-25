package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTimetableVO implements Serializable {

    private Integer semesterId;

    private Integer teacherId;

    private String teacherName;

    private List<TeacherTimetableRecordVO> records;
}
