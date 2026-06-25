package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Semester;
import com.thirteenash.backend.mapper.SemesterMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.SemesterVO;
import com.thirteenash.backend.service.SemesterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {

    private final SemesterMapper semesterMapper;

    public SemesterServiceImpl(SemesterMapper semesterMapper) {
        this.semesterMapper = semesterMapper;
    }

    @Override
    public PageResult page(Integer page, Integer pageSize, String schoolYear, Integer term, Integer isCurrent) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        String searchSchoolYear = trimToNull(schoolYear);
        validateTerm(term, false);
        validateIsCurrent(isCurrent, false);
        int offset = (currentPage - 1) * currentPageSize;

        Long total = semesterMapper.count(searchSchoolYear, term, isCurrent);
        List<SemesterVO> records = semesterMapper.list(searchSchoolYear, term, isCurrent, offset, currentPageSize);
        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public SemesterVO add(Semester semester) {
        validateSemester(semester);
        ensureSemesterNotExists(semester.getSchoolYear(), semester.getTerm(), null);
        if (semester.getIsCurrent() == 1) {
            semesterMapper.clearCurrent(null);
        }
        semesterMapper.insert(semester);
        return getById(semester.getSemesterId());
    }

    @Override
    public SemesterVO getById(Integer semesterId) {
        SemesterVO semester = semesterMapper.getById(semesterId);
        if (semester == null) {
            throw new IllegalArgumentException("学期不存在");
        }
        return semester;
    }

    @Override
    public SemesterVO getCurrent() {
        SemesterVO semester = semesterMapper.getCurrent();
        if (semester == null) {
            throw new IllegalArgumentException("当前学期不存在");
        }
        return semester;
    }

    @Override
    @Transactional
    public SemesterVO update(Integer semesterId, Semester semester) {
        if (semesterId == null) {
            throw new IllegalArgumentException("学期编号不能为空");
        }
        validateSemester(semester);
        ensureSemesterNotExists(semester.getSchoolYear(), semester.getTerm(), semesterId);

        semester.setSemesterId(semesterId);
        if (semester.getIsCurrent() == 1) {
            semesterMapper.clearCurrent(semesterId);
        }
        Integer rows = semesterMapper.update(semester);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("学期不存在");
        }
        return getById(semesterId);
    }

    @Override
    @Transactional
    public void deleteById(Integer semesterId) {
        if (semesterId == null) {
            throw new IllegalArgumentException("学期编号不能为空");
        }
        if (semesterMapper.getById(semesterId) == null) {
            throw new IllegalArgumentException("学期不存在");
        }
        Integer classCourseCount = semesterMapper.countClassCourseBySemesterId(semesterId);
        if (classCourseCount != null && classCourseCount > 0) {
            throw new IllegalArgumentException("该学期存在任课关系，不能删除");
        }
        semesterMapper.deleteById(semesterId);
    }

    private void validateSemester(Semester semester) {
        if (semester == null) {
            throw new IllegalArgumentException("学期信息不能为空");
        }
        String schoolYear = trimToNull(semester.getSchoolYear());
        if (schoolYear == null) {
            throw new IllegalArgumentException("学年不能为空");
        }
        validateTerm(semester.getTerm(), true);
        if (semester.getStartDate() == null) {
            throw new IllegalArgumentException("开始日期不能为空");
        }
        if (semester.getEndDate() == null) {
            throw new IllegalArgumentException("结束日期不能为空");
        }
        if (semester.getEndDate().isBefore(semester.getStartDate())) {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }
        validateIsCurrent(semester.getIsCurrent(), true);

        semester.setSchoolYear(schoolYear);
    }

    private void validateTerm(Integer term, boolean required) {
        if (term == null) {
            if (required) {
                throw new IllegalArgumentException("学期不能为空");
            }
            return;
        }
        if (term != 1 && term != 2) {
            throw new IllegalArgumentException("学期只能是1或2");
        }
    }

    private void validateIsCurrent(Integer isCurrent, boolean required) {
        if (isCurrent == null) {
            if (required) {
                throw new IllegalArgumentException("是否当前学期不能为空");
            }
            return;
        }
        if (isCurrent != 0 && isCurrent != 1) {
            throw new IllegalArgumentException("是否当前学期只能是0或1");
        }
    }

    private void ensureSemesterNotExists(String schoolYear, Integer term, Integer semesterId) {
        Integer count = semesterMapper.countBySchoolYearAndTerm(schoolYear, term, semesterId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("该学年学期已存在");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
