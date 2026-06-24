/* ============================================================
   某中学排课管理系统数据库设计
   数据库类型：MySQL 8.x
   编码：utf8mb4
   ============================================================ */

DROP DATABASE IF EXISTS school_timetable_db;

CREATE DATABASE school_timetable_db
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_general_ci;

USE school_timetable_db;


/* ============================================================
   1. 年级表 grade
   功能：管理年级信息
   ============================================================ */

CREATE TABLE grade (
    grade_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '年级编号，主键',
    grade_name VARCHAR(50) NOT NULL COMMENT '年级名称，例如高一、高二、初三',

    CONSTRAINT uk_grade_name UNIQUE (grade_name)
) COMMENT = '年级表';


/* ============================================================
   2. 教师表 teacher
   功能：管理教师基本信息
   ============================================================ */

CREATE TABLE teacher (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '教师编号，主键',
    teacher_no VARCHAR(30) NOT NULL COMMENT '教师工号',
    teacher_name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    gender CHAR(1) COMMENT '性别：男、女',

    CONSTRAINT uk_teacher_no UNIQUE (teacher_no),
    CONSTRAINT chk_teacher_gender CHECK (gender IN ('男', '女') OR gender IS NULL)
) COMMENT = '教师信息表';


/* ============================================================
   3. 班级表 school_class
   功能：管理班级信息
   说明：head_teacher_id 表示班主任
   ============================================================ */

CREATE TABLE school_class (
    class_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '班级编号，主键',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称，例如高一1班',
    grade_id INT NOT NULL COMMENT '所属年级编号',
    head_teacher_id INT COMMENT '班主任教师编号',

    CONSTRAINT uk_class_name UNIQUE (class_name),

    CONSTRAINT fk_class_grade -- 外键约束：class 表的 grade_id 引用 grade 表的 grade_id，确保班级所属的年级必须存在。
        FOREIGN KEY (grade_id)
        REFERENCES grade(grade_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_class_head_teacher -- 外键约束：class 表的 head_teacher_id 引用 teacher 表的 teacher_id，确保班主任必须是一个真实存在的教师。
        FOREIGN KEY (head_teacher_id)
        REFERENCES teacher(teacher_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) COMMENT = '班级表';


/* ============================================================
   4. 学生表 student
   功能：管理学生基本信息
   ============================================================ */

CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学生编号，主键',
    student_no VARCHAR(30) NOT NULL COMMENT '学号',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    gender CHAR(1) COMMENT '性别：男、女',
    class_id INT NOT NULL COMMENT '所属班级编号',

    CONSTRAINT uk_student_no UNIQUE (student_no),

    CONSTRAINT chk_student_gender CHECK (gender IN ('男', '女') OR gender IS NULL), -- 检查约束：对 gender 列的值进行校验。

    CONSTRAINT fk_student_class -- 外键约束：student 表的 class_id 引用 school_class 表的 class_id，确保学生所属的班级必须存在。
        FOREIGN KEY (class_id)
        REFERENCES school_class(class_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) COMMENT = '学生信息表';


/* ============================================================
   5. 课程表 course
   功能：管理课程信息，例如语文、数学、英语等
   ============================================================ */

CREATE TABLE course (
    course_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程编号，主键',
    course_code VARCHAR(30) NOT NULL COMMENT '课程代码',
    course_name VARCHAR(50) NOT NULL COMMENT '课程名称，例如语文、数学、英语',

    CONSTRAINT uk_course_code UNIQUE (course_code),
    CONSTRAINT uk_course_name UNIQUE (course_name)
) COMMENT = '课程表';


/* ============================================================
   6. 学期表 semester
   功能：管理学年和学期
   说明：term = 1 表示上学期，term = 2 表示下学期
   ============================================================ */

CREATE TABLE semester (
    semester_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学期编号，主键',
    school_year VARCHAR(20) NOT NULL COMMENT '学年，例如2025-2026',
    term TINYINT NOT NULL COMMENT '学期：1表示上学期，2表示下学期',
    start_date DATE COMMENT '学期开始日期',
    end_date DATE COMMENT '学期结束日期',
    is_current TINYINT NOT NULL DEFAULT 0 COMMENT '是否当前学期：1是，0否',

    CONSTRAINT uk_semester UNIQUE (school_year, term), -- 联合唯一约束：school_year（学年）和 term（学期）的组合必须唯一。
    CONSTRAINT chk_semester_term CHECK (term IN (1, 2)), -- 检查约束：学期只能是 1（第一学期）或 2（第二学期）
    CONSTRAINT chk_semester_current CHECK (is_current IN (0, 1)) -- 检查约束：is_current（是否当前学期）只能是 0（否）或 1（是）
) COMMENT = '学期表';


/* ============================================================
   7. 节次表 period
   功能：管理每天的节次，例如第1节、第2节
   ============================================================ */

CREATE TABLE period (
    period_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '节次编号，主键',
    period_no INT NOT NULL COMMENT '第几节课',
    start_time TIME COMMENT '上课时间',
    end_time TIME COMMENT '下课时间',
    remark VARCHAR(255) COMMENT '备注，例如上午第1节、下午第2节等',

    CONSTRAINT uk_period_no UNIQUE (period_no), -- 节次编号 period_no 不允许重复，每个节次必须有唯一的编号。
    CONSTRAINT chk_period_no CHECK (period_no > 0) -- 检查约束：节次编号必须大于 0，不接受 0 或负数，确保编号为正整数。
) COMMENT = '节次表';


/* ============================================================
   8. 班级课程任课表 class_course_teacher
   功能：
   记录某学期、某班级、某课程由哪位教师任课
   例如：
   2025-2026上学期，高一1班，数学，由张老师任课，每周5节
   ============================================================ */

CREATE TABLE class_course_teacher (
    cct_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '班级课程任课编号，主键',
    semester_id INT NOT NULL COMMENT '学期编号',
    class_id INT NOT NULL COMMENT '班级编号',
    course_id INT NOT NULL COMMENT '课程编号',
    teacher_id INT NOT NULL COMMENT '任课教师编号',
    weekly_hours INT NOT NULL COMMENT '每周课时数',

    CONSTRAINT fk_cct_semester -- 外键约束：class_course_teacher 表的 semester_id 引用 semester 表的 semester_id，确保班级课程任课关系必须关联到一个真实存在的学期。
        FOREIGN KEY (semester_id)
        REFERENCES semester(semester_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_cct_class -- 外键约束：class_course_teacher 表的 class_id 引用 school_class 表的 class_id，确保班级课程任课关系必须关联到一个真实存在的班级。
        FOREIGN KEY (class_id)
        REFERENCES school_class(class_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_cct_course -- 外键约束：class_course_teacher 表的 course_id 引用 course 表的 course_id，确保班级课程任课关系必须关联到一个真实存在的课程。
        FOREIGN KEY (course_id)
        REFERENCES course(course_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_cct_teacher -- 外键约束：class_course_teacher 表的 teacher_id 引用 teacher 表的 teacher_id，确保班级课程任课关系必须关联到一个真实存在的教师。
        FOREIGN KEY (teacher_id)
        REFERENCES teacher(teacher_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    /* 同一学期、同一班级、同一课程，只允许设置一个任课关系 */
    CONSTRAINT uk_cct UNIQUE (semester_id, class_id, course_id),

    CONSTRAINT chk_weekly_hours CHECK (weekly_hours > 0)
) COMMENT = '班级课程任课表';


/* ============================================================
   9. 排课表 timetable
   功能：
   记录具体的排课结果
   例如：
   高一1班数学，张老师，星期一第1节上课
   ============================================================ */

CREATE TABLE timetable (
    timetable_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '排课编号，主键',
    cct_id INT NOT NULL COMMENT '班级课程任课编号',
    weekday TINYINT NOT NULL COMMENT '星期几：1周一，2周二，3周三，4周四，5周五，6周六，7周日',
    period_id INT NOT NULL COMMENT '节次编号',
    classroom VARCHAR(50) COMMENT '上课教室',
    remark VARCHAR(255) COMMENT '备注',

    CONSTRAINT fk_timetable_cct -- 外键约束：timetable 表的 cct_id 引用 class_course_teacher 表的 cct_id，确保排课关系必须关联到一个真实存在的班级课程任课关系。
        FOREIGN KEY (cct_id)
        REFERENCES class_course_teacher(cct_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_timetable_period -- 外键约束：timetable 表的 period_id 引用 period 表的 period_id，确保排课关系必须关联到一个真实存在的节次。
        FOREIGN KEY (period_id)
        REFERENCES period(period_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_timetable_weekday CHECK (weekday BETWEEN 1 AND 7)
) COMMENT = '排课表';

/* ============================================================
   初始化节次数据
   可根据学校实际情况修改
   ============================================================ */

INSERT INTO period(period_no, start_time, end_time, remark) VALUES
(1, '08:00:00', '09:45:00', '上午第1节'),
(2, '09:55:00', '11:40:00', '上午第2节'),
(3, '14:00:00', '15:45:00', '下午第1节'),
(4, '15:55:00', '17:40:00', '下午第2节');


/* ============================================================
   存储过程部分
   ============================================================ */

DELIMITER $$


/* ============================================================
   存储过程 1：
   sp_check_teacher_has_class

   功能：
   检测指定教师在指定学期、指定星期、指定节次是否有课

   参数：
   p_semester_id  学期编号
   p_teacher_id   教师编号
   p_weekday      星期几：1周一，2周二，...，7周日
   p_period_id    节次编号

   返回：
   has_class = 1 表示有课
   has_class = 0 表示无课
   ============================================================ */

CREATE PROCEDURE sp_check_teacher_has_class(
    IN p_semester_id INT,
    IN p_teacher_id INT,
    IN p_weekday TINYINT,
    IN p_period_id INT
)
BEGIN
    /* 参数合法性检查 */
    IF NOT EXISTS (SELECT 1 FROM semester WHERE semester_id = p_semester_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定学期不存在';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM teacher WHERE teacher_id = p_teacher_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定教师不存在';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM period WHERE period_id = p_period_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定节次不存在';
    END IF;

    IF p_weekday < 1 OR p_weekday > 7 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '星期参数错误，weekday 必须在 1 到 7 之间';
    END IF;

    /* 如果该教师该时间有课，则返回课程明细 */
    IF EXISTS (
        SELECT 1
        FROM timetable tt
        JOIN class_course_teacher cct ON tt.cct_id = cct.cct_id
        WHERE cct.semester_id = p_semester_id
          AND cct.teacher_id = p_teacher_id
          AND tt.weekday = p_weekday
          AND tt.period_id = p_period_id
    ) THEN

        SELECT
            1 AS has_class,
            te.teacher_id,
            te.teacher_name,
            sc.class_name,
            co.course_name,
            tt.weekday,
            CASE tt.weekday
                WHEN 1 THEN '星期一'
                WHEN 2 THEN '星期二'
                WHEN 3 THEN '星期三'
                WHEN 4 THEN '星期四'
                WHEN 5 THEN '星期五'
                WHEN 6 THEN '星期六'
                WHEN 7 THEN '星期日'
            END AS weekday_name,
            p.period_no,
            p.start_time,
            p.end_time,
            tt.classroom
        FROM timetable tt
        JOIN class_course_teacher cct ON tt.cct_id = cct.cct_id
        JOIN teacher te ON cct.teacher_id = te.teacher_id
        JOIN school_class sc ON cct.class_id = sc.class_id
        JOIN course co ON cct.course_id = co.course_id
        JOIN period p ON tt.period_id = p.period_id
        WHERE cct.semester_id = p_semester_id
          AND cct.teacher_id = p_teacher_id
          AND tt.weekday = p_weekday
          AND tt.period_id = p_period_id;

    ELSE

        SELECT
            0 AS has_class,
            p_teacher_id AS teacher_id,
            NULL AS teacher_name,
            NULL AS class_name,
            NULL AS course_name,
            p_weekday AS weekday,
            CASE p_weekday
                WHEN 1 THEN '星期一'
                WHEN 2 THEN '星期二'
                WHEN 3 THEN '星期三'
                WHEN 4 THEN '星期四'
                WHEN 5 THEN '星期五'
                WHEN 6 THEN '星期六'
                WHEN 7 THEN '星期日'
            END AS weekday_name,
            NULL AS period_no,
            NULL AS start_time,
            NULL AS end_time,
            NULL AS classroom;

    END IF;
END $$


/* ============================================================
   存储过程 2：
   sp_get_class_timetable

   功能：
   生成指定班级在指定学期的课程表

   参数：
   p_semester_id  学期编号
   p_class_id     班级编号

   返回：
   星期、节次、课程名称、教师姓名、教室等信息
   ============================================================ */

CREATE PROCEDURE sp_get_class_timetable(
    IN p_semester_id INT,
    IN p_class_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM semester WHERE semester_id = p_semester_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定学期不存在';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM school_class WHERE class_id = p_class_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定班级不存在';
    END IF;

    WITH weekdays AS (
        SELECT 1 AS weekday, '星期一' AS weekday_name
        UNION ALL SELECT 2, '星期二'
        UNION ALL SELECT 3, '星期三'
        UNION ALL SELECT 4, '星期四'
        UNION ALL SELECT 5, '星期五'
        UNION ALL SELECT 6, '星期六'
        UNION ALL SELECT 7, '星期日'
    ),
    schedule_data AS (
        SELECT
            tt.weekday,
            tt.period_id,
            co.course_name,
            te.teacher_name,
            tt.classroom,
            tt.remark
        FROM timetable tt
        JOIN class_course_teacher cct ON tt.cct_id = cct.cct_id
        JOIN course co ON cct.course_id = co.course_id
        JOIN teacher te ON cct.teacher_id = te.teacher_id
        WHERE cct.semester_id = p_semester_id
          AND cct.class_id = p_class_id
    )
    SELECT
        w.weekday,
        w.weekday_name,
        p.period_no,
        p.start_time,
        p.end_time,
        IFNULL(sd.course_name, '') AS course_name,
        IFNULL(sd.teacher_name, '') AS teacher_name,
        IFNULL(sd.classroom, '') AS classroom,
        IFNULL(sd.remark, '') AS remark
    FROM weekdays w
    CROSS JOIN period p
    LEFT JOIN schedule_data sd
        ON w.weekday = sd.weekday
       AND p.period_id = sd.period_id
    ORDER BY w.weekday, p.period_no;
END $$


/* ============================================================
   存储过程 3：
   sp_get_teacher_timetable

   功能：
   生成指定教师在指定学期的课程表

   参数：
   p_semester_id  学期编号
   p_teacher_id   教师编号

   返回：
   星期、节次、班级名称、课程名称、教室等信息
   ============================================================ */

CREATE PROCEDURE sp_get_teacher_timetable(
    IN p_semester_id INT,
    IN p_teacher_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM semester WHERE semester_id = p_semester_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定学期不存在';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM teacher WHERE teacher_id = p_teacher_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定教师不存在';
    END IF;

    WITH weekdays AS (
        SELECT 1 AS weekday, '星期一' AS weekday_name
        UNION ALL SELECT 2, '星期二'
        UNION ALL SELECT 3, '星期三'
        UNION ALL SELECT 4, '星期四'
        UNION ALL SELECT 5, '星期五'
        UNION ALL SELECT 6, '星期六'
        UNION ALL SELECT 7, '星期日'
    ),
    schedule_data AS (
        SELECT
            tt.weekday,
            tt.period_id,
            sc.class_name,
            co.course_name,
            tt.classroom,
            tt.remark
        FROM timetable tt
        JOIN class_course_teacher cct ON tt.cct_id = cct.cct_id
        JOIN school_class sc ON cct.class_id = sc.class_id
        JOIN course co ON cct.course_id = co.course_id
        WHERE cct.semester_id = p_semester_id
          AND cct.teacher_id = p_teacher_id
    )
    SELECT
        w.weekday,
        w.weekday_name,
        p.period_no,
        p.start_time,
        p.end_time,
        IFNULL(sd.class_name, '') AS class_name,
        IFNULL(sd.course_name, '') AS course_name,
        IFNULL(sd.classroom, '') AS classroom,
        IFNULL(sd.remark, '') AS remark
    FROM weekdays w
    CROSS JOIN period p
    LEFT JOIN schedule_data sd
        ON w.weekday = sd.weekday
       AND p.period_id = sd.period_id
    ORDER BY w.weekday, p.period_no;
END $$


/* ============================================================
   存储过程 4：
   sp_add_timetable

   功能：
   安全添加排课记录

   作用：
   1. 检查 cct_id 是否存在
   2. 检查星期是否合法
   3. 检查节次是否存在
   4. 检查同一班级同一时间是否已有课程
   5. 检查同一教师同一时间是否已有课程
   6. 检查教室冲突
   7. 无冲突时插入排课记录

   参数：
   p_cct_id      班级课程任课编号
   p_weekday     星期几
   p_period_id   节次编号
   p_classroom   教室
   p_remark      备注
   ============================================================ */

CREATE PROCEDURE sp_add_timetable(
    IN p_cct_id INT,
    IN p_weekday TINYINT,
    IN p_period_id INT,
    IN p_classroom VARCHAR(50),
    IN p_remark VARCHAR(255)
)
BEGIN
    DECLARE v_semester_id INT;
    DECLARE v_class_id INT;
    DECLARE v_teacher_id INT;

    /* 检查星期参数 */
    IF p_weekday < 1 OR p_weekday > 7 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '星期参数错误，weekday 必须在 1 到 7 之间';
    END IF;

    /* 检查节次是否存在 */
    IF NOT EXISTS (SELECT 1 FROM period WHERE period_id = p_period_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定节次不存在';
    END IF;

    /* 根据 cct_id 查询对应的学期、班级、教师 */
    SELECT
        semester_id,
        class_id,
        teacher_id
    INTO
        v_semester_id,
        v_class_id,
        v_teacher_id
    FROM class_course_teacher
    WHERE cct_id = p_cct_id;

    /* 如果 cct_id 不存在，变量会为空 */
    IF v_semester_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '指定的班级课程任课关系不存在';
    END IF;

    /* 检查班级冲突：
       同一学期、同一班级、同一星期、同一节次只能上一门课 */
    IF EXISTS (
        SELECT 1
        FROM timetable tt
        JOIN class_course_teacher cct ON tt.cct_id = cct.cct_id
        WHERE cct.semester_id = v_semester_id
          AND cct.class_id = v_class_id
          AND tt.weekday = p_weekday
          AND tt.period_id = p_period_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '排课失败：该班级在指定星期和节次已经有课程';
    END IF;

    /* 检查教师冲突：
       同一学期、同一教师、同一星期、同一节次只能上一门课 */
    IF EXISTS (
        SELECT 1
        FROM timetable tt
        JOIN class_course_teacher cct ON tt.cct_id = cct.cct_id
        WHERE cct.semester_id = v_semester_id
          AND cct.teacher_id = v_teacher_id
          AND tt.weekday = p_weekday
          AND tt.period_id = p_period_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '排课失败：该教师在指定星期和节次已经有课程';
    END IF;

    /* 检查教室冲突：
   同一学期、同一教室、同一星期、同一节次只能安排一个班级 */
    IF p_classroom IS NOT NULL AND p_classroom != '' THEN
        IF EXISTS (
            SELECT 1
            FROM timetable tt
            JOIN class_course_teacher cct ON tt.cct_id = cct.cct_id
            WHERE cct.semester_id = v_semester_id
            AND tt.classroom = p_classroom
            AND tt.weekday = p_weekday
            AND tt.period_id = p_period_id
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '排课失败：该教室在指定星期和节次已经被占用';
        END IF;
    END IF;


    /* 无冲突，插入排课记录 */
    INSERT INTO timetable (
        cct_id,
        weekday,
        period_id,
        classroom,
        remark
    ) VALUES (
        p_cct_id,
        p_weekday,
        p_period_id,
        p_classroom,
        p_remark
    );

    SELECT
        LAST_INSERT_ID() AS timetable_id,
        '排课成功' AS message;
END $$

DELIMITER ;


/* ============================================================
   示例调用方式
   ============================================================ */

/*
-- 1. 检查某教师某节是否有课
CALL sp_check_teacher_has_class(1, 1, 1, 1);

-- 2. 生成某班级课程表
CALL sp_get_class_timetable(1, 1);

-- 3. 生成某教师课程表
CALL sp_get_teacher_timetable(1, 1);

-- 4. 添加排课记录
CALL sp_add_timetable(1, 1, 1, '教学楼A101', '正常排课');
*/