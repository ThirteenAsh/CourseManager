import request from './request'

// 分页查询学生
export function getStudentList(params) {
  return request.get('/students', { params })
}

// 新增学生
export function addStudent(data) {
  return request.post('/students', data)
}

// 修改学生
export function updateStudent(studentId, data) {
  return request.put(`/students/${studentId}`, data)
}

// 删除学生
export function deleteStudent(studentId) {
  return request.delete(`/students/${studentId}`)
}
