import request from './request'

// 分页查询班级课程任课关系
export function getClassCourseTeacherList(params) {
  return request.get('/class-course-teachers', { params })
}

// 新增任课关系
export function addClassCourseTeacher(data) {
  return request.post('/class-course-teachers', data)
}

// 查询任课关系详情
export function getClassCourseTeacherDetail(cctId) {
  return request.get(`/class-course-teachers/${cctId}`)
}

// 修改任课关系
export function updateClassCourseTeacher(cctId, data) {
  return request.put(`/class-course-teachers/${cctId}`, data)
}

// 删除任课关系
export function deleteClassCourseTeacher(cctId) {
  return request.delete(`/class-course-teachers/${cctId}`)
}
