<template>
  <div class="detail-page">
    <div class="loading" v-if="loading">
      <i class="el-icon-loading"></i>
      <span>正在加载MV详情...</span>
    </div>
    
    <div v-else-if="mv" class="mv-detail">
      <!-- MV播放器 -->
      <div class="mv-player">
        <video 
          v-if="mv.videoPath" 
          :src="mv.videoPath" 
          controls 
          :poster="mv.cover"
          class="video-player"
          @play="handlePlay"
        >
          您的浏览器不支持视频播放
        </video>
        <div v-else class="no-video">
          <img :src="mv.cover" :alt="mv.name" class="mv-cover" />
          <div class="no-video-text">暂无视频文件</div>
        </div>
      </div>
      
      <!-- MV信息 -->
      <div class="mv-info">
        <div class="mv-header">
          <div class="mv-title">{{ mv.name }}</div>
          <div class="mv-meta">
            <span class="mv-artist" v-if="mv.artist">{{ mv.artist }}</span>
            <span class="mv-stats">
              <i class="el-icon-video-camera"></i>
              播放 {{ formatPlayCount(mv.playCount) }}
            </span>
            <span class="mv-duration" v-if="mv.duration">
              <i class="el-icon-time"></i>
              {{ formatDuration(mv.duration) }}
            </span>
          </div>
        </div>
        
        <!-- MV描述 -->
        <div class="mv-description" v-if="mv.description">
          <div class="section-title">MV简介</div>
          <div class="description-content">{{ mv.description }}</div>
        </div>
        

      </div>
    </div>
    
    <div v-else class="error-message">
      <i class="el-icon-warning"></i>
      <span>MV不存在或已被删除</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMVById } from '@/api/mv.js'
import { ElMessage } from 'element-plus'

const route = useRoute()
const mv = ref(null)
const loading = ref(true)

// 获取MV详情
const fetchMvDetail = async () => {
  try {
    loading.value = true
    const mvId = route.params.id
    const response = await getMVById(mvId)
    
    if (response && response.code === 200) {
      mv.value = response.data
      
      console.log('🎬 MV详情加载完成:', mv.value)
    } else {
      ElMessage.error('获取MV详情失败')
    }
  } catch (error) {
    ElMessage.error('获取MV详情失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 格式化播放次数
const formatPlayCount = (count) => {
  if (!count) return '0'
  if (count < 10000) return count.toString()
  if (count < 100000000) return (count / 10000).toFixed(1) + '万'
  return (count / 100000000).toFixed(1) + '亿'
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 处理播放事件
const handlePlay = () => {
  // 这里可以调用API增加播放次数
  if (mv.value && mv.value.id) {
    // TODO: 调用增加播放次数的API
  }
}



onMounted(() => {
  fetchMvDetail()
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

/* 黑色主题背景样式增强 */
[data-theme="black"] .detail-page {
  background: var(--background) !important;
  transition: background 0.5s ease-in-out !important;
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

.mv-detail {
  max-width: 900px;
  margin: 0 auto;
}

.mv-player {
  margin-bottom: 32px;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.video-player {
  width: 100%;
  height: auto;
  max-height: 500px;
  background: #000;
}

.no-video {
  position: relative;
  text-align: center;
  background: #000;
  min-height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.mv-cover {
  max-width: 100%;
  max-height: 300px;
  object-fit: cover;
  opacity: 0.7;
}

.no-video-text {
  position: absolute;
  bottom: 20px;
  color: #fff;
  font-size: 18px;
  background: rgba(0, 0, 0, 0.7);
  padding: 8px 16px;
  border-radius: 4px;
}

.mv-info {
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.mv-header {
  margin-bottom: 24px;
}

.mv-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.mv-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 14px;
}

.mv-artist {
  color: var(--primary);
  font-weight: 600;
}

.mv-stats,
.mv-duration {
  display: flex;
  align-items: center;
  gap: 4px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.description-content {
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 24px;
}



.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 16px;
}

.error-message i {
  font-size: 48px;
  color: var(--warning);
}

/* 黑色主题下的文字颜色增强 */
[data-theme="black"] .mv-title,
[data-theme="black"] .section-title {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-page {
    width: 100%;
    padding: 16px;
  }
  
  .mv-detail {
    max-width: 100%;
  }
  
  .mv-title {
    font-size: 24px;
  }
  
  .mv-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>