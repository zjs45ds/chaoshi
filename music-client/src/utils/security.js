/**
 * 安全工具模块
 * 提供XSS、CSRF防护和其他安全功能
 */

// ====== XSS 防护 ======

/**
 * HTML实体编码映射
 */
const HTML_ENTITIES = {
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;',
  "'": '&#x27;',
  '/': '&#x2F;',
  '`': '&#96;',
  '=': '&#x3D;'
}

/**
 * 转义HTML特殊字符，防止XSS攻击
 * @param {string} str - 需要转义的字符串
 * @returns {string} 转义后的字符串
 */
export function escapeHtml(str) {
  if (typeof str !== 'string') {
    return str
  }
  
  return str.replace(/[&<>"'`=\/]/g, (match) => HTML_ENTITIES[match])
}

/**
 * 反转义HTML实体
 * @param {string} str - 需要反转义的字符串
 * @returns {string} 反转义后的字符串
 */
export function unescapeHtml(str) {
  if (typeof str !== 'string') {
    return str
  }
  
  const reverseEntities = Object.fromEntries(
    Object.entries(HTML_ENTITIES).map(([key, value]) => [value, key])
  )
  
  return str.replace(/&amp;|&lt;|&gt;|&quot;|&#x27;|&#x2F;|&#96;|&#x3D;/g, (match) => {
    return reverseEntities[match] || match
  })
}

/**
 * 清理和过滤HTML内容
 * @param {string} html - HTML字符串
 * @param {Object} options - 配置选项
 * @returns {string} 清理后的HTML
 */
export function sanitizeHtml(html, options = {}) {
  if (typeof html !== 'string') {
    return ''
  }
  
  const config = {
    allowedTags: ['p', 'br', 'strong', 'em', 'u', 'i', 'span', 'div'],
    allowedAttributes: ['class', 'style'],
    removeScripts: true,
    removeEvents: true,
    ...options
  }
  
  let cleanHtml = html
  
  // 移除脚本标签
  if (config.removeScripts) {
    cleanHtml = cleanHtml.replace(/<script[^>]*>.*?<\/script>/gis, '')
    cleanHtml = cleanHtml.replace(/<script[^>]*>/gi, '')
  }
  
  // 移除事件处理器
  if (config.removeEvents) {
    cleanHtml = cleanHtml.replace(/\son\w+\s*=\s*["'][^"']*["']/gis, '')
    cleanHtml = cleanHtml.replace(/\son\w+\s*=\s*[^\s>]+/gis, '')
  }
  
  // 移除javascript: 协议
  cleanHtml = cleanHtml.replace(/javascript:/gis, '')
  
  // 移除data: 协议中的脚本
  cleanHtml = cleanHtml.replace(/data:[^;]*;base64[^"']*/gis, '')
  
  return cleanHtml
}

/**
 * 验证URL是否安全
 * @param {string} url - 需要验证的URL
 * @returns {boolean} 是否安全
 */
export function isUrlSafe(url) {
  if (typeof url !== 'string') {
    return false
  }
  
  // 检查是否为相对路径
  if (url.startsWith('/') && !url.startsWith('//')) {
    return true
  }
  
  // 检查协议是否安全
  const allowedProtocols = ['http:', 'https:', 'mailto:', 'tel:']
  
  try {
    const urlObj = new URL(url)
    return allowedProtocols.includes(urlObj.protocol)
  } catch (error) {
    return false
  }
}

/**
 * 清理URL，移除潜在的恶意参数
 * @param {string} url - 需要清理的URL
 * @returns {string} 清理后的URL
 */
export function sanitizeUrl(url) {
  if (!isUrlSafe(url)) {
    return '#'
  }
  
  // 移除javascript:, data:, vbscript: 等危险协议
  const cleanUrl = url.replace(/^(javascript|data|vbscript):/i, '')
  
  return cleanUrl
}

// ====== CSRF 防护 ======

/**
 * 生成CSRF Token
 * @returns {string} CSRF Token
 */
export function generateCSRFToken() {
  const array = new Uint8Array(32)
  crypto.getRandomValues(array)
  return Array.from(array, byte => byte.toString(16).padStart(2, '0')).join('')
}

/**
 * 获取或创建CSRF Token
 * @returns {string} CSRF Token
 */
export function getCSRFToken() {
  let token = sessionStorage.getItem('csrf_token')
  
  if (!token) {
    token = generateCSRFToken()
    sessionStorage.setItem('csrf_token', token)
  }
  
  return token
}

/**
 * 验证CSRF Token
 * @param {string} token - 需要验证的token
 * @returns {boolean} 是否有效
 */
export function validateCSRFToken(token) {
  const storedToken = sessionStorage.getItem('csrf_token')
  return storedToken && storedToken === token
}

/**
 * 为表单添加CSRF Token
 * @param {HTMLFormElement} form - 表单元素
 */
export function addCSRFTokenToForm(form) {
  if (!(form instanceof HTMLFormElement)) {
    return
  }
  
  // 检查是否已存在CSRF token字段
  let tokenInput = form.querySelector('input[name="_token"]')
  
  if (!tokenInput) {
    tokenInput = document.createElement('input')
    tokenInput.type = 'hidden'
    tokenInput.name = '_token'
    form.appendChild(tokenInput)
  }
  
  tokenInput.value = getCSRFToken()
}

// ====== 输入验证和过滤 ======

/**
 * 验证用户名是否安全
 * @param {string} username - 用户名
 * @returns {Object} 验证结果
 */
export function validateUsername(username) {
  const result = {
    isValid: true,
    errors: []
  }
  
  if (!username || typeof username !== 'string') {
    result.isValid = false
    result.errors.push('用户名不能为空')
    return result
  }
  
  // 长度检查
  if (username.length < 3 || username.length > 20) {
    result.isValid = false
    result.errors.push('用户名长度必须在3-20个字符之间')
  }
  
  // 字符检查（只允许字母、数字、下划线、中文）
  if (!/^[\w\u4e00-\u9fa5]+$/.test(username)) {
    result.isValid = false
    result.errors.push('用户名只能包含字母、数字、下划线和中文字符')
  }
  
  // 检查是否包含敏感词汇
  const forbiddenWords = ['admin', 'root', 'administrator', 'system', 'null', 'undefined']
  if (forbiddenWords.some(word => username.toLowerCase().includes(word))) {
    result.isValid = false
    result.errors.push('用户名包含禁用词汇')
  }
  
  return result
}

/**
 * 验证密码强度
 * @param {string} password - 密码
 * @returns {Object} 验证结果
 */
export function validatePassword(password) {
  const result = {
    isValid: true,
    errors: [],
    strength: 0,
    strengthText: '弱'
  }
  
  if (!password || typeof password !== 'string') {
    result.isValid = false
    result.errors.push('密码不能为空')
    return result
  }
  
  // 长度检查
  if (password.length < 8) {
    result.isValid = false
    result.errors.push('密码长度至少8个字符')
  } else {
    result.strength += 1
  }
  
  // 复杂度检查
  if (/[a-z]/.test(password)) result.strength += 1
  if (/[A-Z]/.test(password)) result.strength += 1
  if (/\d/.test(password)) result.strength += 1
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) result.strength += 1
  
  // 设置强度文本
  if (result.strength >= 4) {
    result.strengthText = '强'
  } else if (result.strength >= 3) {
    result.strengthText = '中'
  }
  
  // 检查常见弱密码
  const weakPasswords = ['12345678', 'password', '11111111', 'qwertyui']
  if (weakPasswords.includes(password.toLowerCase())) {
    result.isValid = false
    result.errors.push('密码过于简单，请使用更复杂的密码')
  }
  
  return result
}

/**
 * 验证邮箱格式
 * @param {string} email - 邮箱地址
 * @returns {Object} 验证结果
 */
export function validateEmail(email) {
  const result = {
    isValid: true,
    errors: []
  }
  
  if (!email || typeof email !== 'string') {
    result.isValid = false
    result.errors.push('邮箱不能为空')
    return result
  }
  
  // 基本格式检查
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email)) {
    result.isValid = false
    result.errors.push('邮箱格式不正确')
  }
  
  // 长度检查
  if (email.length > 254) {
    result.isValid = false
    result.errors.push('邮箱地址过长')
  }
  
  return result
}

// ====== 内容安全策略 (CSP) ======

/**
 * 设置内容安全策略
 */
export function setContentSecurityPolicy() {
  const meta = document.createElement('meta')
  meta.httpEquiv = 'Content-Security-Policy'
  meta.content = [
    "default-src 'self'",
    "script-src 'self' 'unsafe-inline' 'unsafe-eval'",
    "style-src 'self' 'unsafe-inline'",
    "img-src 'self' data: https: blob:",
    "font-src 'self' data:",
    "connect-src 'self' ws: wss:",
    "media-src 'self' blob:",
    "object-src 'none'",
    "frame-src 'none'",
    "base-uri 'self'",
    "form-action 'self'"
  ].join('; ')
  
  document.head.appendChild(meta)
}

// ====== 敏感信息过滤 ======

/**
 * 过滤敏感信息
 * @param {string} text - 需要过滤的文本
 * @returns {string} 过滤后的文本
 */
export function filterSensitiveInfo(text) {
  if (typeof text !== 'string') {
    return text
  }
  
  let filteredText = text
  
  // 过滤手机号
  filteredText = filteredText.replace(/1[3-9]\d{9}/g, (match) => {
    return match.substring(0, 3) + '****' + match.substring(7)
  })
  
  // 过滤邮箱
  filteredText = filteredText.replace(/[\w.-]+@[\w.-]+\.\w+/g, (match) => {
    const [local, domain] = match.split('@')
    const maskedLocal = local.length > 2 
      ? local.substring(0, 2) + '***'
      : local
    return maskedLocal + '@' + domain
  })
  
  // 过滤身份证号
  filteredText = filteredText.replace(/\d{17}[\dXx]/g, (match) => {
    return match.substring(0, 6) + '********' + match.substring(14)
  })
  
  return filteredText
}

// ====== 安全随机数生成 ======

/**
 * 生成安全的随机字符串
 * @param {number} length - 字符串长度
 * @param {string} charset - 字符集
 * @returns {string} 随机字符串
 */
export function generateSecureRandom(length = 32, charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789') {
  const array = new Uint8Array(length)
  crypto.getRandomValues(array)
  
  return Array.from(array, byte => charset[byte % charset.length]).join('')
}

/**
 * 生成UUID v4
 * @returns {string} UUID
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

// ====== 安全存储 ======

/**
 * 安全地存储敏感数据到localStorage
 * @param {string} key - 存储键
 * @param {any} data - 要存储的数据
 * @param {string} secret - 加密密钥（可选）
 */
export function secureStore(key, data, secret) {
  try {
    let dataToStore = JSON.stringify(data)
    
    // 如果提供了密钥，进行简单的异或加密
    if (secret) {
      dataToStore = simpleEncrypt(dataToStore, secret)
    }
    
    localStorage.setItem(key, dataToStore)
    return true
  } catch (error) {
    // CONSOLE LOG REMOVED: console.warn('Secure store failed:', error)
    return false
  }
}

/**
 * 安全地从localStorage获取数据
 * @param {string} key - 存储键
 * @param {string} secret - 解密密钥（可选）
 * @returns {any} 解密后的数据
 */
export function secureRetrieve(key, secret) {
  try {
    let storedData = localStorage.getItem(key)
    
    if (!storedData) {
      return null
    }
    
    // 如果提供了密钥，进行解密
    if (secret) {
      storedData = simpleDecrypt(storedData, secret)
    }
    
    return JSON.parse(storedData)
  } catch (error) {
    // CONSOLE LOG REMOVED: console.warn('Secure retrieve failed:', error)
    return null
  }
}

/**
 * 简单的异或加密
 * @param {string} text - 要加密的文本
 * @param {string} key - 加密密钥
 * @returns {string} 加密后的文本
 */
function simpleEncrypt(text, key) {
  let result = ''
  for (let i = 0; i < text.length; i++) {
    result += String.fromCharCode(text.charCodeAt(i) ^ key.charCodeAt(i % key.length))
  }
  return btoa(result)
}

/**
 * 简单的异或解密
 * @param {string} encryptedText - 加密的文本
 * @param {string} key - 解密密钥
 * @returns {string} 解密后的文本
 */
function simpleDecrypt(encryptedText, key) {
  const text = atob(encryptedText)
  let result = ''
  for (let i = 0; i < text.length; i++) {
    result += String.fromCharCode(text.charCodeAt(i) ^ key.charCodeAt(i % key.length))
  }
  return result
}

// ====== 导出所有函数 ======
export default {
  // XSS 防护
  escapeHtml,
  unescapeHtml,
  sanitizeHtml,
  isUrlSafe,
  sanitizeUrl,
  
  // CSRF 防护
  generateCSRFToken,
  getCSRFToken,
  validateCSRFToken,
  addCSRFTokenToForm,
  
  // 输入验证
  validateUsername,
  validatePassword,
  validateEmail,
  
  // 内容安全
  setContentSecurityPolicy,
  filterSensitiveInfo,
  
  // 安全随机数
  generateSecureRandom,
  generateUUID,
  
  // 安全存储
  secureStore,
  secureRetrieve
}