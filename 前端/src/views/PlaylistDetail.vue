<template>
  <div class="detail-page">
    <div class="loading" v-if="loading">
      <i class="el-icon-loading"></i>
      <span>正在加载歌单详情...</span>
    </div>
    
    <div v-else-if="playlist">
      <div class="playlist-header">
        <img :src="playlist.coverUrl || '/src/assets/1音乐.png'" :alt="playlist.name" class="playlist-cover" />
        <div class="playlist-info">
          <div class="playlist-title">{{ playlist.name }}</div>
          <div class="playlist-count">播放量：{{ playlist.playCount || 0 }}</div>
          <div class="playlist-desc" v-if="playlist.description">{{ playlist.description }}</div>
        </div>
      </div>
      <div class="song-list" v-if="songs.length > 0">
        <div class="song-list-title">歌曲列表</div>
        <ul>
          <li v-for="song in songs" :key="song.id" @click="goToSong(song.id)" class="song-item">
            <span class="song-name">{{ song.name }}</span> - <span class="song-artist">{{ song.artistName || '未知歌手' }}</span>
          </li>
        </ul>
      </div>
      <div v-else class="no-songs">
        <i class="el-icon-info"></i>
        <span>该歌单暂无歌曲</span>
      </div>
    </div>
    
    <div v-else class="error-message">
      <i class="el-icon-warning"></i>
      <span>歌单不存在或已被删除</span>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPlaylistById } from '@/api/playlist.js'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const playlist = ref(null)
const songs = ref([])
const loading = ref(true)

// 获取歌单详情
const fetchPlaylistDetail = async () => {
  try {
    loading.value = true
    const playlistId = route.params.id
    
    const response = await getPlaylistById(playlistId)
    if (response && response.code === 200) {
      playlist.value = response.data
      
      console.log('🎵 歌单详情加载完成:', playlist.value)
      
      // TODO: 获取歌单中的歌曲列表
      // 这里需要根据后端API调整
      songs.value = []
    } else {
      ElMessage.error('歌单不存在')
    }
  } catch (error) {
    ElMessage.error('获取歌单详情失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 跳转到歌曲详情
const goToSong = (songId) => {
  const targetPath = `/song/${songId}`
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}

onMounted(() => {
  fetchPlaylistDetail()
})
</script>
<style scoped>
.detail-page {
  width: 1280px;
  margin: 0 auto;
  background: var(--background);
  min-height: 400px;
  padding: 32px;
  color: var(--text-primary);
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 16px;
}

.loading i {
  font-size: 24px;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error-message,
.no-songs {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 16px;
}

.error-message i {
  font-size: 48px;
  color: var(--warning);
}

.no-songs i {
  font-size: 48px;
  color: var(--text-secondary);
}

.playlist-header {
  display: flex;
  align-items: center;
  gap: 32px;
  margin-bottom: 32px;
  background: var(--background-card);
  padding: 24px;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.playlist-cover {
  width: 160px;
  height: 160px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.playlist-info {
  flex: 1;
}

.playlist-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.playlist-count {
  color: var(--text-secondary);
  font-size: 16px;
  margin-bottom: 8px;
}

.playlist-desc {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
}

.song-list-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
  color: var(--text-primary);
  border-left: 4px solid var(--primary);
  padding-left: 12px;
}

ul {
  padding-left: 0;
  list-style: none;
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.song-item {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
  font-size: 16px;
  color: var(--text-primary);
  cursor: pointer;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
}

.song-item:hover {
  background: var(--background-hover);
  color: var(--primary);
  transform: translateX(4px);
}

.song-item:last-child {
  border-bottom: none;
}

.song-name {
  font-weight: 600;
  color: var(--text-primary);
}

.song-artist {
  color: var(--primary);
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-page {
    width: 100%;
    padding: 16px;
  }
  
  .playlist-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .playlist-cover {
    width: 120px;
    height: 120px;
  }
  
  .playlist-title {
    font-size: 24px;
  }
}
</style>