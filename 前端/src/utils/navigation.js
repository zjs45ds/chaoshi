/**
 * 安全导航工具函数
 * 解决 Vue Router 重复导航警告问题
 */

import { isNavigationFailure, NavigationFailureType } from 'vue-router'

/**
 * 创建安全导航工具
 * @param {Router} router - Vue Router 实例
 * @param {Route} route - 当前路由
 */
export const createSafeNavigation = (router, route) => {
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
        
        await router.push(path)
      } catch (error) {
        if (!isNavigationFailure(error, NavigationFailureType.duplicated)) {
          console.error('Navigation error:', error)
          throw error
        }
        // 忽略重复导航错误
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
        
        await router.replace(path)
      } catch (error) {
        if (!isNavigationFailure(error, NavigationFailureType.duplicated)) {
          console.error('Navigation error:', error)
          throw error
        }
        // 忽略重复导航错误
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
        console.error('Back navigation error:', error)
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
        console.error('Forward navigation error:', error)
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
      
      if (replace) {
        await router.replace(path)
      } else {
        await router.push(path)
      }
    } catch (error) {
      if (!isNavigationFailure(error, NavigationFailureType.duplicated)) {
        console.error('Navigation error:', error)
        throw error
      }
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