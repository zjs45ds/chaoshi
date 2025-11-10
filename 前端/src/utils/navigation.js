/**
 * 安全导航工具函数
 * 解决 Vue Router 重复导航警告问题和导航取消问题
 */

import { isNavigationFailure, NavigationFailureType } from 'vue-router'

/**
 * 创建安全导航工具
 * @param {Router} router - Vue Router 实例
 * @param {Route} route - 当前路由
 */
export const createSafeNavigation = (router, route) => {
  let pendingNavigation = null
  const NAVIGATION_DELAY = 50 // 导航延迟时间
  
  return {
    /**
     * 安全的 push 导航
     * @param {string|object} path - 目标路径或路由对象
     */
    push: async (path) => {
      try {
        // 检查是否为当前路径
        const targetPath = typeof path === 'string' ? path : path.path
        if (route.path === targetPath) {
          return Promise.resolve()
        }
        
        // 取消待处理的导航
        if (pendingNavigation) {
          clearTimeout(pendingNavigation)
        }
        
        // 添加导航延迟，避免快速连续导航导致的问题
        return new Promise((resolve, reject) => {
          pendingNavigation = setTimeout(async () => {
            try {
              await router.push(path)
              resolve()
            } catch (error) {
              if (isNavigationFailure(error, NavigationFailureType.duplicated) || 
                  isNavigationFailure(error, NavigationFailureType.cancelled)) {
                resolve() // 忽略重复和取消的错误
              } else {
                // CONSOLE LOG REMOVED: console.error('Navigation error:', error)
                reject(error)
              }
            }
          }, NAVIGATION_DELAY)
        })
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('Navigation setup error:', error)
        throw error
      }
    },

    /**
     * 安全的 replace 导航
     * @param {string|object} path - 目标路径或路由对象
     */
    replace: async (path) => {
      try {
        // 检查是否为当前路径
        const targetPath = typeof path === 'string' ? path : path.path
        if (route.path === targetPath) {
          return Promise.resolve()
        }
        
        // 取消待处理的导航
        if (pendingNavigation) {
          clearTimeout(pendingNavigation)
        }
        
        // 添加导航延迟，避免快速连续导航导致的问题
        return new Promise((resolve, reject) => {
          pendingNavigation = setTimeout(async () => {
            try {
              await router.replace(path)
              resolve()
            } catch (error) {
              if (isNavigationFailure(error, NavigationFailureType.duplicated) || 
                  isNavigationFailure(error, NavigationFailureType.cancelled)) {
                resolve() // 忽略重复和取消的错误
              } else {
                // CONSOLE LOG REMOVED: console.error('Navigation error:', error)
                reject(error)
              }
            }
          }, NAVIGATION_DELAY)
        })
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('Navigation setup error:', error)
        throw error
      }
    },

    /**
     * 安全的后退导航
     */
    back: () => {
      try {
        if (window.history.length > 1) {
          router.go(-1)
        } else {
          router.push('/')
        }
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('Back navigation error:', error)
        router.push('/')
      }
    },

    /**
     * 安全的前进导航
     */
    forward: () => {
      try {
        router.go(1)
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('Forward navigation error:', error)
      }
    }
  }
}

/**
 * 检查路径是否相同
 * @param {string} currentPath - 当前路径
 * @param {string|object} targetPath - 目标路径
 */
export const isSamePath = (currentPath, targetPath) => {
  const target = typeof targetPath === 'string' ? targetPath : targetPath.path
  return currentPath === target
}

/**
 * 快速导航函数（兼容旧代码）
 */
export const safeNavigate = {
  // 导航防抖：避免短时间内多次导航导致的取消问题
  pendingNavigation: null,
  NAVIGATION_DELAY: 50,
  
  /**
   * 安全导航到指定路径
   * @param {Router} router - Vue Router 实例
   * @param {Route} route - 当前路由
   * @param {string|object} path - 目标路径
   * @param {boolean} replace - 是否使用 replace 而不是 push
   */
  to: async (router, route, path, replace = false) => {
    try {
      const targetPath = typeof path === 'string' ? path : path.path
      if (route.path === targetPath) {
        return Promise.resolve()
      }
      
      // 取消待处理的导航
      if (safeNavigate.pendingNavigation) {
        clearTimeout(safeNavigate.pendingNavigation)
      }
      
      // 添加导航延迟，避免快速连续导航导致的问题
      return new Promise((resolve, reject) => {
        safeNavigate.pendingNavigation = setTimeout(async () => {
          try {
            if (replace) {
              await router.replace(path)
            } else {
              await router.push(path)
            }
            resolve()
          } catch (error) {
            if (isNavigationFailure(error, NavigationFailureType.duplicated) || 
                isNavigationFailure(error, NavigationFailureType.cancelled)) {
              resolve() // 忽略重复和取消的错误
            } else {
              // CONSOLE LOG REMOVED: console.error('Navigation error:', error)
              reject(error)
            }
          }
        }, safeNavigate.NAVIGATION_DELAY)
      })
    } catch (error) {
      // CONSOLE LOG REMOVED: console.error('Navigation setup error:', error)
      throw error
    }
  }
}

/**
 * Vue Composition API 导航组合函数
 */
export const useNavigation = () => {
  const { useRouter, useRoute } = require('vue-router')
  const router = useRouter()
  const route = useRoute()
  
  return createSafeNavigation(router, route)
}