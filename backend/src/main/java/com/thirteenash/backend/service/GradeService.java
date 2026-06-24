package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Grade;
import com.thirteenash.backend.pojo.result.PageResult;

public interface GradeService {

    PageResult page(Integer page, Integer pageSize, String gradeName);

    void add(Grade grade);

    Grade getById(Integer gradeId);

    void update(Integer gradeId, Grade grade);

    void deleteById(Integer gradeId);
}
