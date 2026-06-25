import request from './request'

// 分页查询学期
export function getSemesterList(params) {
  return request.get('/semesters', { params })
}

// 查询当前学期
export function getCurrentSemester() {
  return request.get('/semesters/current')
}

// 新增学期
export function addSemester(data) {
  return request.post('/semesters', data)
}

// 修改学期
export function updateSemester(semesterId, data) {
  return request.put(`/semesters/${semesterId}`, data)
}

// 删除学期
export function deleteSemester(semesterId) {
  return request.delete(`/semesters/${semesterId}`)
}
