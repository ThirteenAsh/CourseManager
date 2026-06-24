import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 响应拦截：统一处理后端 Result 结构
request.interceptors.response.use(
  (res) => {
    const { code, msg, data } = res.data
    if (code === 1) {
      return data
    }
    return Promise.reject(new Error(msg || '请求失败'))
  },
  (err) => {
    return Promise.reject(err)
  }
)

export default request
