/**
 * 响应式设计工具类
 * 提供设备检测、断点管理和响应式布局功能
 */

// 响应式断点定义
export const BREAKPOINTS = {
  mobile: 480,
  tabletSmall: 768,
  tablet: 992,
  desktopSmall: 1200,
  desktop: 1600,
  desktopLarge: 1920
}

// 设备类型枚举
export const DEVICE_TYPES = {
  MOBILE: 'mobile',
  TABLET_SMALL: 'tablet-small',
  TABLET: 'tablet',
  DESKTOP_SMALL: 'desktop-small',
  DESKTOP: 'desktop',
  DESKTOP_LARGE: 'desktop-large'
}

/**
 * 根据窗口宽度获取设备类型
 * @param {number} width - 窗口宽度
 * @returns {string} 设备类型
 */
export function getDeviceType(width = window.innerWidth) {
  if (width < BREAKPOINTS.mobile) return DEVICE_TYPES.MOBILE
  if (width < BREAKPOINTS.tabletSmall) return DEVICE_TYPES.TABLET_SMALL
  if (width < BREAKPOINTS.tablet) return DEVICE_TYPES.TABLET
  if (width < BREAKPOINTS.desktopSmall) return DEVICE_TYPES.DESKTOP_SMALL
  if (width < BREAKPOINTS.desktop) return DEVICE_TYPES.DESKTOP
  return DEVICE_TYPES.DESKTOP_LARGE
}

/**
 * 获取当前设备的每行显示数量
 * @param {string} deviceType - 设备类型
 * @returns {number} 每行显示数量
 */
export function getCardsPerRow(deviceType) {
  const cardsConfig = {
    [DEVICE_TYPES.MOBILE]: 1,
    [DEVICE_TYPES.TABLET_SMALL]: 2,
    [DEVICE_TYPES.TABLET]: 3,
    [DEVICE_TYPES.DESKTOP_SMALL]: 4,
    [DEVICE_TYPES.DESKTOP]: 5,
    [DEVICE_TYPES.DESKTOP_LARGE]: 6
  }
  
  return cardsConfig[deviceType] || 5
}

/**
 * 检查是否为移动设备
 * @param {string} deviceType - 设备类型
 * @returns {boolean}
 */
export function isMobile(deviceType) {
  return deviceType === DEVICE_TYPES.MOBILE
}

/**
 * 检查是否为平板设备
 * @param {string} deviceType - 设备类型
 * @returns {boolean}
 */
export function isTablet(deviceType) {
  return [DEVICE_TYPES.TABLET_SMALL, DEVICE_TYPES.TABLET].includes(deviceType)
}

/**
 * 检查是否为桌面设备
 * @param {string} deviceType - 设备类型
 * @returns {boolean}
 */
export function isDesktop(deviceType) {
  return [
    DEVICE_TYPES.DESKTOP_SMALL,
    DEVICE_TYPES.DESKTOP,
    DEVICE_TYPES.DESKTOP_LARGE
  ].includes(deviceType)
}

/**
 * 获取响应式CSS类名
 * @param {string} deviceType - 设备类型
 * @returns {object} CSS类名对象
 */
export function getResponsiveClasses(deviceType) {
  return {
    'mobile-layout': isMobile(deviceType),
    'tablet-layout': isTablet(deviceType),
    'desktop-layout': isDesktop(deviceType),
    [`device-${deviceType}`]: true
  }
}

/**
 * 创建响应式composable
 * @returns {object} 响应式数据和方法
 */
export function useResponsive() {
  const windowWidth = ref(window?.innerWidth || 1280)
  const windowHeight = ref(window?.innerHeight || 720)
  
  // 设备类型检测
  const deviceType = computed(() => getDeviceType(windowWidth.value))
  
  // 动态每行显示数量
  const cardsPerRow = computed(() => getCardsPerRow(deviceType.value))
  
  // 设备类型判断
  const isMobileDevice = computed(() => isMobile(deviceType.value))
  const isTabletDevice = computed(() => isTablet(deviceType.value))
  const isDesktopDevice = computed(() => isDesktop(deviceType.value))
  
  // 响应式CSS类
  const responsiveClasses = computed(() => getResponsiveClasses(deviceType.value))
  
  // 视窗更新处理
  function handleResize() {
    windowWidth.value = window.innerWidth
    windowHeight.value = window.innerHeight
  }
  
  // 节流处理的resize事件
  let resizeTimer = null
  function throttledResize() {
    if (resizeTimer) clearTimeout(resizeTimer)
    resizeTimer = setTimeout(handleResize, 100)
  }
  
  return {
    windowWidth,
    windowHeight,
    deviceType,
    cardsPerRow,
    isMobileDevice,
    isTabletDevice,
    isDesktopDevice,
    responsiveClasses,
    handleResize,
    throttledResize
  }
}

/**
 * 获取图片尺寸配置
 * @param {string} deviceType - 设备类型
 * @returns {object} 图片尺寸配置
 */
export function getImageSizes(deviceType) {
  const sizeConfig = {
    [DEVICE_TYPES.MOBILE]: {
      cardHeight: 240,
      bannerHeight: 280,
      highlightSize: 80
    },
    [DEVICE_TYPES.TABLET_SMALL]: {
      cardHeight: 180,
      bannerHeight: 320,
      highlightSize: 90
    },
    [DEVICE_TYPES.TABLET]: {
      cardHeight: 180,
      bannerHeight: 360,
      highlightSize: 100
    },
    [DEVICE_TYPES.DESKTOP_SMALL]: {
      cardHeight: 200,
      bannerHeight: 420,
      highlightSize: 120
    },
    [DEVICE_TYPES.DESKTOP]: {
      cardHeight: 200,
      bannerHeight: 520,
      highlightSize: 120
    },
    [DEVICE_TYPES.DESKTOP_LARGE]: {
      cardHeight: 220,
      bannerHeight: 560,
      highlightSize: 140
    }
  }
  
  return sizeConfig[deviceType] || sizeConfig[DEVICE_TYPES.DESKTOP]
}

/**
 * 媒体查询工具
 */
export const mediaQueries = {
  mobile: `(max-width: ${BREAKPOINTS.mobile - 1}px)`,
  tabletSmall: `(min-width: ${BREAKPOINTS.mobile}px) and (max-width: ${BREAKPOINTS.tabletSmall - 1}px)`,
  tablet: `(min-width: ${BREAKPOINTS.tabletSmall}px) and (max-width: ${BREAKPOINTS.tablet - 1}px)`,
  desktopSmall: `(min-width: ${BREAKPOINTS.tablet}px) and (max-width: ${BREAKPOINTS.desktopSmall - 1}px)`,
  desktop: `(min-width: ${BREAKPOINTS.desktopSmall}px) and (max-width: ${BREAKPOINTS.desktop - 1}px)`,
  desktopLarge: `(min-width: ${BREAKPOINTS.desktop}px)`,
  
  // 简化的媒体查询
  isMobile: `(max-width: ${BREAKPOINTS.mobile - 1}px)`,
  isTablet: `(min-width: ${BREAKPOINTS.mobile}px) and (max-width: ${BREAKPOINTS.tablet - 1}px)`,
  isDesktop: `(min-width: ${BREAKPOINTS.tablet}px)`,
  
  // 特殊查询
  touchDevice: '(hover: none) and (pointer: coarse)',
  reducedMotion: '(prefers-reduced-motion: reduce)',
  darkTheme: '(prefers-color-scheme: dark)',
  highDensity: '(-webkit-min-device-pixel-ratio: 2), (min-resolution: 192dpi)'
}

// 导入Vue的ref和computed（如果在Vue环境中使用）
import { ref, computed } from 'vue'