package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Timetable;
import com.thirteenash.backend.pojo.vo.ClassTimetableRecordVO;
import com.thirteenash.backend.pojo.vo.TeacherClassCheckVO;
import com.thirteenash.backend.pojo.vo.TeacherTimetableRecordVO;
import com.thirteenash.backend.pojo.vo.TimetableAddResultVO;
import com.thirteenash.backend.pojo.vo.TimetableVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TimetableMapper {

    List<TimetableVO> list(@Param("semesterId") Integer semesterId,
                           @Param("classId") Integer classId,
                           @Param("teacherId") Integer teacherId,
                           @Param("courseId") Integer courseId,
                           @Param("weekday") Integer weekday,
                           @Param("periodId") Integer periodId,
                           @Param("offset") Integer offset,
                           @Param("pageSize") Integer pageSize);

    Long count(@Param("semesterId") Integer semesterId,
               @Param("classId") Integer classId,
               @Param("teacherId") Integer teacherId,
               @Param("courseId") Integer courseId,
               @Param("weekday") Integer weekday,
               @Param("periodId") Integer periodId);

    TimetableVO getById(Integer timetableId);

    /**
     * 调用存储过程 sp_add_timetable 新增排课记录。
     *
     * <p>入参来自 {@link Timetable}：</p>
     * <ul>
     *     <li>cctId：班级课程任课编号，对应 class_course_teacher.cct_id</li>
     *     <li>weekday：星期几，1 表示星期一，7 表示星期日</li>
     *     <li>periodId：节次编号，对应 period.period_id</li>
     *     <li>classroom：上课教室</li>
     *     <li>remark：排课备注</li>
     * </ul>
     *
     * <p>存储过程内部会校验任课关系、星期、节次是否存在或合法，并检查同一学期下的班级冲突、教师冲突和教室冲突。</p>
     *
     * @param timetable 排课请求参数
     * @return 新增结果，包含 timetableId 和 message；message 通常为“排课成功”
     */
    TimetableAddResultVO addByProcedure(Timetable timetable);

    Integer update(Timetable timetable);

    Integer deleteById(Integer timetableId);

    /**
     * 调用存储过程 sp_check_teacher_has_class 检测教师在指定时间是否有课。
     *
     * @param semesterId 学期编号，对应 semester.semester_id
     * @param teacherId 教师编号，对应 teacher.teacher_id
     * @param weekday 星期几，1 表示星期一，7 表示星期日
     * @param periodId 节次编号，对应 period.period_id
     * @return 教师占用检测结果：
     *         hasClass 为 1 时包含教师、班级、课程、星期、节次时间和教室信息；
     *         hasClass 为 0 时表示该时间无课，课程相关字段为空
     */
    TeacherClassCheckVO checkTeacherByProcedure(@Param("semesterId") Integer semesterId,
                                                @Param("teacherId") Integer teacherId,
                                                @Param("weekday") Integer weekday,
                                                @Param("periodId") Integer periodId);

    /**
     * 调用存储过程 sp_get_class_timetable 查询指定班级的完整课程表。
     *
     * @param semesterId 学期编号，对应 semester.semester_id
     * @param classId 班级编号，对应 school_class.class_id
     * @return 班级课程表明细列表。每条记录包含星期、节次、上下课时间、课程名称、教师姓名、教室和备注；
     *         没有排课的位置由存储过程返回空字符串字段
     */
    List<ClassTimetableRecordVO> listClassTableRecordsByProcedure(@Param("semesterId") Integer semesterId,
                                                                  @Param("classId") Integer classId);

    /**
     * 调用存储过程 sp_get_teacher_timetable 查询指定教师的完整课程表。
     *
     * @param semesterId 学期编号，对应 semester.semester_id
     * @param teacherId 教师编号，对应 teacher.teacher_id
     * @return 教师课程表明细列表。每条记录包含星期、节次、上下课时间、班级名称、课程名称、教室和备注；
     *         没有排课的位置由存储过程返回空字符串字段
     */
    List<TeacherTimetableRecordVO> listTeacherTableRecordsByProcedure(@Param("semesterId") Integer semesterId,
                                                                      @Param("teacherId") Integer teacherId);

    Integer countClassConflict(@Param("semesterId") Integer semesterId,
                               @Param("classId") Integer classId,
                               @Param("weekday") Integer weekday,
                               @Param("periodId") Integer periodId,
                               @Param("timetableId") Integer timetableId);

    Integer countTeacherConflict(@Param("semesterId") Integer semesterId,
                                 @Param("teacherId") Integer teacherId,
                                 @Param("weekday") Integer weekday,
                                 @Param("periodId") Integer periodId,
                                 @Param("timetableId") Integer timetableId);

    Integer countClassroomConflict(@Param("semesterId") Integer semesterId,
                                   @Param("classroom") String classroom,
                                   @Param("weekday") Integer weekday,
                                   @Param("periodId") Integer periodId,
                                   @Param("timetableId") Integer timetableId);
}
