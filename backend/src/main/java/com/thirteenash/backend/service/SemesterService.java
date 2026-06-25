package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Semester;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.SemesterVO;

public interface SemesterService {

    PageResult page(Integer page, Integer pageSize, String schoolYear, Integer term, Integer isCurrent);

    SemesterVO add(Semester semester);

    SemesterVO getById(Integer semesterId);

    SemesterVO getCurrent();

    SemesterVO update(Integer semesterId, Semester semester);

    void deleteById(Integer semesterId);
}
