# 中学排课管理系统

中学排课管理系统，用于管理学校的基础数据（年级、班级、课程、教师、学生），并通过存储过程自动生成课表。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.5 + MyBatis + MySQL + Lombok |
| 前端 | Vue 3 (Composition API) + Vite + Axios |
| Java 版本 | 17 |
| Node 版本 | >= 22.18.0 或 >= 24.12.0 |

## 项目结构

```
CourseManager/
├── backend/                           # 后端 Spring Boot 项目
│   ├── pom.xml                        # Maven 配置
│   └── src/main/
│       ├── java/com/thirteenash/backend/
│       │   ├── BackendApplication.java      # 启动类
│       │   ├── controller/
│       │   │   └── GradeController.java     # 年级管理接口
│       │   ├── entity/                      # 实体类（对应数据库表）
│       │   │   ├── ClassCourseTeacher.java  # 任课安排
│       │   │   ├── Course.java              # 课程
│       │   │   ├── Grade.java               # 年级
│       │   │   ├── Period.java              # 节次
│       │   │   ├── SchoolClass.java         # 班级
│       │   │   ├── Semester.java            # 学期
│       │   │   ├── Student.java             # 学生
│       │   │   ├── Teacher.java             # 教师
│       │   │   └── Timetable.java           # 课表
│       │   ├── mapper/
│       │   │   └── GradeMapper.java         # MyBatis Mapper 接口
│       │   ├── service/
│       │   │   ├── GradeService.java        # Service 接口
│       │   │   └── impl/
│       │   │       └── GradeServiceImpl.java
│       │   ├── pojo/result/
│       │   │   ├── Result.java              # 统一响应封装
│       │   │   └── PageResult.java          # 分页响应封装
│       │   └── exception/
│       │       └── GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yaml             # 应用配置
│           └── mapper/
│               └── GradeMapper.xml          # MyBatis SQL 映射
│
├── frontend/                          # 前端 Vue 项目
│   ├── package.json                   # npm 配置
│   ├── vite.config.js                 # Vite 配置（含 /api 代理）
│   └── src/
│       ├── main.js                    # Vue 应用入口
│       ├── App.vue                    # 主布局（侧边栏 + 页面切换）
│       ├── api/
│       │   ├── request.js             # Axios 实例（baseURL: /api）
│       │   └── grade.js               # 年级 API 封装
│       ├── assets/
│       │   └── main.css               # 全局样式（CSS 变量 + 响应式）
│       └── views/
│           ├── DashboardView.vue      # 首页仪表盘
│           ├── GradeView.vue          # 年级管理（已完成）
│           ├── ClassesView.vue        # 班级管理（UI 骨架）
│           ├── CoursesView.vue        # 课程管理（UI 骨架）
│           ├── StudentsView.vue       # 学生管理（UI 骨架）
│           ├── TeachersView.vue       # 教师管理（UI 骨架）
│           ├── ClassCoursesView.vue   # 任课安排（UI 骨架）
│           └── ProcedureToolsView.vue # 排课工具（UI 骨架）
│
└── doc/
    └── 接口文档.md                    # API 接口文档
```

## 数据库设计

数据库名：`school_timetable_db`，MySQL，字符集 UTF-8。

### ER 关系

```
需完善
```

### 表结构

**grade** - 年级

| 字段 | 类型 | 说明 |
|------|------|------|
| grade_id | INT, PK, 自增 | 年级 ID |
| grade_name | VARCHAR | 年级名称，如"高一""初三" |

**school_class** - 班级

| 字段 | 类型 | 说明 |
|------|------|------|
| class_id | INT, PK | 班级 ID |
| class_name | VARCHAR | 班级名称，如"高一1班" |
| grade_id | INT, FK → grade | 所属年级 |
| head_teacher_id | INT, FK → teacher | 班主任 |

**student** - 学生

| 字段 | 类型 | 说明 |
|------|------|------|
| student_id | INT, PK | 学生 ID |
| student_no | VARCHAR | 学号 |
| student_name | VARCHAR | 姓名 |
| gender | VARCHAR | 性别："男"/"女" |
| class_id | INT, FK → school_class | 所属班级 |

**teacher** - 教师

| 字段 | 类型 | 说明 |
|------|------|------|
| teacher_id | INT, PK | 教师 ID |
| teacher_no | VARCHAR | 工号 |
| teacher_name | VARCHAR | 姓名 |
| gender | VARCHAR | 性别："男"/"女" |

**course** - 课程

| 字段 | 类型 | 说明 |
|------|------|------|
| course_id | INT, PK | 课程 ID |
| course_code | VARCHAR | 课程代码，如 "MATH" |
| course_name | VARCHAR | 课程名称，如"数学" |

**semester** - 学期

| 字段 | 类型 | 说明 |
|------|------|------|
| semester_id | INT, PK | 学期 ID |
| school_year | VARCHAR | 学年，如 "2025-2026" |
| term | INT | 1=上学期，2=下学期 |
| start_date | DATE | 开始日期 |
| end_date | DATE | 结束日期 |
| is_current | INT | 1=当前学期，0=非当前 |

**period** - 节次

| 字段 | 类型 | 说明 |
|------|------|------|
| period_id | INT, PK | 节次 ID |
| period_no | INT | 第几节课 |
| start_time | TIME | 开始时间 |
| end_time | TIME | 结束时间 |
| remark | VARCHAR | 备注，如"上午第1节" |

**class_course_teacher** - 任课安排

| 字段 | 类型 | 说明 |
|------|------|------|
| cct_id | INT, PK | 安排 ID |
| semester_id | INT, FK → semester | 学期 |
| class_id | INT, FK → school_class | 班级 |
| course_id | INT, FK → course | 课程 |
| teacher_id | INT, FK → teacher | 授课教师 |
| weekly_hours | INT | 每周课时数 |

**timetable** - 课表

| 字段 | 类型 | 说明 |
|------|------|------|
| timetable_id | INT, PK | 课表条目 ID |
| cct_id | INT, FK → class_course_teacher | 关联任课安排 |
| weekday | INT | 星期几（1=周一 ... 7=周日） |
| period_id | INT, FK → period | 节次 |
| classroom | VARCHAR | 教室 |
| remark | VARCHAR | 备注 |

## 环境搭建与运行

### 前置条件

- JDK 17
- Maven 3.x
- MySQL 8.x（运行在 localhost:3306）
- Node.js >= 22.18.0 或 >= 24.12.0

### 1. 创建数据库

```sql
CREATE DATABASE school_timetable_db DEFAULT CHARACTER SET utf8mb4;
```

然后根据上方表结构建表（或使用项目中的 SQL 脚本，如有）。

MySQL Workbench 连接配置：

| 参数 | 值 |
|------|------|
| Connection Name | CourseManager |
| Hostname | 127.0.0.1 |
| Port | 3306 |
| Username | root |
| Password | 123456 |
| Default Schema | school_timetable_db |

### 2. 修改数据库配置

编辑 `backend/src/main/resources/application.yaml`，确认数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/school_timetable_db?...
    username: root
    password: 123456
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，已配置代理将 `/api` 请求转发到后端 8080 端口。

## 接口规范

所有接口返回统一格式：

```json
{
  "code": 1,
  "msg": "success",
  "data": { ... }
}
```

- `code == 1` 表示成功
- `code == 0` 表示失败，`msg` 中包含错误信息

分页接口额外返回：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 100,
    "records": [ ... ]
  }
}
```

分页参数：`page`（页码，从1开始）、`pageSize`（每页条数）。

详细接口文档见 `doc/接口文档.md`。

**后端**

1. `entity/` - 实体类已定义好，无需新建
2. `mapper/` - 新建 Mapper 接口 + 对应的 XML（`resources/mapper/xxxMapper.xml`）
3. `service/` - 新建 Service 接口 + `impl/` 下的实现类
4. `controller/` - 新建 Controller，使用 `Result` 封装返回值

**前端**

1. `api/` - 新建请求模块，封装 CRUD 接口调用
2. `views/` - 补全页面逻辑：分页加载、搜索、新增/编辑弹窗、删除确认

## 业务流程

```
录入基础数据 → 安排任课教师 → 调用存储过程排课 → 查看课表
（年级/班级/     （班级-课程-     （自动生成课表     （按班级或
 课程/教师/       教师绑定）       条目）            教师查看）
 学生）
```
