import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 响应拦截：统一处理后端 Result 结构
request.interceptors.response.use(
  (res) => {
    if (res.config.responseType === 'blob') {
      const contentType = res.headers['content-type'] || ''
      if (contentType.includes('application/json')) {
        return res.data.text().then((text) => {
          try {
            const result = JSON.parse(text)
            return Promise.reject(new Error(result.msg || '请求失败'))
          } catch {
            return Promise.reject(new Error('请求失败'))
          }
        })
      }
      return res
    }

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
