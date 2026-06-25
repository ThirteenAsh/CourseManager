import request from './request'

// 分页查询排课记录
export function getTimetableList(params) {
  return request.get('/timetables', { params })
}

// 新增排课记录
export function addTimetable(data) {
  return request.post('/timetables', data)
}

// 查询排课详情
export function getTimetableDetail(timetableId) {
  return request.get(`/timetables/${timetableId}`)
}

// 修改排课记录
export function updateTimetable(timetableId, data) {
  return request.put(`/timetables/${timetableId}`, data)
}

// 删除排课记录
export function deleteTimetable(timetableId) {
  return request.delete(`/timetables/${timetableId}`)
}

// 检测教师指定节次是否有课
export function checkTeacherClass(params) {
  return request.get('/timetables/check-teacher', { params })
}

// 查询班级课程表
export function getClassTimetable(params) {
  return request.get('/timetables/class-table', { params })
}

// 查询教师课程表
export function getTeacherTimetable(params) {
  return request.get('/timetables/teacher-table', { params })
}
