import request from './request'

// 分页查询年级
export function getGradeList(params) {
  return request.get('/grades', { params })
}

// 新增年级
export function addGrade(data) {
  return request.post('/grades', data)
}

// 修改年级
export function updateGrade(gradeId, data) {
  return request.put(`/grades/${gradeId}`, data)
}

// 删除年级
export function deleteGrade(gradeId) {
  return request.delete(`/grades/${gradeId}`)
}
