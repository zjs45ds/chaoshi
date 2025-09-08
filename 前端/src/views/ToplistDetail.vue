<template>
  <!-- 二级导航栏 -->
  <SubNav />
  
  <div class="detail-page">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载排行榜详情...</p>
    </div>
    
    <div v-else-if="toplist" class="toplist-container">
      <!-- 页面标题和日期 -->
      <div class="page-header">
        <div class="header-left">
          <h1 class="page-title">{{ toplist.name }}</h1>
          <div class="page-date">2025-07-29</div>
        </div>
        <div class="header-right">
          <a href="#" class="rules-link">榜单规则</a>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <div class="left-space"></div>
        <button class="play-all-btn">
          <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
            <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
          </svg>
          播放全部
        </button>
      </div>

      <!-- 歌曲列表 -->
      <div class="song-list-container" v-if="toplist">
        <div class="song-list-header">
          <div class="header-rank">排名</div>
          <div class="header-song">歌曲</div>
          <div class="header-singer">歌手</div>
          <div class="header-duration">时长</div>
        </div>
        
        <div class="song-list">
          <div v-for="(song, index) in songs" :key="song.id || index" class="song-item">
            <div class="song-rank">
              <span class="rank-number">{{ index + 1 }}</span>
              <div class="trend-info" v-if="index < 10">
                <span class="trend-arrow">{{ index % 2 === 0 ? '↑' : '↓' }}</span>
                <span class="trend-percent">{{ Math.floor(Math.random() * 50) + 10 }}%</span>
              </div>
            </div>
            
            <div class="song-info">
              <img :src="song.cover || song.albumCover || '/default-song-cover.jpg'" :alt="song.name" class="song-cover" />
              <div class="song-details">
                <div class="song-name">{{ song.name || '未知歌曲' }}</div>
                <div class="song-tags">
                  <span v-if="song.vip || song.isVip" class="tag vip-tag">VIP</span>
                  <span v-if="song.mv || song.mvId" class="tag mv-tag">MV</span>
                </div>
              </div>
            </div>
            
            <div class="song-artist">{{ song.artist || song.artistName || '未知歌手' }}</div>
            <div class="song-duration">{{ formatDuration(song.duration) || '03:45' }}</div>
          </div>
          
          <!-- 如果没有歌曲数据显示提示 -->
          <div v-if="songs.length === 0" class="no-songs">
            <i class="el-icon-music"></i>
            <span>该排行榜暂无歌曲数据</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="error-message">
      <i class="el-icon-warning"></i>
      <span>排行榜不存在或已被删除</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getToplistById, getToplistSongs } from '@/api/toplist.js'
import { ElMessage } from 'element-plus'
import SubNav from '@/components/SubNav.vue'

const route = useRoute()
const toplist = ref(null)
const loading = ref(true)
const songs = ref([])

// 获取排行榜详情
const fetchToplistDetail = async () => {
  try {
    loading.value = true
    const toplistId = route.params.id
    
    // 并行获取排行榜详情和歌曲列表
    const [toplistResponse, songsResponse] = await Promise.allSettled([
      getToplistById(toplistId),
      getToplistSongs(toplistId, 1, 50)
    ])
    
    // 处理排行榜详情
    if (toplistResponse.status === 'fulfilled' && toplistResponse.value && toplistResponse.value.code === 200) {
      toplist.value = toplistResponse.value.data
      console.log('🏆 排行榜详情加载完成:', toplist.value)
    } else {
      ElMessage.error('排行榜不存在')
      return
    }
    
    // 处理排行榜歌曲列表
    if (songsResponse.status === 'fulfilled' && songsResponse.value && songsResponse.value.code === 200) {
      const songsData = songsResponse.value.data
      if (songsData && songsData.content && Array.isArray(songsData.content)) {
        songs.value = songsData.content
      } else if (songsData && Array.isArray(songsData)) {
        songs.value = songsData
      } else {
        songs.value = []
      }
      console.log('🎵 排行榜歌曲加载完成:', songs.value.length, '首歌曲')
    } else {
      // 如果没有歌曲数据，使用排行榜详情中的歌曲（如果存在）
      if (toplist.value && toplist.value.songs && Array.isArray(toplist.value.songs)) {
        songs.value = toplist.value.songs
        console.log('🎵 使用排行榜详情中的歌曲:', songs.value.length, '首')
      } else {
        songs.value = []
        console.warn('⚠️ 未获取到排行榜歌曲数据')
      }
    }
    
    // 确保toplist对象包含歌曲列表
    if (toplist.value) {
      toplist.value.songs = songs.value
    }
    
  } catch (error) {
    console.error('获取排行榜详情失败:', error)
    ElMessage.error('获取排行榜详情失败')
  } finally {
    loading.value = false
  }
}

const formatDuration = (duration) => {
  if (!duration) return '03:45'
  if (typeof duration === 'string') {
    // 如果已经是格式化的字符串，直接返回
    if (duration.includes(':')) return duration
    // 如果是秒数字符串，转换为数字处理
    duration = parseInt(duration)
  }
  if (typeof duration === 'number') {
    const minutes = Math.floor(duration / 60)
    const seconds = duration % 60
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  }
  return '03:45'
}

onMounted(() => {
  fetchToplistDetail()
})
</script>

<style scoped>
.detail-page {
  width: 1280px;
  margin: 0 auto;
  background: var(--background);
  min-height: 600px;
  padding: 32px;
}

.loading-container,
.error-message {
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
.error-message span {
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

/* 黑色主题下的ToplistDetail页面样式 */
[data-theme="black"] .detail-page {
  background: var(--background) !important;
}

.toplist-container {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.page-date {
  font-size: 14px;
  color: #666;
}

.rules-link {
  color: #22c55e;
  text-decoration: none;
  font-size: 14px;
}

.rules-link:hover {
  text-decoration: underline;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.left-space {
  flex: 1;
}

.play-all-btn {
  background: #22c55e;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.play-all-btn:hover {
  background: #16a34a;
  transform: translateY(-1px);
}

.action-btn {
  background: var(--background-card);
  color: var(--text-primary);
  border: 1px solid var(--border);
  padding: 12px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.song-list-container {
  background: var(--background-card);
  border-radius: 8px;
  overflow: hidden;
}

.song-list-header {
  display: grid;
  grid-template-columns: 80px 1fr 120px 80px;
  gap: 16px;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e5e7eb;
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.song-list {
  display: flex;
  flex-direction: column;
}

.song-item {
  display: grid;
  grid-template-columns: 80px 1fr 120px 80px;
  gap: 16px;
  padding: 16px 20px;
  align-items: center;
  border-bottom: 1px solid #f3f4f6;
  transition: all 0.2s;
  cursor: pointer;
}

.song-item:hover {
  background: #f9fafb;
}

.song-rank {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.rank-number {
  font-size: 18px;
  font-weight: bold;
  color: #dc2626;
}

.trend-info {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  color: #22c55e;
}

.trend-arrow {
  font-size: 10px;
}

.trend-percent {
  font-weight: 600;
}

.song-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.song-cover {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
}

.song-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.song-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.song-tags {
  display: flex;
  gap: 6px;
}

.tag {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  color: white;
}

.vip-tag {
  background: #22c55e;
}

.mv-tag {
  background: #22c55e;
}

.song-artist {
  font-size: 14px;
  color: #666;
  text-align: center;
}

.song-duration {
  font-size: 14px;
  color: #666;
  text-align: center;
}

.not-found {
  text-align: center;
  padding: 60px;
  color: #666;
  font-size: 18px;
}

.no-songs {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #666;
  font-size: 16px;
  gap: 12px;
}

.no-songs i {
  font-size: 48px;
  color: #ddd;
}
</style>