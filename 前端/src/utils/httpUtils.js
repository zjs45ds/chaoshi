/**
 * HTTP 请求工具模块
 * 统一管理API调用、错误处理、重试逻辑等
 */

import { getCSRFToken } from './security.js'
import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

// API配置
const API_CONFIG = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  maxRetries: 3,
  retryDelay: 1000
}

// 创建axios实例
const http = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: API_CONFIG.timeout,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 全局加载状态管理
let loadingInstance = null
let loadingCount = 0

// 错误消息去重管理
let lastErrorMessage = ''
let lastErrorTime = 0
const ERROR_THROTTLE_TIME = 3000 // 3秒内相同错误只显示一次

// 网络错误特殊处理 - 全局状态管理
let isShowingNetworkError = false
const NETWORK_ERROR_COOLDOWN = 5000 // 5秒内不再显示网络错误

// 显示全局加载
function showLoading(text = '加载中...') {
  if (loadingCount === 0) {
    loadingInstance = ElLoading.service({
      lock: true,
      text,
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }
  loadingCount++
}

// 隐藏全局加载
function hideLoading() {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  }
}

// 显示错误消息（带去重功能）
function showErrorMessage(message) {
  const now = Date.now()
  
  // 特殊处理网络错误 - 更严格的去重
  const isNetworkError = message.includes('网络连接失败') || 
                         message.includes('请求超时') || 
                         message.includes('连接被拒绝')
  
  if (isNetworkError) {
    if (isShowingNetworkError) {
      return
    }
    
    isShowingNetworkError = true
    
    // 设置冷却时间
    setTimeout(() => {
      isShowingNetworkError = false
    }, NETWORK_ERROR_COOLDOWN)
  } else {
    // 非网络错误使用原有去重逻辑
    if (lastErrorMessage === message && (now - lastErrorTime) < ERROR_THROTTLE_TIME) {
      return
    }
    
    // 更新最后错误信息
    lastErrorMessage = message
    lastErrorTime = now
  }
  
  // 显示错误消息
  ElMessage.error({
    message: message,
    duration: 5000,
    showClose: true
  })
}

// 请求拦截器
http.interceptors.request.use(
  (config) => {
    // 自动添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 添加CSRF Token（对于POST、PUT、DELETE请求）
    if (['post', 'put', 'delete', 'patch'].includes(config.method?.toLowerCase())) {
      config.headers['X-CSRF-TOKEN'] = getCSRFToken()
    }

    // 添加请求时间戳
    config.metadata = { startTime: new Date() }

    // 显示加载状态（默认禁用，除非明确启用）
    if (config.showLoading === true) {
      showLoading(config.loadingText)
    }
    
    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  (response) => {
    const { config } = response
    
    // 隐藏加载状态
    if (config.showLoading === true) {
      hideLoading()
    }

    // 统一处理响应数据结构
    const { code, data, message } = response.data
    
    if (code === 200 || code === 0) {
      // 成功响应，返回完整的响应数据结构给前端页面使用
      return response.data
    } else {
      // 业务错误，抛出异常让组件层处理
      const error = new Error(message || '请求失败')
      error.code = code
      error.data = data
      error.response = response // 保留原始响应信息
      return Promise.reject(error)
    }
  },
  async (error) => {
    const { config } = error
    
    // 隐藏加载状态
    if (config?.showLoading === true) {
      hideLoading()
    }

    // 检查是否需要重试（在显示错误之前）
    const willRetry = config && shouldRetry(error)
    
    // 处理不同类型的系统级错误（只在不重试或最后一次重试时显示）
    if (!willRetry) {
      if (error.response) {
        // 服务器响应错误
        const { status, data } = error.response
        
        // 只处理系统级错误，不处理业务错误
        switch (status) {
          case 401:
            // 未授权，清除token并跳转登录
            localStorage.removeItem('token')
            localStorage.removeItem('isLogin')
            showErrorMessage('登录已过期，请重新登录')
            setTimeout(() => {
              window.location.href = '/login'
            }, 1500)
            break
            
          case 403:
            showErrorMessage('没有权限访问该资源')
            break
            
          case 404:
            showErrorMessage('请求的资源不存在')
            break
            
          case 500:
          case 502:
          case 503:
          case 504:
            showErrorMessage('服务器内部错误，请稍后重试')
            break
            
          default:
            // 其他HTTP状态码错误，传递给组件层处理
            if (status >= 400 && status < 500) {
              // 4xx 客户端错误，让组件层处理
              const businessError = new Error(data?.message || `请求错误 (${status})`)
              businessError.response = error.response
              return Promise.reject(businessError)
            }
            break
        }
      } else if (error.code === 'ECONNABORTED') {
        // 请求超时
        showErrorMessage('请求超时，请检查网络连接或稍后重试')
      } else if (error.message === 'Network Error') {
        // 网络错误
        showErrorMessage('网络连接失败，请检查后端服务是否启动')
      } else if (error.code === 'ECONNREFUSED') {
        // 连接被拒绝
        showErrorMessage('连接被拒绝，后端服务未启动或端口8081被占用')
      }
    }

    // 重试逻辑
    if (willRetry) {
      return retryRequest(config)
    }

    return Promise.reject(error)
  }
)

// 判断是否应该重试
function shouldRetry(error) {
  const { config } = error
  
  // 已达到最大重试次数
  if ((config._retryCount || 0) >= API_CONFIG.maxRetries) {
    return false
  }
  
  // 只对网络错误和超时错误重试
  if (error.code === 'ECONNABORTED' || error.message === 'Network Error') {
    return true
  }
  
  // 5xx 服务器错误也可以重试
  if (error.response && error.response.status >= 500) {
    return true
  }
  
  return false
}

// 重试请求
async function retryRequest(config) {
  config._retryCount = (config._retryCount || 0) + 1
  
  // 等待重试延迟
  await new Promise(resolve => 
    setTimeout(resolve, API_CONFIG.retryDelay * config._retryCount)
  )
  
  return http.request(config)
}

// 封装常用请求方法
export const request = {
  /**
   * GET请求
   * @param {string} url - 请求地址
   * @param {Object} params - 查询参数
   * @param {Object} options - 配置选项
   */
  get(url, params = {}, options = {}) {
    return http.get(url, { params, ...options })
  },

  /**
   * POST请求
   * @param {string} url - 请求地址
   * @param {Object} data - 请求数据
   * @param {Object} options - 配置选项
   */
  post(url, data = {}, options = {}) {
    return http.post(url, data, options)
  },

  /**
   * PUT请求
   * @param {string} url - 请求地址
   * @param {Object} data - 请求数据
   * @param {Object} options - 配置选项
   */
  put(url, data = {}, options = {}) {
    return http.put(url, data, options)
  },

  /**
   * DELETE请求
   * @param {string} url - 请求地址
   * @param {Object} options - 配置选项
   */
  delete(url, options = {}) {
    return http.delete(url, options)
  },

  /**
   * 文件上传
   * @param {string} url - 上传地址
   * @param {FormData} formData - 文件数据
   * @param {Object} options - 配置选项
   */
  upload(url, formData, options = {}) {
    return http.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      ...options
    })
  },

  /**
   * 下载文件
   * @param {string} url - 下载地址
   * @param {Object} params - 查询参数
   * @param {string} filename - 文件名
   */
  async download(url, params = {}, filename = '') {
    const response = await http.get(url, {
      params,
      responseType: 'blob',
      showLoading: false  // 下载不显示加载状态
    })
    
    // 创建下载链接
    const blob = new Blob([response])
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = filename || 'download'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)
  }
}

// 请求并发控制
export class RequestQueue {
  constructor(maxConcurrent = 5) {
    this.maxConcurrent = maxConcurrent
    this.queue = []
    this.running = []
  }

  async add(requestFn) {
    return new Promise((resolve, reject) => {
      this.queue.push({ requestFn, resolve, reject })
      this.process()
    })
  }

  async process() {
    if (this.running.length >= this.maxConcurrent || this.queue.length === 0) {
      return
    }

    const { requestFn, resolve, reject } = this.queue.shift()
    const promise = requestFn()
    
    this.running.push(promise)
    
    try {
      const result = await promise
      resolve(result)
    } catch (error) {
      reject(error)
    } finally {
      this.running = this.running.filter(p => p !== promise)
      this.process()
    }
  }
}

// 缓存管理
export class ApiCache {
  constructor(maxSize = 100, ttl = 5 * 60 * 1000) { // 默认5分钟过期
    this.cache = new Map()
    this.maxSize = maxSize
    this.ttl = ttl
  }

  generateKey(url, params) {
    return `${url}?${JSON.stringify(params)}`
  }

  get(url, params) {
    const key = this.generateKey(url, params)
    const item = this.cache.get(key)
    
    if (!item) return null
    
    if (Date.now() - item.timestamp > this.ttl) {
      this.cache.delete(key)
      return null
    }
    
    return item.data
  }

  set(url, params, data) {
    const key = this.generateKey(url, params)
    
    // 如果缓存已满，删除最旧的项
    if (this.cache.size >= this.maxSize) {
      const firstKey = this.cache.keys().next().value
      this.cache.delete(firstKey)
    }
    
    this.cache.set(key, {
      data,
      timestamp: Date.now()
    })
  }

  clear() {
    this.cache.clear()
  }
}

// 创建全局缓存实例
export const apiCache = new ApiCache()

// 带缓存的请求方法
export function cachedRequest(url, params = {}, options = {}) {
  const { useCache = true, cacheTime } = options
  
  if (!useCache) {
    return request.get(url, params, options)
  }
  
  // 检查缓存
  const cached = apiCache.get(url, params)
  if (cached) {
    return Promise.resolve(cached)
  }
  
  // 发起请求并缓存结果
  return request.get(url, params, options).then(response => {
    apiCache.set(url, params, response)
    return response
  })
}

export { http }
export default request