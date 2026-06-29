package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Timetable;
import com.thirteenash.backend.entity.Period;
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
import com.thirteenash.backend.pojo.vo.SemesterVO;
import com.thirteenash.backend.pojo.vo.TeacherClassCheckVO;
import com.thirteenash.backend.pojo.vo.TeacherTimetableRecordVO;
import com.thirteenash.backend.pojo.vo.TeacherTimetableVO;
import com.thirteenash.backend.pojo.vo.TimetableAddResultVO;
import com.thirteenash.backend.pojo.vo.TimetableVO;
import com.thirteenash.backend.service.TimetableService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TimetableServiceImpl implements TimetableService {

    private static final int WEEKDAY_COUNT = 7;
    private static final String[] WEEKDAY_NAMES = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

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

    @Override
    public byte[] exportClassTable(Integer semesterId, Integer classId) {
        ClassTimetableVO timetable = getClassTable(semesterId, classId);
        SemesterVO semester = semesterMapper.getById(semesterId);
        List<Period> periods = getExportPeriods(timetable.getRecords());
        Map<String, String> cellTextMap = timetable.getRecords() == null
                ? Map.of()
                : timetable.getRecords().stream()
                .filter(record -> record.getPeriodNo() != null && record.getWeekday() != null)
                .collect(Collectors.toMap(
                        record -> buildRecordKey(record.getPeriodNo(), record.getWeekday()),
                        this::buildCourseText,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
        return writeTimetableWorkbook(buildExportTitle(semester, timetable), periods, cellTextMap);
    }

    @Override
    public byte[] exportTeacherTable(Integer semesterId, Integer teacherId) {
        TeacherTimetableVO timetable = getTeacherTable(semesterId, teacherId);
        SemesterVO semester = semesterMapper.getById(semesterId);
        List<Period> periods = getTeacherExportPeriods(timetable.getRecords());
        Map<String, String> cellTextMap = timetable.getRecords() == null
                ? Map.of()
                : timetable.getRecords().stream()
                .filter(record -> record.getPeriodNo() != null && record.getWeekday() != null)
                .collect(Collectors.toMap(
                        record -> buildRecordKey(record.getPeriodNo(), record.getWeekday()),
                        this::buildTeacherCourseText,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
        return writeTimetableWorkbook(buildTeacherExportTitle(semester, timetable), periods, cellTextMap);
    }

    private byte[] writeTimetableWorkbook(String title, List<Period> periods, Map<String, String> cellTextMap) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("课程表");
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle periodStyle = createPeriodStyle(workbook);
            CellStyle contentStyle = createContentStyle(workbook);

            createTitleRow(sheet, titleStyle, title);
            createHeaderRow(sheet, headerStyle);
            createContentRows(sheet, periods, cellTextMap, periodStyle, contentStyle);
            resizeColumns(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("课程表导出失败");
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

    private List<Period> getExportPeriods(List<ClassTimetableRecordVO> records) {
        List<Period> periods = periodMapper.listAll();
        if (periods != null && !periods.isEmpty()) {
            return periods;
        }
        if (records == null) {
            return List.of();
        }
        return records.stream()
                .filter(record -> record.getPeriodNo() != null)
                .collect(Collectors.toMap(
                        ClassTimetableRecordVO::getPeriodNo,
                        record -> new Period(null, record.getPeriodNo(), record.getStartTime(), record.getEndTime(), null),
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(Period::getPeriodNo))
                .toList();
    }

    private List<Period> getTeacherExportPeriods(List<TeacherTimetableRecordVO> records) {
        List<Period> periods = periodMapper.listAll();
        if (periods != null && !periods.isEmpty()) {
            return periods;
        }
        if (records == null) {
            return List.of();
        }
        return records.stream()
                .filter(record -> record.getPeriodNo() != null)
                .collect(Collectors.toMap(
                        TeacherTimetableRecordVO::getPeriodNo,
                        record -> new Period(null, record.getPeriodNo(), record.getStartTime(), record.getEndTime(), null),
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(Period::getPeriodNo))
                .toList();
    }

    private void createTitleRow(org.apache.poi.ss.usermodel.Sheet sheet, CellStyle titleStyle, String title) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(28);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, WEEKDAY_COUNT));
    }

    private void createHeaderRow(org.apache.poi.ss.usermodel.Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(1);
        headerRow.setHeightInPoints(24);
        Cell periodHeader = headerRow.createCell(0);
        periodHeader.setCellValue("节次");
        periodHeader.setCellStyle(headerStyle);
        for (int i = 0; i < WEEKDAY_COUNT; i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(WEEKDAY_NAMES[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void createContentRows(org.apache.poi.ss.usermodel.Sheet sheet,
                                   List<Period> periods,
                                   Map<String, String> cellTextMap,
                                   CellStyle periodStyle,
                                   CellStyle contentStyle) {
        for (int rowIndex = 0; rowIndex < periods.size(); rowIndex++) {
            Period period = periods.get(rowIndex);
            Row row = sheet.createRow(rowIndex + 2);
            row.setHeightInPoints(72);

            Cell periodCell = row.createCell(0);
            periodCell.setCellValue(buildPeriodText(period));
            periodCell.setCellStyle(periodStyle);

            for (int weekday = 1; weekday <= WEEKDAY_COUNT; weekday++) {
                Cell cell = row.createCell(weekday);
                cell.setCellValue(cellTextMap.getOrDefault(buildRecordKey(period.getPeriodNo(), weekday), ""));
                cell.setCellStyle(contentStyle);
            }
        }
    }

    private void resizeColumns(org.apache.poi.ss.usermodel.Sheet sheet) {
        sheet.setColumnWidth(0, 14 * 256);
        for (int i = 1; i <= WEEKDAY_COUNT; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }
    }

    private String buildExportTitle(SemesterVO semester, ClassTimetableVO timetable) {
        String semesterText = semester == null ? "学期" : semester.getSchoolYear() + " " + nullToEmpty(semester.getTermName());
        return semesterText + " " + nullToEmpty(timetable.getClassName()) + "课程表";
    }

    private String buildTeacherExportTitle(SemesterVO semester, TeacherTimetableVO timetable) {
        String semesterText = semester == null ? "学期" : semester.getSchoolYear() + " " + nullToEmpty(semester.getTermName());
        return semesterText + " " + nullToEmpty(timetable.getTeacherName()) + "课程表";
    }

    private String buildRecordKey(Integer periodNo, Integer weekday) {
        return periodNo + "-" + weekday;
    }

    private String buildPeriodText(Period period) {
        StringBuilder builder = new StringBuilder("第").append(period.getPeriodNo()).append("节");
        String timeRange = formatTimeRange(period.getStartTime(), period.getEndTime());
        if (!timeRange.isBlank()) {
            builder.append('\n').append(timeRange);
        }
        return builder.toString();
    }

    private String buildCourseText(ClassTimetableRecordVO record) {
        if (record == null || isBlank(record.getCourseName())) {
            return "";
        }
        StringBuilder builder = new StringBuilder(record.getCourseName());
        appendLine(builder, record.getTeacherName());
        appendLine(builder, record.getClassroom());
        appendLine(builder, record.getRemark());
        return builder.toString();
    }

    private String buildTeacherCourseText(TeacherTimetableRecordVO record) {
        if (record == null || isBlank(record.getCourseName())) {
            return "";
        }
        StringBuilder builder = new StringBuilder(record.getCourseName());
        appendLine(builder, record.getClassName());
        appendLine(builder, record.getClassroom());
        appendLine(builder, record.getRemark());
        return builder.toString();
    }

    private void appendLine(StringBuilder builder, String value) {
        if (!isBlank(value)) {
            builder.append('\n').append(value.trim());
        }
    }

    private String formatTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return "";
        }
        return startTime + "-" + endTime;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = createBorderedStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createPeriodStyle(Workbook workbook) {
        CellStyle style = createBorderedStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createContentStyle(Workbook workbook) {
        CellStyle style = createBorderedStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createBorderedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private String nullToEmpty(String value) {
        return Objects.toString(value, "");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
