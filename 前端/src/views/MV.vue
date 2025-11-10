// MV列表页面
<template>
  <div class="mv-page">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载MV数据...</p>
    </div>
    <div v-else-if="error" class="error-container">
      <p>{{ error }}</p>
      <button @click="fetchMVs">重试</button>
    </div>
    <div v-else-if="mvs.length === 0" class="empty-container">
      <p>暂无MV数据</p>
    </div>
    <div v-else class="mv-list">
      <div v-for="mv in mvs" :key="mv.id" class="mv-item" @click="goToMVDetail(mv.id)">
        <div class="mv-cover-container">
          <img :src="mv.cover || '/default-mv-cover.jpg'" :alt="mv.name" class="mv-cover" />
          <div class="mv-overlay">
            <div class="mv-play-btn" @click.stop="playMV(mv)">
              <svg class="mv-play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
              </svg>
            </div>
          </div>
        </div>
        <div class="mv-title">{{ mv.name || '未知MV' }}</div>
        <div class="mv-artist">{{ mv.artist || mv.artistName || '未知歌手' }}</div>
        <div class="mv-stats">
          <span class="mv-views">{{ formatViews(mv.playCount || mv.viewCount || 0) }}</span>
          <span class="mv-date">{{ formatDate(mv.publishTime || mv.createTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMVList } from '@/api/mv.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const mvs = ref([])
const loading = ref(true)
const error = ref(null)

// 获取MV列表
const fetchMVs = async () => {
  try {
    loading.value = true
    error.value = null
    
    const response = await getMVList(1, 50) // 获取前50个MV
    
    // 处理API返回的数据
    if (response && response.code === 200) {
      if (response.data && response.data.content) {
        mvs.value = response.data.content
      } else if (response.data && Array.isArray(response.data)) {
        mvs.value = response.data
      } else {
        mvs.value = []
      }
    } else {
      mvs.value = []
      error.value = response?.message || '获取MV数据失败'
    }
    
    // CONSOLE LOG REMOVED: console.log('🎥 MV数据加载完成:', mvs.value.length, mvs.value)
    
  } catch (err) {
    error.value = '获取MV列表失败，请重试'
    // CONSOLE LOG REMOVED: console.error('获取MV列表失败:', err)
    
    // 网络错误已在httpUtils.js中处理
    let shouldShowError = true
    
    if (err.message === 'Network Error' || err.code === 'ECONNABORTED' || err.code === 'ECONNREFUSED') {
      shouldShowError = false
    }
    
    if (shouldShowError) {
      ElMessage.error('获取MV列表失败')
    }
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchMVs()
})

// 跳转MV详情，遵循路由导航规范
function goToMVDetail(id) {
  const targetPath = `/mv/${id}`
  // 避免重复导航
  if (router.currentRoute.value.path !== targetPath) {
    router.push(targetPath).catch(err => {
      // 忽略导航重复错误
      if (err.name !== 'NavigationDuplicated') {
        // CONSOLE LOG REMOVED: console.error('路由导航错误:', err)
      }
    })
  }
}

// 播放MV
const playMV = async (mv) => {
  try {
    // CONSOLE LOG REMOVED: console.log('📺 准备播放MV:', mv.name)
    ElMessage.success(`开始播放MV: ${mv.name}`)
    // 这里可以添加实际的播放逻辑
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('播放MV错误:', error)
    ElMessage.error('播放失败: ' + error.message)
  }
}

// 格式化播放量
const formatViews = (count) => {
  if (!count || count === 0) return '0'
  if (count < 10000) return count.toString()
  if (count < 100000000) return (count / 10000).toFixed(1) + '万'
  return (count / 100000000).toFixed(1) + '亿'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '2025-09-04'
  try {
    const date = new Date(dateString)
    return date.toISOString().split('T')[0]
  } catch {
    return '2025-09-04'
  }
}
</script>

<style scoped>
.mv-page {
  width: 1280px;
  margin: 0 auto;
  min-height: 600px;
  background: var(--background);
}
.mv-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  padding: 20px 0;
}

.mv-item {
  background: transparent;
  border-radius: 0;
  box-shadow: none;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
}

/* 移除卡片整体的悬停效果 */
.mv-cover-container {
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  margin-bottom: 12px;
  width: 100%;
  aspect-ratio: 16/9;
}

.mv-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.mv-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.mv-cover-container:hover .mv-overlay {
  opacity: 1;
}

.mv-cover-container:hover .mv-cover {
  transform: scale(1.05);
}

.mv-play-btn {
  width: 68px;
  height: 68px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.mv-play-btn:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
  box-shadow: 0 0 25px rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 1);
}

.mv-play-icon-svg {
  width: 24px;
  height: 24px;
  color: #1f2937;
  transition: all 0.3s ease;
}
.mv-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-align: left;
}

.mv-artist {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 6px;
  text-align: left;
  cursor: pointer;
}

.mv-artist:hover {
  color: var(--primary);
}

.mv-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-tertiary);
  text-align: left;
}

.mv-views::after {
  content: "•";
  margin-left: 8px;
  color: var(--text-tertiary);
}

.mv-date {
  color: var(--text-tertiary);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .mv-list {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .mv-list {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
  }
  
  .mv-title {
    font-size: 14px;
  }
  
  .mv-artist {
    font-size: 13px;
  }
  
  .mv-stats {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .mv-list {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .mv-play-btn {
    width: 60px;
    height: 60px;
  }
  
  .mv-play-icon-svg {
    width: 20px;
    height: 20px;
  }
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #000000;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

.loading-container p,
.error-container p,
.empty-container p {
  color: #000000;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  margin: 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-container button {
  margin-top: 12px;
  padding: 8px 16px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.error-container button:hover {
  background: var(--primary-dark);
}

/* 黑色主题下的加载文字样式 */
[data-theme="black"] .loading-container,
[data-theme="black"] .error-container,
[data-theme="black"] .empty-container {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}

[data-theme="black"] .loading-container p,
[data-theme="black"] .error-container p,
[data-theme="black"] .empty-container p {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}
</style>