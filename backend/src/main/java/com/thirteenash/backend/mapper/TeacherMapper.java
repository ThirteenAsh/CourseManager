package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教师数据访问层接口
 * 对应数据库 teacher 表，提供教师的 CRUD 操作
 */
public interface TeacherMapper {

    /** 分页查询教师列表（支持按关键字模糊查询和性别筛选） */
    List<Teacher> list(@Param("keyword") String keyword,
                       @Param("gender") String gender,
                       @Param("offset") Integer offset,
                       @Param("pageSize") Integer pageSize);

    /** 查询教师总数（支持按关键字模糊查询和性别筛选） */
    Long count(@Param("keyword") String keyword, @Param("gender") String gender);

    /** 根据ID查询教师 */
    Teacher getById(Integer teacherId);

    /** 按工号查询教师数量（排除指定ID，用于唯一性校验） */
    Integer countByTeacherNo(@Param("teacherNo") String teacherNo, @Param("teacherId") Integer teacherId);

    /** 查询教师的任课关系数量（用于删除前校验） */
    Integer countClassCourseByTeacherId(Integer teacherId);

    /** 新增教师，返回自增主键 */
    Integer insert(Teacher teacher);

    /** 修改教师信息 */
    Integer update(Teacher teacher);

    /** 根据ID删除教师 */
    Integer deleteById(Integer teacherId);
}
