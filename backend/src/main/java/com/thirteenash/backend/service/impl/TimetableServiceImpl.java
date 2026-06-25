package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Timetable;
import com.thirteenash.backend.entity.Teacher;
import com.thirteenash.backend.mapper.ClassCourseTeacherMapper;
import com.thirteenash.backend.mapper.PeriodMapper;
import com.thirteenash.backend.mapper.SchoolClassMapper;
import com.thirteenash.backend.mapper.SemesterMapper;
import com.thirteenash.backend.mapper.TeacherMapper;
import com.thirteenash.backend.mapper.TimetableMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.ClassCourseTeacherVO;
import com.thirteenash.backend.pojo.vo.ClassTimetableRecordVO;
import com.thirteenash.backend.pojo.vo.ClassTimetableVO;
import com.thirteenash.backend.pojo.vo.SchoolClassVO;
import com.thirteenash.backend.pojo.vo.TeacherClassCheckVO;
import com.thirteenash.backend.pojo.vo.TeacherTimetableRecordVO;
import com.thirteenash.backend.pojo.vo.TeacherTimetableVO;
import com.thirteenash.backend.pojo.vo.TimetableAddResultVO;
import com.thirteenash.backend.pojo.vo.TimetableVO;
import com.thirteenash.backend.service.TimetableService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TimetableServiceImpl implements TimetableService {

    private final TimetableMapper timetableMapper;
    private final ClassCourseTeacherMapper classCourseTeacherMapper;
    private final SemesterMapper semesterMapper;
    private final SchoolClassMapper schoolClassMapper;
    private final TeacherMapper teacherMapper;
    private final PeriodMapper periodMapper;

    public TimetableServiceImpl(TimetableMapper timetableMapper,
                                ClassCourseTeacherMapper classCourseTeacherMapper,
                                SemesterMapper semesterMapper,
                                SchoolClassMapper schoolClassMapper,
                                TeacherMapper teacherMapper,
                                PeriodMapper periodMapper) {
        this.timetableMapper = timetableMapper;
        this.classCourseTeacherMapper = classCourseTeacherMapper;
        this.semesterMapper = semesterMapper;
        this.schoolClassMapper = schoolClassMapper;
        this.teacherMapper = teacherMapper;
        this.periodMapper = periodMapper;
    }

    @Override
    public PageResult page(Integer page, Integer pageSize, Integer semesterId, Integer classId, Integer teacherId,
                           Integer courseId, Integer weekday, Integer periodId) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        validateWeekday(weekday, false);
        int offset = (currentPage - 1) * currentPageSize;

        Long total = timetableMapper.count(semesterId, classId, teacherId, courseId, weekday, periodId);
        List<TimetableVO> records = timetableMapper.list(semesterId, classId, teacherId, courseId, weekday, periodId, offset, currentPageSize);
        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public TimetableAddResultVO add(Timetable timetable) {
        validateTimetable(timetable);
        try {
            TimetableAddResultVO result = timetableMapper.addByProcedure(timetable);
            if (result == null || result.getTimetableId() == null) {
                throw new IllegalArgumentException("排课失败");
            }
            return result;
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(extractDatabaseMessage(e, "排课失败"));
        }
    }

    @Override
    public TimetableVO getById(Integer timetableId) {
        TimetableVO timetable = timetableMapper.getById(timetableId);
        if (timetable == null) {
            throw new IllegalArgumentException("排课记录不存在");
        }
        return timetable;
    }

    @Override
    @Transactional
    public TimetableVO update(Integer timetableId, Timetable timetable) {
        if (timetableId == null) {
            throw new IllegalArgumentException("排课编号不能为空");
        }
        if (timetableMapper.getById(timetableId) == null) {
            throw new IllegalArgumentException("排课记录不存在");
        }
        ClassCourseTeacherVO classCourseTeacher = validateTimetable(timetable);
        ensureNoScheduleConflict(timetableId, classCourseTeacher, timetable);

        timetable.setTimetableId(timetableId);
        Integer rows = timetableMapper.update(timetable);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("排课记录不存在");
        }
        return getById(timetableId);
    }

    @Override
    @Transactional
    public void deleteById(Integer timetableId) {
        if (timetableId == null) {
            throw new IllegalArgumentException("排课编号不能为空");
        }
        if (timetableMapper.getById(timetableId) == null) {
            throw new IllegalArgumentException("排课记录不存在");
        }
        timetableMapper.deleteById(timetableId);
    }

    @Override
    public TeacherClassCheckVO checkTeacher(Integer semesterId, Integer teacherId, Integer weekday, Integer periodId) {
        validateSemester(semesterId);
        validateTeacher(teacherId);
        validateWeekday(weekday, true);
        validatePeriod(periodId);
        try {
            return timetableMapper.checkTeacherByProcedure(semesterId, teacherId, weekday, periodId);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(extractDatabaseMessage(e, "教师占用检测失败"));
        }
    }

    @Override
    public ClassTimetableVO getClassTable(Integer semesterId, Integer classId) {
        validateSemester(semesterId);
        SchoolClassVO schoolClass = validateSchoolClass(classId);
        try {
            List<ClassTimetableRecordVO> records = timetableMapper.listClassTableRecordsByProcedure(semesterId, classId);
            return new ClassTimetableVO(semesterId, classId, schoolClass.getClassName(), records);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(extractDatabaseMessage(e, "班级课程表查询失败"));
        }
    }

    @Override
    public TeacherTimetableVO getTeacherTable(Integer semesterId, Integer teacherId) {
        validateSemester(semesterId);
        String teacherName = validateTeacher(teacherId);
        try {
            List<TeacherTimetableRecordVO> records = timetableMapper.listTeacherTableRecordsByProcedure(semesterId, teacherId);
            return new TeacherTimetableVO(semesterId, teacherId, teacherName, records);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(extractDatabaseMessage(e, "教师课程表查询失败"));
        }
    }

    private ClassCourseTeacherVO validateTimetable(Timetable timetable) {
        if (timetable == null) {
            throw new IllegalArgumentException("排课信息不能为空");
        }
        if (timetable.getCctId() == null) {
            throw new IllegalArgumentException("任课关系编号不能为空");
        }
        ClassCourseTeacherVO classCourseTeacher = classCourseTeacherMapper.getById(timetable.getCctId());
        if (classCourseTeacher == null) {
            throw new IllegalArgumentException("任课关系不存在");
        }
        validateWeekday(timetable.getWeekday(), true);
        validatePeriod(timetable.getPeriodId());

        String classroom = trimToNull(timetable.getClassroom());
        if (classroom == null) {
            throw new IllegalArgumentException("教室不能为空");
        }
        String remark = trimToNull(timetable.getRemark());
        if (remark == null) {
            throw new IllegalArgumentException("备注不能为空");
        }
        timetable.setClassroom(classroom);
        timetable.setRemark(remark);
        return classCourseTeacher;
    }

    private void ensureNoScheduleConflict(Integer timetableId, ClassCourseTeacherVO classCourseTeacher, Timetable timetable) {
        Integer classConflictCount = timetableMapper.countClassConflict(classCourseTeacher.getSemesterId(),
                classCourseTeacher.getClassId(),
                timetable.getWeekday(),
                timetable.getPeriodId(),
                timetableId);
        if (classConflictCount != null && classConflictCount > 0) {
            throw new IllegalArgumentException("排课失败：该班级在指定星期和节次已经有课程");
        }

        Integer teacherConflictCount = timetableMapper.countTeacherConflict(classCourseTeacher.getSemesterId(),
                classCourseTeacher.getTeacherId(),
                timetable.getWeekday(),
                timetable.getPeriodId(),
                timetableId);
        if (teacherConflictCount != null && teacherConflictCount > 0) {
            throw new IllegalArgumentException("排课失败：该教师在指定星期和节次已经有课程");
        }

        Integer classroomConflictCount = timetableMapper.countClassroomConflict(classCourseTeacher.getSemesterId(),
                timetable.getClassroom(),
                timetable.getWeekday(),
                timetable.getPeriodId(),
                timetableId);
        if (classroomConflictCount != null && classroomConflictCount > 0) {
            throw new IllegalArgumentException("排课失败：该教室在指定星期和节次已经被占用");
        }
    }

    private void validateSemester(Integer semesterId) {
        if (semesterId == null) {
            throw new IllegalArgumentException("学期编号不能为空");
        }
        if (semesterMapper.getById(semesterId) == null) {
            throw new IllegalArgumentException("学期不存在");
        }
    }

    private SchoolClassVO validateSchoolClass(Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级编号不能为空");
        }
        SchoolClassVO schoolClass = schoolClassMapper.getById(classId);
        if (schoolClass == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        return schoolClass;
    }

    private String validateTeacher(Integer teacherId) {
        if (teacherId == null) {
            throw new IllegalArgumentException("教师编号不能为空");
        }
        Teacher teacher = teacherMapper.getById(teacherId);
        if (teacher == null) {
            throw new IllegalArgumentException("教师不存在");
        }
        return teacher.getTeacherName();
    }

    private void validatePeriod(Integer periodId) {
        if (periodId == null) {
            throw new IllegalArgumentException("节次编号不能为空");
        }
        if (periodMapper.getById(periodId) == null) {
            throw new IllegalArgumentException("节次不存在");
        }
    }

    private void validateWeekday(Integer weekday, boolean required) {
        if (weekday == null) {
            if (required) {
                throw new IllegalArgumentException("星期不能为空");
            }
            return;
        }
        if (weekday < 1 || weekday > 7) {
            throw new IllegalArgumentException("星期必须在1到7之间");
        }
    }

    private String extractDatabaseMessage(DataAccessException e, String defaultMessage) {
        Throwable cause = e.getMostSpecificCause();
        String message = cause == null ? e.getMessage() : cause.getMessage();
        if (message == null || message.isBlank()) {
            return defaultMessage;
        }
        return message;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
