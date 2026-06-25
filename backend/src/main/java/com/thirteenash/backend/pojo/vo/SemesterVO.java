package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterVO implements Serializable {

    private Integer semesterId;

    private String schoolYear;

    private Integer term;

    private String termName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer isCurrent;
}
