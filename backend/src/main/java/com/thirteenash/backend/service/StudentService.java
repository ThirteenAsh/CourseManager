package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Student;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.StudentVO;

public interface StudentService {

    PageResult page(Integer page, Integer pageSize, String studentNo, String studentName, String gender, Integer classId);

    StudentVO add(Student student);

    StudentVO getById(Integer studentId);

    StudentVO update(Integer studentId, Student student);

    void deleteById(Integer studentId);
}
