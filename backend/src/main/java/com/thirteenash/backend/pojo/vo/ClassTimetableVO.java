package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTimetableVO implements Serializable {

    private Integer semesterId;

    private Integer classId;

    private String className;

    private List<ClassTimetableRecordVO> records;
}
