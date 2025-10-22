// 歌手列表页面
<template>
  <div class="artist-page">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载歌手数据...</p>
    </div>
    <div v-else-if="error" class="error-container">
      <p>{{ error }}</p>
      <button @click="fetchArtists">重试</button>
    </div>
    <div v-else-if="artists.length === 0" class="empty-container">
      <p>暂无歌手数据</p>
    </div>
    <div v-else class="artist-list">
      <div v-for="artist in artists" :key="artist.id" class="artist-item" @click="goDetail(artist.id)">
        <div class="artist-avatar-container">
          <img :src="artist.avatar || '/default-avatar.jpg'" :alt="artist.name" class="artist-avatar" />
        </div>
        <div class="artist-name">{{ artist.name || '未知歌手' }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArtistList } from '@/api/artist.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const artists = ref([])
const loading = ref(true)
const error = ref(null)

// 获取歌手列表 - 保持原始数据库顺序
const fetchArtists = async () => {
  try {
    loading.value = true
    error.value = null
    
    const response = await getArtistList(1, 50) // 获取前50个歌手
    
    // 处理API返回的数据
    if (response && response.code === 200) {
      if (response.data && response.data.content) {
        artists.value = response.data.content
      } else if (response.data && Array.isArray(response.data)) {
        artists.value = response.data
      } else {
        artists.value = []
      }
    } else {
      artists.value = []
      error.value = response?.message || '获取歌手数据失败'
    }
    
    console.log('🎤 歌手数据加载完成 (保持原始数据库顺序):', artists.value.length, artists.value)
    
  } catch (err) {
    error.value = '获取歌手列表失败，请重试'
    console.error('获取歌手列表失败:', err)
    
    // 根据项目规范，网络错误已在httpUtils.js中处理
    let shouldShowError = true
    
    if (err.message === 'Network Error' || err.code === 'ECONNABORTED' || err.code === 'ECONNREFUSED') {
      shouldShowError = false
    }
    
    if (shouldShowError) {
      ElMessage.error('获取歌手列表失败')
    }
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchArtists()
})

function goDetail(id) {
  const targetPath = `/artist/${id}`
  // 避免重复导航
  if (router.currentRoute.value.path !== targetPath) {
    router.push(targetPath).catch(err => {
      // 忽略导航重复错误
      if (err.name !== 'NavigationDuplicated') {
        console.error('路由导航错误:', err)
      }
    })
  }
}
</script>

<style scoped>
.artist-page {
  width: 1280px;
  margin: 0 auto;
  min-height: 600px;
  background: var(--background);
}
.artist-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 40px;
  padding: 20px;
  max-width: 1280px;
  margin: 0 auto;
}

.artist-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

/* 移除整体悬停效果，只保留头像变化 */

.artist-avatar-container {
  position: relative;
  margin-bottom: 16px;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--background-light, #f5f5f5);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.artist-avatar-container:hover {
  transform: scale(1.05);
}

.artist-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  transition: none; /* 移除图片的变换效果 */
  display: block;
}

.artist-name {
  font-size: 18px;
  font-weight: 500;
  color: var(--text-primary);
  text-align: center;
  max-width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  /* 确保名字完全静态，不受任何悬停影响 */
  transition: none;
  transform: none;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .artist-page {
    width: 100%;
    max-width: 1200px;
    padding: 0 20px;
  }
  
  .artist-list {
    grid-template-columns: repeat(4, 1fr);
    gap: 32px;
  }
  
  .artist-avatar-container {
    width: 150px;
    height: 150px;
  }
  
  .artist-name {
    max-width: 150px;
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .artist-list {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    padding: 15px;
  }
  
  .artist-avatar-container {
    width: 120px;
    height: 120px;
  }
  
  .artist-name {
    font-size: 14px;
    max-width: 120px;
  }
}

@media (max-width: 480px) {
  .artist-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    padding: 10px;
  }
  
  .artist-avatar-container {
    width: 100px;
    height: 100px;
    margin-bottom: 12px;
  }
  
  .artist-name {
    font-size: 13px;
    max-width: 100px;
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