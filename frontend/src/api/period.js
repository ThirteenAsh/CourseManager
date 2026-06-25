import request from './request'

// 查询全部节次
export function getPeriodList() {
  return request.get('/periods')
}

// 新增节次
export function addPeriod(data) {
  return request.post('/periods', data)
}

// 修改节次
export function updatePeriod(periodId, data) {
  return request.put(`/periods/${periodId}`, data)
}

// 删除节次
export function deletePeriod(periodId) {
  return request.delete(`/periods/${periodId}`)
}
