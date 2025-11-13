// 应用主页面
<template>
  <div class="app-container">
    <HeaderNav v-if="$route.path !== '/open-platform' && !$route.query.popup" />
    <SubNav v-if="$route.path !== '/my-music' && !$route.path.startsWith('/album/') && !$route.path.startsWith('/toplist/') && $route.path !== '/game' && $route.path !== '/0717' && $route.path !== '/open-platform' && !$route.query.popup" />
    <router-view />
    <!-- 音乐播放器 -->
    <MusicPlayer v-if="$route.path !== '/game' && $route.path !== '/open-platform' && !$route.query.popup" />
    <FullscreenPlayer 
      :visible="showFullscreenPlayer" 
      @close="showFullscreenPlayer = false"
      @toggle-playlist="showPlaylist = !showPlaylist"
    />
    <PlaylistPanelNew 
      :is-open="showPlaylist" 
      :fullscreen-mode="showFullscreenPlayer"
      @close="closePlaylist"
      @play-song="playByIndex"
    />
    <!-- 回到顶部按钮 -->
    <BackToTop 
      v-if="$route.path !== '/game' && $route.path !== '/open-platform'"
      :threshold="300"
      :show-progress="true"
      :show-tooltip="true"
      :bottom-offset="90"
      :right-offset="30"
    />
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import HeaderNav from './components/HeaderNav.vue'
import SubNav from './components/SubNav.vue'
import MusicPlayer from './components/MusicPlayer.vue'
import FullscreenPlayer from './components/FullscreenPlayer.vue'
import PlaylistPanelNew from './components/PlaylistPanelNew.vue'
import BackToTop from './components/BackToTop.vue'
import { initMusicPlayer, playByIndex } from '@/utils/musicPlayer.js'

// 播放器状态
const showFullscreenPlayer = ref(false)
const showPlaylist = ref(false)

let backgroundChangeHandler = null;

// 添加页面载入动画效果
onMounted(() => {
  document.body.classList.add('page-loaded');
  
  // 初始化主题
  const savedTheme = localStorage.getItem('theme') || 'netease'
  document.documentElement.setAttribute('data-theme', savedTheme)
  
  // 初始化背景
  const savedBackground = localStorage.getItem('userBannerBg')
  if (savedBackground) {
    applyBackground(savedBackground)
  }
  
  // 初始化播放器
  initMusicPlayer()
  
  // 监听背景变化事件
  backgroundChangeHandler = (event) => {
    applyBackground(event.detail.url)
  }
  window.addEventListener('background-changed', backgroundChangeHandler)
  
  // 监听播放器事件
  window.addEventListener('openFullscreenPlayer', () => {
    showFullscreenPlayer.value = true
  })
  
  window.addEventListener('openPlaylist', () => {
    showPlaylist.value = !showPlaylist.value // 切换显示/隐藏状态
    // 通知BackToTop组件播放列表状态变化
    window.dispatchEvent(new CustomEvent('playlistToggle', { detail: { isOpen: showPlaylist.value } }))
  })
});

onUnmounted(() => {
  if (backgroundChangeHandler) {
    window.removeEventListener('background-changed', backgroundChangeHandler)
  }
});

// 应用背景图片
function applyBackground(url) {
  if (url) {
    document.body.style.backgroundImage = `url(${url})`
    document.body.style.backgroundSize = 'cover'
    document.body.style.backgroundPosition = 'center'
    document.body.style.backgroundRepeat = 'no-repeat'
    document.body.style.backgroundAttachment = 'fixed'
  } else {
    // 移除背景
    document.body.style.backgroundImage = ''
    document.body.style.backgroundSize = ''
    document.body.style.backgroundPosition = ''
    document.body.style.backgroundRepeat = ''
    document.body.style.backgroundAttachment = ''
  }
}

// 关闭播放列表
function closePlaylist() {
  showPlaylist.value = false
  window.dispatchEvent(new CustomEvent('playlistToggle', { detail: { isOpen: false } }))
}
</script>

<style>

/* 全局body样式 */
body {
  background-color: var(--background);
  color: var(--text-primary);
  margin: 0;
  padding: 0;
  font-family: var(--font-sans);
}

:root {
  /* 红色系配色方案 */
  --primary: #d33a31; /* 主色调：网易云红 */
  --primary-light: #e5443a; /* 主色调浅色 */
  --primary-dark: #b8302a; /* 主色调深色 */
  --secondary: #f5f5f5; /* 辅助色：浅灰色 */
  --accent: #d33a31; /* 强调色：红色 */
  --background: #fafafa; /* 背景色：浅灰白 */
  --background-light: #ffffff; /* 背景亮色 */
  --background-card: rgba(255, 255, 255, 0.95); /* 卡片背景 */
  --text-primary: #333333; /* 主要文本：深灰色 */
  --text-secondary: #666666; /* 次要文本：中灰色 */
  --text-tertiary: #999999; /* 三级文本：浅灰色 */
  --border: #e7e7e7; /* 边框色：浅灰色 */
  --success: #52c41a; /* 成功色：绿色 */
  --warning: #faad14; /* 警告色：黄色 */
  --error: #ff4d4f; /* 错误色：红色 */

  /* 新增全局动画变量 */
  --animation-pulse: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
  --animation-float: float 3s ease-in-out infinite;
  --animation-gradient: gradient 8s ease infinite;
  
  /* 排版系统 */
  --font-sans: 'Inter', system-ui, sans-serif;
  --font-display: 'Montserrat', sans-serif;
  --text-xs: 0.75rem; /* 12px */
  --text-sm: 0.875rem; /* 14px */
  --text-base: 1rem; /* 16px */
  --text-lg: 1.125rem; /* 18px */
  --text-xl: 1.25rem; /* 20px */
  --text-2xl: 1.5rem; /* 24px */
  --text-3xl: 1.875rem; /* 30px */
  --text-4xl: 2.25rem; /* 36px */
  --font-light: 300;
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;

  /* 间距系统 */
  --space-xs: 0.25rem; /* 4px */
  --space-sm: 0.5rem; /* 8px */
  --space-md: 1rem; /* 16px */
  --space-lg: 1.5rem; /* 24px */
  --space-xl: 2rem; /* 32px */
  --space-2xl: 3rem; /* 48px */
  --space-3xl: 4rem; /* 64px */

  /* 边框与圆角 */
  --border-radius-sm: 0.375rem; /* 6px */
  --border-radius: 0.5rem; /* 8px */
  --border-radius-lg: 0.75rem; /* 12px */
  --border-radius-xl: 1rem; /* 16px */
  --border-radius-2xl: 1.5rem; /* 24px */
  --border-width: 1px;
  --border-width-lg: 2px;

  /* 阴影系统 */
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.1);
  --shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 10px 15px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 20px 25px rgba(0, 0, 0, 0.1);
  --shadow-xl: 0 25px 50px rgba(0, 0, 0, 0.2);

  /* 过渡系统 */
  --transition-fast: 0.2s ease;
  --transition-normal: 0.3s ease;
  --transition-slow: 0.5s ease;
  --transition-transform: transform 0.3s ease, box-shadow 0.3s ease;
}

/* 粉色主题变量 */
[data-theme="pink"] {
  --primary: #ec4899; /* 主色调：粉色 */
  --primary-light: #f472b6; /* 主色调浅色 */
  --primary-dark: #be185d; /* 主色调深色 */
  --secondary: #f9a8d4; /* 辅助色：浅粉色 */
  --accent: #db2777; /* 强调色：深粉色 */
  --background: #f9a8d4; /* 背景色：浅粉色系 */
  --background-light: #fcf7ff; /* 背景亮色 */
  --background-card: rgba(249, 168, 212, 0.95); /* 卡片背景 */
  --text-primary: #831843; /* 主要文本：深粉色 */
  --text-secondary: #be185d; /* 次要文本：粉色 */
  --text-tertiary: #ec4899; /* tertiary文本：浅粉色 */
  --border: rgba(236, 72, 153, 0.3); /* 边框色：粉色半透明 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ef4444; /* 错误色：保持红色 */
}

/* 浅粉色主题变量 */
[data-theme="lightPink"] {
  --primary: #f6406d; /* 主色调：浅粉色 */
  --primary-light: #fce7ed; /* 主色调浅色 */
  --primary-dark: #db7093; /* 主色调深色 */
  --secondary: #fce7ed; /* 辅助色：浅粉色 */
  --accent: #f891aa; /* 强调色：浅粉色 */
  --background: #f7b9c8; /* 背景色：极浅粉色系 */
  --background-light: #d9a1ab; /* 背景亮色 */
  --background-card: rgba(247, 185, 200, 0.95); /* 卡片背景 */
  --text-primary: #4a1e2b; /* 主要文本：深粉色 */
  --text-secondary: #7d4a5a; /* 次要文本：中粉色 */
  --text-tertiary: #a66b7a; /* tertiary文本：浅粉色 */
  --border: rgba(247, 185, 200, 0.3); /* 边框色：浅粉色半透明 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ef4444; /* 错误色：保持红色 */
}

/* 蓝色主题变量 */
[data-theme="blue"] {
  --primary: #3b82f6; /* 主色调：蓝色 */
  --primary-light: #60a5fa; /* 主色调浅色 */
  --primary-dark: #2563eb; /* 主色调深色 */
  --secondary: #6366f1; /* 辅助色：靛蓝色 */
  --accent: #06b6d4; /* 强调色：青色 */
  --background: #93c5fd; /* 背景色：浅蓝色系 */
  --background-light: #f1f5f9; /* 背景亮色 */
  --background-card: rgba(147, 197, 253, 0.95); /* 卡片背景 */
  --text-primary: #1e3a8a; /* 主要文本：深蓝色 */
  --text-secondary: #2563eb; /* 次要文本：蓝色 */
  --text-tertiary: #3b82f6; /* tertiary文本：浅蓝色 */
  --border: rgba(59, 130, 246, 0.3); /* 边框色：蓝色半透明 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ef4444; /* 错误色：保持红色 */
}

/* 绿色主题变量 */
[data-theme="green"] {
  --primary: #10b981; /* 主色调：绿色 */
  --primary-light: #34d399; /* 主色调浅色 */
  --primary-dark: #059669; /* 主色调深色 */
  --secondary: #059669; /* 辅助色：深绿色 */
  --accent: #06b6d4; /* 强调色：青色 */
  --background: #8dffb6; /* 背景色：浅绿色系 */
  --background-light: #f9fafb; /* 背景亮色 */
  --background-card: rgba(141, 255, 182, 0.95); /* 卡片背景 */
  --text-primary: #064e3b; /* 主要文本：深绿色 */
  --text-secondary: #059669; /* 次要文本：绿色 */
  --text-tertiary: #10b981; /* tertiary文本：浅绿色 */
  --border: rgba(16, 185, 129, 0.3); /* 边框色：绿色半透明 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ef4444; /* 错误色：保持红色 */
}

/* 紫色主题变量 */
[data-theme="purple"] {
  --primary: #8b5cf6; /* 主色调：紫色 */
  --primary-light: #a78bfa; /* 主色调浅色 */
  --primary-dark: #7c3aed; /* 主色调深色 */
  --secondary: #a855f7; /* 辅助色：深紫色 */
  --accent: #ec4899; /* 强调色：粉色 */
  --background: #c2a0d9; /* 背景色：浅紫色系 */
  --background-light: #f9fafb; /* 背景亮色 */
  --background-card: rgba(202, 160, 217, 0.95); /* 卡片背景 */
  --text-primary: #4c1d95; /* 主要文本：深紫色 */
  --text-secondary: #7c3aed; /* 次要文本：紫色 */
  --text-tertiary: #8b5cf6; /* tertiary文本：浅紫色 */
  --border: rgba(139, 92, 246, 0.3); /* 边框色：紫色半透明 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ef4444; /* 错误色：保持红色 */
}

/* 橙色主题变量 */
[data-theme="orange"] {
  --primary: #f97316; /* 主色调：橙色 */
  --primary-light: #fb923c; /* 主色调浅色 */
  --primary-dark: #ea580c; /* 主色调深色 */
  --secondary: #f59e0b; /* 辅助色：琥珀色 */
  --accent: #ef4444; /* 强调色：红色 */
  --background: #fdba74; /* 背景色：浅橙色系 */
  --background-light: #f9fafb; /* 背景亮色 */
  --background-card: rgba(253, 186, 116, 0.95); /* 卡片背景 */
  --text-primary: #7c2d12; /* 主要文本：深橙色 */
  --text-secondary: #ea580c; /* 次要文本：橙色 */
  --text-tertiary: #f97316; /* tertiary文本：浅橙色 */
  --border: rgba(249, 115, 22, 0.3); /* 边框色：橙色半透明 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ef4444; /* 错误色：保持红色 */
}

/* 红色主题变量 - 优雅优化版 */
[data-theme="red"] {
  --primary: #ff0000; /* 主色调：红色 */
  --primary-light: #ff8080; /* 主色调浅色：浅红色 */
  --primary-dark: #cc3333; /* 主色调深色：深红色 */
  --secondary: #f9fafb; /* 辅助色：浅灰色 - 增加层次感 */
  --accent: #f43f5e; /* 强调色：玫红色 - 增强视觉焦点 */
  --background: #ff5454; /* 背景色：白色 - 简洁清爽 */
  --background-light: #ffffff; /* 背景亮色：白色 - 提高播放器对比度 */
  --background-card: rgba(255, 84, 84, 0.95); /* 卡片背景：白色半透明 */
  --text-primary: #000409; /* 主要文本：深灰色 - 提高可读性 */
  --text-secondary: #000000; /* 次要文本：中灰色 */
  --text-tertiary: #5187f4; /* tertiary文本：浅灰色 */
  --border: rgba(255, 255, 255, 0.603); /* 边框色：主色半透明 - 精致边框 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ff4f4f; /* 错误色：使用主色调 */
}

/* 黑色主题变量 */
[data-theme="black"] {
  --primary: #ffffff;
  --primary-light: #e5e5e5;
  --primary-dark: #cccccc;
  --secondary: #f3f4f6;
  --accent: #3b82f6;
  --background: #000000;
  --background-light: #111111;
  --background-card: rgba(0, 0, 0, 0.95);
  --text-primary: #ffffff;
  --text-secondary: #e5e5e5;
  --text-tertiary: #cccccc;
  --border: rgba(255, 255, 255, 0.2);
  --success: #10b981;
  --warning: #f59e0b;
  --error: #ef4444;
}

/* 黑色主题下body样式 */
[data-theme="black"] body {
  background-image: none !important;
  background-color: var(--background) !important;
  background: var(--background) !important;
  background-repeat: no-repeat !important;
  background-attachment: fixed !important;
}

/* 黑色主题下app-container样式 */
[data-theme="black"] .app-container {
  background: var(--background) !important;
}

/* 白色主题变量 */
[data-theme="white"] {
  --primary: #696969 !important; /* 主色调：黑色 */
  --primary-light: #333333; /* 主色调浅色 */
  --primary-dark: #000000; /* 主色调深色 */
  --secondary: #666666; /* 辅助色：深灰色 */
  --accent: #333333; /* 强调色：中灰色 */
  --background: #ffffff; /* 背景色：白色 */
  --background-light: #f9fafb; /* 背景亮色 */
  --background-card: rgba(255, 255, 255, 0.95); /* 卡片背景 */
  --text-primary: #000000; /* 主要文本：黑色 */
  --text-secondary: #333333; /* 次要文本：深灰色 */
  --text-tertiary: #666666; /* tertiary文本：中灰色 */
  --border: rgba(0, 0, 0, 0.1); /* 边框色：黑色半透明 */
  --success: #10b981; /* 成功色：保持绿色 */
  --warning: #f59e0b; /* 警告色：保持琥珀黄 */
  --error: #ef4444; /* 错误色：保持红色 */
}
/* 基础样式重置与设置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--font-sans);
  background-color: var(--background);
  color: var(--text-primary);
  line-height: 1.5;
  background-image:
    radial-gradient(circle at 25% 25%, rgba(236, 72, 153, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(244, 114, 182, 0.05) 0%, transparent 50%);
  min-height: 100vh;
  transition: background-color var(--transition-normal), background-image var(--transition-normal);
}

/* 为所有元素添加颜色过渡效果 */
* {
  transition: color var(--transition-normal), background-color var(--transition-normal), border-color var(--transition-normal);
}

.app-container {
  width: 100%;
  min-height: 100vh;
  margin: 0 auto;
  background: transparent;
  overflow-x: hidden;
  padding-bottom: 80px; /* 为底部播放器留出空间 */
}

/* 开放平台页面移除底部间距 */
body.open-platform-page .app-container {
  padding-bottom: 0;
}

/* 应用全局滚动条样式 */
/* 具体样式定义在main.css中的全局滚动条美化部分 */

::-webkit-scrollbar-corner {
  background: transparent !important;
}

/* Firefox浏览器 */
* {
  scrollbar-width: none !important;
  scrollbar-color: transparent transparent !important;
}

/* IE和Edge Legacy浏览器 */
* {
  -ms-overflow-style: none !important;
}

/* 确保页面没有水平滚动条 */
html, body {
  overflow-x: hidden;
}

</style>