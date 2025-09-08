/**
 * 图片处理工具模块
 * 统一管理图片压缩、优化和加载逻辑
 */

/**
 * 图片压缩配置
 */
const IMAGE_CONFIG = {
  // 最大尺寸限制
  MAX_WIDTH: 1920,
  MAX_HEIGHT: 1080,
  // 文件大小限制（字节）
  MAX_FILE_SIZE: 10 * 1024 * 1024, // 10MB
  // 压缩提示阈值
  COMPRESSION_TIP_SIZE: 2 * 1024 * 1024, // 2MB
  // 质量配置
  QUALITY_HIGH: 0.8,
  QUALITY_LOW: 0.7,
  QUALITY_THRESHOLD: 5 * 1024 * 1024, // 5MB
  // 支持的图片格式
  SUPPORTED_TYPES: ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
}

/**
 * 验证图片文件
 * @param {File} file - 图片文件
 * @returns {Object} 验证结果
 */
export function validateImageFile(file) {
  const errors = []
  
  // 检查文件类型
  if (!IMAGE_CONFIG.SUPPORTED_TYPES.includes(file.type)) {
    errors.push('请选择有效的图片文件（JPG、PNG、GIF、WebP）')
  }
  
  // 检查文件大小
  if (file.size > IMAGE_CONFIG.MAX_FILE_SIZE) {
    errors.push('图片文件大小不能超过10MB')
  }
  
  return {
    isValid: errors.length === 0,
    errors,
    needsCompression: file.size > IMAGE_CONFIG.COMPRESSION_TIP_SIZE
  }
}

/**
 * 计算图片缩放尺寸
 * @param {number} width - 原始宽度
 * @param {number} height - 原始高度
 * @returns {Object} 缩放后的尺寸
 */
export function calculateImageSize(width, height) {
  const { MAX_WIDTH, MAX_HEIGHT } = IMAGE_CONFIG
  
  if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
    return { width, height }
  }
  
  const ratio = Math.min(MAX_WIDTH / width, MAX_HEIGHT / height)
  return {
    width: Math.floor(width * ratio),
    height: Math.floor(height * ratio)
  }
}

/**
 * 图片压缩处理
 * @param {File} file - 原始图片文件
 * @param {Object} options - 压缩选项
 * @returns {Promise<string>} 压缩后的base64数据
 */
export function compressImage(file, options = {}) {
  return new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    const img = new Image()
    
    img.onload = () => {
      try {
        // 计算目标尺寸
        const { width, height } = calculateImageSize(img.width, img.height)
        
        canvas.width = width
        canvas.height = height
        
        // 绘制压缩后的图片
        ctx.drawImage(img, 0, 0, width, height)
        
        // 选择压缩质量
        const quality = file.size > IMAGE_CONFIG.QUALITY_THRESHOLD 
          ? IMAGE_CONFIG.QUALITY_LOW 
          : IMAGE_CONFIG.QUALITY_HIGH
          
        // 转换为base64
        const compressedDataUrl = canvas.toDataURL(
          options.outputType || file.type, 
          options.quality || quality
        )
        
        // 计算压缩比例
        const originalSize = file.size / 1024 / 1024
        const compressedSize = compressedDataUrl.length * 0.75 / 1024 / 1024
        
        resolve({
          dataUrl: compressedDataUrl,
          originalSize: originalSize.toFixed(2),
          compressedSize: compressedSize.toFixed(2),
          compressionRatio: ((1 - compressedSize / originalSize) * 100).toFixed(1)
        })
      } catch (error) {
        reject(new Error('图片压缩失败: ' + error.message))
      }
    }
    
    img.onerror = () => {
      reject(new Error('图片加载失败'))
    }
    
    img.src = URL.createObjectURL(file)
  })
}

/**
 * 图片懒加载处理
 * @param {string} selector - 图片选择器
 * @param {Object} options - 配置选项
 */
export function setupLazyLoading(selector = 'img[data-src]', options = {}) {
  const config = {
    rootMargin: '50px 0px',
    threshold: 0.01,
    ...options
  }
  
  if ('IntersectionObserver' in window) {
    const imageObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target
          img.src = img.dataset.src
          img.classList.remove('lazy')
          img.classList.add('loaded')
          observer.unobserve(img)
        }
      })
    }, config)
    
    document.querySelectorAll(selector).forEach(img => {
      imageObserver.observe(img)
    })
  } else {
    // 降级处理
    document.querySelectorAll(selector).forEach(img => {
      img.src = img.dataset.src
      img.classList.remove('lazy')
      img.classList.add('loaded')
    })
  }
}

/**
 * 预加载关键图片
 * @param {Array<string>} urls - 图片URL数组
 * @returns {Promise<Array>} 预加载结果
 */
export function preloadImages(urls) {
  return Promise.allSettled(
    urls.map(url => {
      return new Promise((resolve, reject) => {
        const img = new Image()
        img.onload = () => resolve(url)
        img.onerror = () => reject(new Error(`Failed to load image: ${url}`))
        img.src = url
      })
    })
  )
}

/**
 * 生成响应式图片srcset
 * @param {string} baseUrl - 基础图片URL
 * @param {Array<number>} widths - 宽度数组
 * @returns {string} srcset字符串
 */
export function generateSrcSet(baseUrl, widths = [480, 768, 1024, 1440, 1920]) {
  return widths
    .map(width => `${baseUrl}?w=${width} ${width}w`)
    .join(', ')
}

export default {
  validateImageFile,
  calculateImageSize,
  compressImage,
  setupLazyLoading,
  preloadImages,
  generateSrcSet,
  IMAGE_CONFIG
}