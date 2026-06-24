package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassVO implements Serializable {

    private Integer classId;

    private String className;

    private Integer gradeId;

    private String gradeName;

    private Integer headTeacherId;

    private String headTeacherName;
}
