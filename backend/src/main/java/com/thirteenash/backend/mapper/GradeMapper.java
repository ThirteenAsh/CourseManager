package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Grade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 年级数据访问层接口
 * 对应数据库 grade 表，提供年级的 CRUD 操作
 */
public interface GradeMapper {

    /** 分页查询年级列表（支持按名称模糊查询） */
    List<Grade> list(@Param("gradeName") String gradeName,
                     @Param("offset") Integer offset,
                     @Param("pageSize") Integer pageSize);

    /** 查询年级总数（支持按名称模糊查询） */
    Long count(@Param("gradeName") String gradeName);

    /** 根据ID查询年级 */
    Grade getById(Integer gradeId);

    /** 按名称查询年级数量（排除指定ID，用于唯一性校验） */
    Integer countByName(@Param("gradeName") String gradeName, @Param("gradeId") Integer gradeId);

    /** 查询指定年级下的班级数量（用于删除前校验） */
    Integer countClassByGradeId(Integer gradeId);

    /** 新增年级，返回自增主键 */
    Integer insert(Grade grade);

    /** 修改年级信息 */
    Integer update(Grade grade);

    /** 根据ID删除年级 */
    Integer deleteById(Integer gradeId);
}
