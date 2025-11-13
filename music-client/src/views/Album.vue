// 专辑列表页面
<template>
  <div class="album-page">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载专辑数据...</p>
    </div>
    <div v-else-if="error" class="error-container">
      <p>{{ error }}</p>
      <button @click="fetchAlbums">重试</button>
    </div>
    <div v-else-if="albums.length === 0" class="empty-container">
      <p>暂无专辑数据</p>
    </div>
    <div v-else class="album-list">
      <div v-for="album in albums" :key="album.id" class="album-card" @click="goToAlbum(album.id)">
        <div class="album-cover-container">
          <img :src="album.coverImg" :alt="album.name" class="album-cover" />
          <div class="album-overlay">
            <div class="album-play-btn">
              <svg class="album-play-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
              </svg>
            </div>
          </div>
        </div>
        <div class="album-info">
          <div class="album-name">{{ album.name }}</div>
          <div class="album-artist">{{ album.artist }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'
// 使用真实API
import { getAlbumList } from '@/api/album.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const albums = ref([])
const loading = ref(true)
const error = ref(null)

const fetchAlbums = async () => {
  try {
    loading.value = true
    error.value = null
    
    // 调用真实API获取专辑数据
    const response = await getAlbumList(1, 50) // 获取前50个专辑
    
    // 处理API返回的数据
    if (response && response.code === 200) {
      if (response.data && response.data.content) {
        albums.value = response.data.content.map(album => ({
          id: album.id,
          name: album.name,
          coverImg: album.cover || '/default-album-cover.jpg',
          artist: album.artist || '未知歌手',
          description: album.description || '暂无描述'
        }))
      } else if (response.data && Array.isArray(response.data)) {
        albums.value = response.data.map(album => ({
          id: album.id,
          name: album.name,
          coverImg: album.cover || '/default-album-cover.jpg',
          artist: album.artist || '未知歌手',
          description: album.description || '暂无描述'
        }))
      } else {
        albums.value = []
      }
    } else {
      albums.value = []
      error.value = response?.message || '获取专辑数据失败'
    }
    
    // CONSOLE LOG REMOVED: console.log('🎵 专辑数据加载完成:', albums.value.length, albums.value)
    
  } catch (err) {
    error.value = '获取专辑列表失败，请重试'
    // CONSOLE LOG REMOVED: console.error('Failed to fetch albums:', err)
    
    // 根据项目规范，网络错误已在httpUtils.js中处理
    let shouldShowError = true
    
    if (err.message === 'Network Error' || err.code === 'ECONNABORTED' || err.code === 'ECONNREFUSED') {
      shouldShowError = false
    }
    
    if (shouldShowError) {
      ElMessage.error('获取专辑列表失败')
    }
  } finally {
    loading.value = false
  }
}

const goToAlbum = (id) => {
  const targetPath = `/album/${id}`
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}


onMounted(() => {
  fetchAlbums()
})
</script>

<style scoped>
.album-page {
  width: 1280px;
  margin: 0 auto;
  min-height: 600px;
  background: var(--background);
  padding: 20px;
}
.album-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 30px 20px;
  justify-items: center;
  padding: 20px 0;
}

/* 专辑卡片 - 直角设计 */
.album-card {
  width: 220px;
  background: transparent;
  border-radius: 0;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  overflow: visible;
}

/* 专辑封面容器 */
.album-cover-container {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  border-radius: 0;
  margin-bottom: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 专辑封面图片 */
.album-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: block;
}

.album-card:hover .album-cover {
  transform: scale(1.05);
}

/* 专辑遮罩层 */
.album-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.album-card:hover .album-overlay {
  opacity: 1;
}

/* 专辑播放按钮 */
.album-play-btn {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.album-play-btn:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.album-play-icon {
  width: 24px;
  height: 24px;
  color: #333;
  margin-left: 2px;
}

/* 专辑信息区域 */
.album-info {
  padding: 15px 0 20px 0;
  background: transparent;
  text-align: left;
  width: 100%;
}

.album-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.album-artist {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.loading-container,
.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
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
  width: 50px;
  height: 50px;
  border: 5px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.error-container p {
  color: #000000;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  margin-bottom: 20px;
}
.error-container button {
  padding: 8px 16px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}
.error-container button:hover {
  background: var(--primary-color-dark);
}
.empty-container p {
  color: #000000;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .album-list {
    grid-template-columns: repeat(4, 1fr);
    gap: 25px 18px;
  }
  
  .album-card {
    width: 200px;
  }
  
  .album-cover-container {
    height: 200px;
  }
}

@media (max-width: 768px) {
  .album-page {
    width: 100%;
    padding: 15px;
  }
  
  .album-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px 15px;
  }
  
  .album-card {
    width: 180px;
  }
  
  .album-cover-container {
    height: 180px;
  }
  
  .album-info {
    padding: 12px 0 15px 0;
  }
  
  .album-name {
    font-size: 15px;
  }
  
  .album-artist {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .album-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px 12px;
  }
  
  .album-card {
    width: 160px;
  }
  
  .album-cover-container {
    height: 160px;
  }
  
  .album-info {
    padding: 10px 0 12px 0;
  }
  
  .album-name {
    font-size: 14px;
  }
  
  .album-artist {
    font-size: 12px;
  }
}

/* 黑色主题样式 */
[data-theme="black"] .album-card {
  background: transparent;
}

[data-theme="black"] .album-name {
  color: var(--text-primary);
}

[data-theme="black"] .album-artist {
  color: var(--text-secondary);
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