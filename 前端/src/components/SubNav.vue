<template>
  <nav class="sub-nav">
    <div class="sub-nav-content">
      <a @click.prevent="navigateTo('/')" class="sub-nav-item" :class="{ active: isActive('/') }">
        <span class="nav-icon">🎵</span> 首页
      </a>
      <a @click.prevent="navigateTo('/artist')" class="sub-nav-item" :class="{ active: isActive('/artist') }">
        <span class="nav-icon">👤</span> 歌手
      </a>
      <a @click.prevent="navigateTo('/album')" class="sub-nav-item" :class="{ active: isActive('/album') }">
        <span class="nav-icon">💿</span> 新碟
      </a>
      <a @click.prevent="navigateTo('/toplist')" class="sub-nav-item" :class="{ active: isActive('/toplist') }">
        <span class="nav-icon">🏆</span> 排行榜
      </a>
      <a @click.prevent="navigateTo('/mv')" class="sub-nav-item" :class="{ active: isActive('/mv') }">
        <span class="nav-icon">🎬</span> MV
      </a>
    </div>
  </nav>
</template>

<style scoped>
.sub-nav {
  position: relative;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  z-index: 100;
  transition: all var(--transition-normal);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.sub-nav-content {
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56px;
  gap: var(--space-lg);
  padding: 0 var(--space-md);
  overflow-x: auto;
  scrollbar-width: none;
}

.sub-nav-content::-webkit-scrollbar {
  display: none;
}

.sub-nav-item {
  font-size: 16px;
  color: #4a5568;
  font-weight: 500;
  text-decoration: none;
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--border-radius);
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  cursor: pointer;
}

.nav-icon {
  font-size: 18px;
  vertical-align: middle;
  color: #718096;
}

.sub-nav-item:hover {
  color: var(--primary);
  background: rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
}

.sub-nav-item:hover .nav-icon {
  color: var(--primary);
}

.sub-nav-item.active {
  font-weight: 600;
  box-shadow: 0 2px 0 0 var(--primary) !important;
  color: #1e40af !important;
  background: rgba(59, 130, 246, 0.15) !important;
  transform: translateY(-2px);
}

.sub-nav-item.active .nav-icon {
  color: var(--primary) !important;
}
</style>

<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 导航函数，包含路由检查
function navigateTo(path) {
  if (route.path !== path) {
    router.push(path)
  }
}

// 检查当前路由是否激活
function isActive(path) {
  if (path === '/') {
    return route.path === '/'
  }
  // 对于 /album, /artist, /toplist, /mv 等路径
  // 检查当前路径是否完全匹配或以该路径开头
  return route.path === path || route.path.startsWith(path + '/')
}

// 无需逻辑，纯展示组件
if (typeof window !== 'undefined') {
  document.body.classList.add('has-subnav');
}
</script>

<style>
/* 黑色主题样式 */
[data-theme="black"] .sub-nav {
  background: var(--background);
  border-bottom: 1px solid var(--border);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

[data-theme="black"] .sub-nav-item {
  color: var(--text-secondary);
}

[data-theme="black"] .sub-nav-item:hover {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.1);
}

[data-theme="black"] .nav-icon {
  color: var(--text-tertiary);
}

[data-theme="black"] .sub-nav-item:hover .nav-icon {
  color: var(--text-primary);
}

[data-theme="black"] .sub-nav-item.active {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border-bottom: none;
}

.my-music-page .sub-nav {
  display: none !important;
}
</style>