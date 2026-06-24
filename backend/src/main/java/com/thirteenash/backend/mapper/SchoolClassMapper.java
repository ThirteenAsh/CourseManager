package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.SchoolClass;
import com.thirteenash.backend.pojo.vo.SchoolClassVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级数据访问层接口
 * 对应数据库 school_class 表，提供班级的 CRUD 操作
 * 查询结果为 SchoolClassVO，包含年级名称和班主任姓名
 */
public interface SchoolClassMapper {

    /** 分页查询班级列表（支持按班级名称、年级、班主任筛选） */
    List<SchoolClassVO> list(@Param("className") String className,
                             @Param("gradeId") Integer gradeId,
                             @Param("headTeacherId") Integer headTeacherId,
                             @Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize);

    /** 查询班级总数（支持按班级名称、年级、班主任筛选） */
    Long count(@Param("className") String className,
               @Param("gradeId") Integer gradeId,
               @Param("headTeacherId") Integer headTeacherId);

    /** 根据ID查询班级详情（含年级名称、班主任姓名） */
    SchoolClassVO getById(Integer classId);

    /** 按班级名称查询数量（排除指定ID，用于唯一性校验） */
    Integer countByClassName(@Param("className") String className, @Param("classId") Integer classId);

    /** 查询班级下的学生数量（用于删除前校验） */
    Integer countStudentByClassId(Integer classId);

    /** 查询班级的任课关系数量（用于删除前校验） */
    Integer countClassCourseByClassId(Integer classId);

    /** 新增班级，返回自增主键 */
    Integer insert(SchoolClass schoolClass);

    /** 修改班级信息 */
    Integer update(SchoolClass schoolClass);

    /** 根据ID删除班级 */
    Integer deleteById(Integer classId);
}
