import request from './request'

// 分页查询班级
export function getClassList(params) {
  return request.get('/classes', { params })
}

// 新增班级
export function addClass(data) {
  return request.post('/classes', data)
}

// 修改班级
export function updateClass(classId, data) {
  return request.put(`/classes/${classId}`, data)
}

// 删除班级
export function deleteClass(classId) {
  return request.delete(`/classes/${classId}`)
}
