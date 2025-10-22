// 排行榜页面
<template>
  <div class="toplist-page">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载排行榜...</p>
    </div>
    <div v-else-if="toplists.length === 0" class="empty-container">
      <p>暂无排行榜数据</p>
    </div>
    <div v-else class="toplist-list">
      <div v-for="top in toplists" :key="top.id" class="toplist-item" @click="goToToplist(top.id)">
        <div class="toplist-cover-container">
          <img :src="top.cover || '/default-toplist-cover.jpg'" :alt="top.name" class="toplist-cover" />
          <div class="toplist-overlay">
            <div class="toplist-play-btn" @click.stop="playToplist(top)">
              <svg class="toplist-play-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M8 5v14l11-7z"/>
              </svg>
            </div>
          </div>
        </div>
        <div class="toplist-name">{{ top.name }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getToplistList } from '@/api/toplist.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const toplists = ref([])
const loading = ref(true)

const goToToplist = (id) => {
  router.push(`/toplist/${id}`)
}

// 播放排行榜
const playToplist = async (toplist) => {
  try {
    console.log('🏆 准备播放排行榜:', toplist.name)
    ElMessage.success(`开始播放排行榜: ${toplist.name}`)
    // 这里可以添加实际的播放逻辑
  } catch (error) {
    console.error('播放排行榜错误:', error)
    ElMessage.error('播放失败: ' + error.message)
  }
}

// 加载排行榜数据
async function loadToplists() {
  try {
    loading.value = true
    const response = await getToplistList()
    
    // 处理API返回的数据
    if (response && response.code === 200) {
      if (response.data && Array.isArray(response.data)) {
        toplists.value = response.data
      } else if (response.data && response.data.content && Array.isArray(response.data.content)) {
        toplists.value = response.data.content
      } else {
        toplists.value = []
      }
    } else {
      toplists.value = []
      ElMessage.error(response?.message || '获取排行榜数据失败')
    }
    
    console.log('🏆 排行榜数据加载完成:', toplists.value.length, toplists.value)
    
  } catch (error) {
    console.error('加载排行榜数据失败:', error)
    
    // 根据项目规范处理错误
    let shouldShowError = true
    
    if (error.message === 'Network Error' || error.code === 'ECONNABORTED' || error.code === 'ECONNREFUSED') {
      shouldShowError = false
    }
    
    if (shouldShowError) {
      ElMessage.error('加载排行榜数据失败，请刷新页面')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadToplists()
})
</script>

<style scoped>
.toplist-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  min-height: 600px;
  padding: 24px;
  background: var(--background-card);
}
.toplist-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.toplist-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

/* 移除卡片整体的悬停效果，只保留封面变化 */

.toplist-cover-container {
  position: relative;
  margin-bottom: 20px;
  width: 360px;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--background-light, #f5f5f5);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  transition: none;
}


.toplist-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  transition: transform 0.3s ease;
  display: block;
}

.toplist-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  border-radius: 12px;
  z-index: 2;
  pointer-events: none;
}

.toplist-cover-container:hover .toplist-overlay {
  opacity: 1;
  pointer-events: auto;
  background: rgba(0, 0, 0, 0.3);
}

.toplist-cover-container:hover .toplist-cover {
  transform: scale(1.05);
}

.toplist-play-btn {
  width: 68px;
  height: 68px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.9);
  pointer-events: auto;
  z-index: 3;
  position: relative;
}

.toplist-play-btn:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.15);
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 1);
}

.toplist-play-icon-svg {
  width: 28px;
  height: 28px;
  color: #333;
  transition: all 0.3s ease;
}

.toplist-play-btn:hover .toplist-play-icon-svg {
  color: #333;
  transform: scale(1.05);
}

.toplist-name {
  font-size: 18px;
  font-weight: 500;
  color: var(--text-primary);
  text-align: center;
  max-width: 360px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  /* 确保名字完全静态，不受任何悬停影响 */
  transition: none;
  transform: none;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .toplist-page {
    width: 100%;
    max-width: 1200px;
    padding: 0 20px;
  }
  
  .toplist-list {
    gap: 30px;
    padding: 30px;
  }
  
  .toplist-cover-container {
    width: 320px;
    height: 180px;
  }
  
  .toplist-name {
    max-width: 320px;
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .toplist-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
    padding: 20px;
  }
  
  .toplist-cover-container {
    width: 240px;
    height: 140px;
  }
  
  /* 移动端显示播放按钮 */
  .toplist-overlay {
    opacity: 0.7;
    pointer-events: auto;
    background: rgba(0, 0, 0, 0.2);
  }
  
  .toplist-play-btn {
    width: 56px;
    height: 56px;
  }
  
  .toplist-play-icon-svg {
    width: 24px;
    height: 24px;
  }
  
  .toplist-name {
    font-size: 15px;
    max-width: 240px;
  }
}

@media (max-width: 480px) {
  .toplist-list {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 15px;
  }
  
  .toplist-cover-container {
    width: 320px;
    height: 180px;
    margin-bottom: 16px;
  }
  
  .toplist-name {
    font-size: 16px;
    max-width: 320px;
  }
}

.loading-container,
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

/* 黑色主题下的文字颜色 */
[data-theme="black"] .loading-container,
[data-theme="black"] .empty-container {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}

[data-theme="black"] .loading-container p,
[data-theme="black"] .empty-container p {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}
</style>