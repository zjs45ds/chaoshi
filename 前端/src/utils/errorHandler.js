/**
 * 错误处理工具模块
 * 统一管理项目中的错误处理逻辑
 */

import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

// 错误类型枚举
export const ErrorTypes = {
  NETWORK: 'NETWORK_ERROR',
  API: 'API_ERROR',
  VALIDATION: 'VALIDATION_ERROR',
  AUTH: 'AUTH_ERROR',
  PERMISSION: 'PERMISSION_ERROR',
  BUSINESS: 'BUSINESS_ERROR',
  UPLOAD: 'UPLOAD_ERROR',
  UNKNOWN: 'UNKNOWN_ERROR'
}

// 错误级别
export const ErrorLevels = {
  INFO: 'info',
  WARNING: 'warning',
  ERROR: 'error',
  CRITICAL: 'critical'
}

/**
 * 自定义错误类
 */
export class AppError extends Error {
  constructor(message, type = ErrorTypes.UNKNOWN, level = ErrorLevels.ERROR, details = null) {
    super(message)
    this.name = 'AppError'
    this.type = type
    this.level = level
    this.details = details
    this.timestamp = new Date().toISOString()
    
    // 保持错误栈跟踪
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, AppError)
    }
  }

  toJSON() {
    return {
      name: this.name,
      message: this.message,
      type: this.type,
      level: this.level,
      details: this.details,
      timestamp: this.timestamp,
      stack: this.stack
    }
  }
}

/**
 * 网络错误类
 */
export class NetworkError extends AppError {
  constructor(message, status = null, url = null) {
    super(message, ErrorTypes.NETWORK, ErrorLevels.ERROR, { status, url })
    this.name = 'NetworkError'
  }
}

/**
 * API错误类
 */
export class ApiError extends AppError {
  constructor(message, code = null, data = null) {
    super(message, ErrorTypes.API, ErrorLevels.ERROR, { code, data })
    this.name = 'ApiError'
  }
}

/**
 * 验证错误类
 */
export class ValidationError extends AppError {
  constructor(message, field = null, value = null) {
    super(message, ErrorTypes.VALIDATION, ErrorLevels.WARNING, { field, value })
    this.name = 'ValidationError'
  }
}

/**
 * 认证错误类
 */
export class AuthError extends AppError {
  constructor(message) {
    super(message, ErrorTypes.AUTH, ErrorLevels.ERROR)
    this.name = 'AuthError'
  }
}

/**
 * 权限错误类
 */
export class PermissionError extends AppError {
  constructor(message, requiredPermission = null) {
    super(message, ErrorTypes.PERMISSION, ErrorLevels.ERROR, { requiredPermission })
    this.name = 'PermissionError'
  }
}

/**
 * 业务错误类
 */
export class BusinessError extends AppError {
  constructor(message, businessCode = null) {
    super(message, ErrorTypes.BUSINESS, ErrorLevels.WARNING, { businessCode })
    this.name = 'BusinessError'
  }
}

/**
 * 上传错误类
 */
export class UploadError extends AppError {
  constructor(message, file = null, reason = null) {
    super(message, ErrorTypes.UPLOAD, ErrorLevels.ERROR, { file: file?.name, reason })
    this.name = 'UploadError'
  }
}

/**
 * 错误处理器类
 */
export class ErrorHandler {
  constructor(options = {}) {
    this.options = {
      showToast: true,
      showNotification: false,
      logToConsole: true,
      reportToServer: false,
      maxRetries: 3,
      retryDelay: 1000,
      ...options
    }
    
    // 错误统计
    this.errorStats = {
      total: 0,
      byType: {},
      byLevel: {}
    }

    // 错误日志队列
    this.errorLogs = []
    this.maxLogs = 100

    // 自动设置全局错误处理
    this.setupGlobalErrorHandling()
  }

  /**
   * 处理错误
   * @param {Error|AppError} error - 错误对象
   * @param {Object} context - 上下文信息
   */
  handle(error, context = {}) {
    // 标准化错误对象
    const standardError = this.normalizeError(error)
    
    // 记录错误统计
    this.recordError(standardError)
    
    // 记录错误日志
    this.logError(standardError, context)
    
    // 显示用户提示
    if (this.options.showToast) {
      this.showErrorToast(standardError)
    }
    
    if (this.options.showNotification) {
      this.showErrorNotification(standardError)
    }
    
    // 上报错误到服务器
    if (this.options.reportToServer) {
      this.reportError(standardError, context)
    }
    
    return standardError
  }

  /**
   * 标准化错误对象
   */
  normalizeError(error) {
    if (error instanceof AppError) {
      return error
    }
    
    if (error instanceof TypeError) {
      return new AppError(error.message, ErrorTypes.UNKNOWN, ErrorLevels.ERROR)
    }
    
    if (error instanceof SyntaxError) {
      return new AppError('语法错误', ErrorTypes.UNKNOWN, ErrorLevels.ERROR)
    }
    
    if (typeof error === 'string') {
      return new AppError(error, ErrorTypes.UNKNOWN, ErrorLevels.ERROR)
    }
    
    return new AppError(
      error?.message || '未知错误',
      ErrorTypes.UNKNOWN,
      ErrorLevels.ERROR,
      error
    )
  }

  /**
   * 记录错误统计
   */
  recordError(error) {
    this.errorStats.total++
    this.errorStats.byType[error.type] = (this.errorStats.byType[error.type] || 0) + 1
    this.errorStats.byLevel[error.level] = (this.errorStats.byLevel[error.level] || 0) + 1
  }

  /**
   * 记录错误日志
   */
  logError(error, context) {
    const logEntry = {
      ...error.toJSON(),
      context,
      userAgent: navigator.userAgent,
      url: window.location.href,
      userId: localStorage.getItem('userId') || 'anonymous'
    }
    
    // 添加到日志队列
    this.errorLogs.unshift(logEntry)
    
    // 限制日志数量
    if (this.errorLogs.length > this.maxLogs) {
      this.errorLogs.pop()
    }
    
    // 控制台输出
    if (this.options.logToConsole) {
      console.group(`🚨 ${error.level.toUpperCase()}: ${error.type}`)
      console.error(error.message)
      console.error('Details:', error.details)
      console.error('Context:', context)
      console.error('Stack:', error.stack)
      console.groupEnd()
    }
  }

  /**
   * 显示错误提示
   */
  showErrorToast(error) {
    const messageConfig = {
      message: this.getDisplayMessage(error),
      duration: this.getDisplayDuration(error.level),
      showClose: true
    }

    switch (error.level) {
      case ErrorLevels.INFO:
        ElMessage.info(messageConfig)
        break
      case ErrorLevels.WARNING:
        ElMessage.warning(messageConfig)
        break
      case ErrorLevels.ERROR:
      case ErrorLevels.CRITICAL:
        ElMessage.error(messageConfig)
        break
      default:
        ElMessage(messageConfig)
    }
  }

  /**
   * 显示错误通知
   */
  showErrorNotification(error) {
    if (error.level === ErrorLevels.CRITICAL) {
      ElNotification({
        title: '严重错误',
        message: this.getDisplayMessage(error),
        type: 'error',
        duration: 0, // 不自动关闭
        showClose: true
      })
    }
  }

  /**
   * 获取显示消息
   */
  getDisplayMessage(error) {
    // 根据错误类型返回用户友好的消息
    const userMessages = {
      [ErrorTypes.NETWORK]: '网络连接失败，请检查网络设置',
      [ErrorTypes.API]: error.message || 'API请求失败',
      [ErrorTypes.VALIDATION]: error.message || '输入数据格式不正确',
      [ErrorTypes.AUTH]: '登录已过期，请重新登录',
      [ErrorTypes.PERMISSION]: '您没有权限执行此操作',
      [ErrorTypes.BUSINESS]: error.message || '操作失败',
      [ErrorTypes.UPLOAD]: error.message || '文件上传失败',
      [ErrorTypes.UNKNOWN]: error.message || '发生未知错误'
    }

    return userMessages[error.type] || error.message
  }

  /**
   * 获取显示时长
   */
  getDisplayDuration(level) {
    const durations = {
      [ErrorLevels.INFO]: 3000,
      [ErrorLevels.WARNING]: 4000,
      [ErrorLevels.ERROR]: 5000,
      [ErrorLevels.CRITICAL]: 8000
    }
    return durations[level] || 4000
  }

  /**
   * 上报错误到服务器
   */
  async reportError(error, context) {
    try {
      // 这里可以调用错误上报API
      // await api.post('/errors/report', { error: error.toJSON(), context })
      console.log('Error reported to server:', error.toJSON())
    } catch (reportError) {
      console.warn('Failed to report error to server:', reportError)
    }
  }

  /**
   * 设置全局错误处理
   */
  setupGlobalErrorHandling() {
    // 全局JavaScript错误
    window.addEventListener('error', (event) => {
      const error = new AppError(
        event.message,
        ErrorTypes.UNKNOWN,
        ErrorLevels.ERROR,
        {
          filename: event.filename,
          lineno: event.lineno,
          colno: event.colno
        }
      )
      this.handle(error, { source: 'global-error' })
    })

    // 全局Promise拒绝
    window.addEventListener('unhandledrejection', (event) => {
      const error = new AppError(
        event.reason?.message || 'Unhandled Promise Rejection',
        ErrorTypes.UNKNOWN,
        ErrorLevels.ERROR,
        event.reason
      )
      this.handle(error, { source: 'unhandled-rejection' })
      event.preventDefault() // 阻止默认的控制台错误输出
    })

    // Vue错误处理将在main.js中设置
  }

  /**
   * 获取错误统计
   */
  getStats() {
    return { ...this.errorStats }
  }

  /**
   * 获取错误日志
   */
  getLogs(count = 10) {
    return this.errorLogs.slice(0, count)
  }

  /**
   * 清除错误日志
   */
  clearLogs() {
    this.errorLogs = []
    this.errorStats = {
      total: 0,
      byType: {},
      byLevel: {}
    }
  }
}

// 创建全局错误处理器实例
export const errorHandler = new ErrorHandler({
  showToast: true,
  showNotification: true,
  logToConsole: process.env.NODE_ENV === 'development',
  reportToServer: process.env.NODE_ENV === 'production'
})

/**
 * 便捷的错误处理函数
 */
export function handleError(error, context = {}) {
  return errorHandler.handle(error, context)
}

/**
 * 创建错误处理装饰器（用于异步函数）
 */
export function withErrorHandling(fn, context = {}) {
  return async function(...args) {
    try {
      return await fn.apply(this, args)
    } catch (error) {
      handleError(error, { ...context, function: fn.name })
      throw error // 重新抛出错误，让调用方处理
    }
  }
}

/**
 * 安全执行函数（捕获错误但不重新抛出）
 */
export async function safeExecute(fn, defaultValue = null, context = {}) {
  try {
    return await fn()
  } catch (error) {
    handleError(error, { ...context, function: fn.name })
    return defaultValue
  }
}

/**
 * 重试机制
 */
export async function retryWithErrorHandling(fn, maxRetries = 3, delay = 1000, context = {}) {
  let lastError
  
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      return await fn()
    } catch (error) {
      lastError = error
      
      if (attempt === maxRetries) {
        handleError(error, { ...context, attempt, maxRetries })
        throw error
      }
      
      // 等待后重试
      await new Promise(resolve => setTimeout(resolve, delay * attempt))
    }
  }
}

// 导出所有错误类和工具函数
export default {
  ErrorHandler,
  errorHandler,
  handleError,
  withErrorHandling,
  safeExecute,
  retryWithErrorHandling,
  ErrorTypes,
  ErrorLevels,
  AppError,
  NetworkError,
  ApiError,
  ValidationError,
  AuthError,
  PermissionError,
  BusinessError,
  UploadError
}