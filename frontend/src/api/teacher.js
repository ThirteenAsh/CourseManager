import request from './request'

// 分页查询教师
export function getTeacherList(params) {
  return request.get('/teachers', { params })
}

// 新增教师
export function addTeacher(data) {
  return request.post('/teachers', data)
}

// 修改教师
export function updateTeacher(teacherId, data) {
  return request.put(`/teachers/${teacherId}`, data)
}

// 删除教师
export function deleteTeacher(teacherId) {
  return request.delete(`/teachers/${teacherId}`)
}
