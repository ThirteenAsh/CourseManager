package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.SchoolClass;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.SchoolClassVO;

/**
 * 班级管理服务接口
 * 定义班级的增删改查业务方法
 */
public interface SchoolClassService {

    /**
     * 分页查询班级
     * @param page 页码
     * @param pageSize 每页条数
     * @param className 班级名称关键字（模糊查询）
     * @param gradeId 所属年级ID
     * @param headTeacherId 班主任教师ID
     * @return 分页结果
     */
    PageResult page(Integer page, Integer pageSize, String className, Integer gradeId, Integer headTeacherId);

    /**
     * 新增班级
     * @param schoolClass 班级信息
     * @return 新增后的班级信息（含年级名称、班主任姓名）
     */
    SchoolClassVO add(SchoolClass schoolClass);

    /**
     * 根据ID查询班级
     * @param classId 班级ID
     * @return 班级详情
     */
    SchoolClassVO getById(Integer classId);

    /**
     * 修改班级信息
     * @param classId 班级ID
     * @param schoolClass 班级信息
     * @return 修改后的班级信息
     */
    SchoolClassVO update(Integer classId, SchoolClass schoolClass);

    /**
     * 根据ID删除班级
     * @param classId 班级ID
     */
    void deleteById(Integer classId);
}
