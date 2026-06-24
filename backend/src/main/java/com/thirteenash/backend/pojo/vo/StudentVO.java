package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentVO implements Serializable {

    private Integer studentId;

    private String studentNo;

    private String studentName;

    private String gender;

    private Integer classId;

    private String className;
}
