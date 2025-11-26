import { ref } from 'vue'

// 背景图片列表
export const backgroundImages = [
  'http://localhost:9000/chaoshi/背景/侧脸.png',
  'http://localhost:9000/chaoshi/背景/背景1.png',
  'http://localhost:9000/chaoshi/背景/背景2.png',
  'http://localhost:9000/chaoshi/背景/背景3.png',
  'http://localhost:9000/chaoshi/背景/背景4.png',
  'http://localhost:9000/chaoshi/背景/背景5.png',
  'http://localhost:9000/chaoshi/背景/双马尾小猫.png',
  'http://localhost:9000/chaoshi/背景/背景.jpg'
]

// 当前背景状态
const currentBackgroundIndex = ref(0)
const currentBackgroundUrl = ref('')

// 初始化背景
export const initBackground = () => {
  // 优先从 userBannerBg 获取（这是 HeaderNav 使用的键）
  const userBannerBg = localStorage.getItem('userBannerBg')
  
  if (userBannerBg) {
    currentBackgroundUrl.value = userBannerBg
    // 尝试找到对应的索引
    const index = backgroundImages.indexOf(userBannerBg)
    if (index !== -1) {
      currentBackgroundIndex.value = index
    }
  } else {
    // 如果没有 userBannerBg，尝试从 globalBackgroundIndex 获取
    const savedIndex = localStorage.getItem('globalBackgroundIndex')
    if (savedIndex !== null) {
      const index = parseInt(savedIndex)
      if (index >= 0 && index < backgroundImages.length) {
        currentBackgroundIndex.value = index
        currentBackgroundUrl.value = backgroundImages[index]
      }
    } else {
      // 默认背景
      currentBackgroundUrl.value = backgroundImages[0]
      currentBackgroundIndex.value = 0
    }
  }
  
  return currentBackgroundUrl.value
}

// 获取当前背景
export const getCurrentBackground = () => {
  return currentBackgroundUrl
}

// 更新背景
export const updateBackground = (index) => {
  if (index >= 0 && index < backgroundImages.length) {
    const url = backgroundImages[index]
    currentBackgroundIndex.value = index
    currentBackgroundUrl.value = url
    
    // 同步保存到两个地方，保持一致性
    localStorage.setItem('globalBackgroundIndex', index.toString())
    localStorage.setItem('userBannerBg', url)
    
    // 触发自定义事件
    window.dispatchEvent(new CustomEvent('background-changed', {
      detail: { url, index }
    }))
  }
}

// 通过 URL 更新背景
export const updateBackgroundByUrl = (url) => {
  currentBackgroundUrl.value = url
  localStorage.setItem('userBannerBg', url)
  
  const index = backgroundImages.indexOf(url)
  if (index !== -1) {
    currentBackgroundIndex.value = index
    localStorage.setItem('globalBackgroundIndex', index.toString())
  }
  
  window.dispatchEvent(new CustomEvent('background-changed', {
    detail: { url, index }
  }))
}

// 切换到下一个背景
export const nextBackground = () => {
  // 如果当前背景不在列表中，从 0 开始
  let nextIndex = currentBackgroundIndex.value + 1
  if (nextIndex >= backgroundImages.length) {
    nextIndex = 0
  }
  updateBackground(nextIndex)
  return nextIndex
}
