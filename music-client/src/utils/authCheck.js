/**
 * 登录状态检查工具
 */
import { ElMessageBox } from 'element-plus'
import router from '@/router'

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export function isUserLoggedIn() {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  const isLogin = localStorage.getItem('isLogin') || sessionStorage.getItem('isLogin')
  // 只要有token或isLogin标记为'1'就认为已登录
  return (token && token !== 'null' && token !== 'undefined') || isLogin === '1'
}

/**
 * 检查登录状态，未登录则提示并跳转
 * @param {string} action - 操作名称，如"播放歌曲"、"播放视频"
 * @returns {Promise<boolean>} 是否已登录
 */
export async function checkLoginForAction(action = '播放') {
  if (isUserLoggedIn()) {
    return true
  }

  try {
    await ElMessageBox.confirm(
      `请先登录后再${action}`,
      '需要登录',
      {
        confirmButtonText: '去登录',
        cancelButtonText: '取消',
        type: 'warning',
        center: true,
        customClass: 'auth-check-message-box'
      }
    )
    
    // 用户点击了"去登录"，跳转到登录页
    router.push({
      path: '/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return false
  } catch {
    // 用户点击了"取消"
    return false
  }
}

/**
 * 获取当前用户ID
 * @returns {string|null} 用户ID
 */
export function getCurrentUserId() {
  return localStorage.getItem('userId') || 
         localStorage.getItem('currentUserId') || 
         sessionStorage.getItem('userId') || 
         sessionStorage.getItem('currentUserId')
}
