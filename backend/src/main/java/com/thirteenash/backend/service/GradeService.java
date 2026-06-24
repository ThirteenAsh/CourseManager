package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Grade;
import com.thirteenash.backend.pojo.result.PageResult;

/**
 * 年级管理服务接口
 * 定义年级的增删改查业务方法
 */
public interface GradeService {

    /**
     * 分页查询年级
     * @param page 页码
     * @param pageSize 每页条数
     * @param gradeName 年级名称关键字（模糊查询）
     * @return 分页结果
     */
    PageResult page(Integer page, Integer pageSize, String gradeName);

    /**
     * 新增年级
     * @param grade 年级信息
     */
    void add(Grade grade);

    /**
     * 根据ID查询年级
     * @param gradeId 年级ID
     * @return 年级信息
     */
    Grade getById(Integer gradeId);

    /**
     * 修改年级信息
     * @param gradeId 年级ID
     * @param grade 年级信息
     */
    void update(Integer gradeId, Grade grade);

    /**
     * 根据ID删除年级
     * @param gradeId 年级ID
     */
    void deleteById(Integer gradeId);
}
