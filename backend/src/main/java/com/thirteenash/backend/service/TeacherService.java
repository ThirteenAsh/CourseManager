package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Teacher;
import com.thirteenash.backend.pojo.result.PageResult;

/**
 * 教师管理服务接口
 * 定义教师的增删改查业务方法
 */
public interface TeacherService {

    /**
     * 分页查询教师
     * @param page 页码
     * @param pageSize 每页条数
     * @param keyword 搜索关键字（按工号或姓名模糊查询）
     * @param gender 性别筛选
     * @return 分页结果
     */
    PageResult page(Integer page, Integer pageSize, String keyword, String gender);

    /**
     * 新增教师
     * @param teacher 教师信息
     * @return 新增后的教师信息
     */
    Teacher add(Teacher teacher);

    /**
     * 根据ID查询教师
     * @param teacherId 教师ID
     * @return 教师信息
     */
    Teacher getById(Integer teacherId);

    /**
     * 修改教师信息
     * @param teacherId 教师ID
     * @param teacher 教师信息
     * @return 修改后的教师信息
     */
    Teacher update(Integer teacherId, Teacher teacher);

    /**
     * 根据ID删除教师
     * @param teacherId 教师ID
     */
    void deleteById(Integer teacherId);
}
