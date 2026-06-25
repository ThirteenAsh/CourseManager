import request from './request'

// 分页查询课程
export function getCourseList(params) {
  return request.get('/courses', { params })
}

// 新增课程
export function addCourse(data) {
  return request.post('/courses', data)
}

// 修改课程
export function updateCourse(courseId, data) {
  return request.put(`/courses/${courseId}`, data)
}

// 删除课程
export function deleteCourse(courseId) {
  return request.delete(`/courses/${courseId}`)
}
