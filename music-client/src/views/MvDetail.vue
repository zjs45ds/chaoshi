// MV详情页面
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
          <div class="mv-title-row">
            <div class="mv-title">{{ mv.name }}</div>
            <div class="mv-actions">
              <button class="action-btn" @click="toggleFavorite" :class="{ 'is-favorited': isFavorited }" :disabled="isToggling">
                <svg v-if="isFavorited" class="heart-icon-svg filled" viewBox="0 0 24 24" width="16" height="16">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="currentColor"/>
                </svg>
                <svg v-else class="heart-icon-svg outline" viewBox="0 0 24 24" width="16" height="16">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="none" stroke="currentColor" stroke-width="2"/>
                </svg>
                {{ isFavorited ? '已收藏' : '' }}
              </button>
            </div>
          </div>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMVById, incrementMVPlayCount } from '@/api/mv.js'
import { favoriteMv, getUserFavoriteMvs } from '@/api/favorite.js'
import { ElMessage } from 'element-plus'
import { checkLoginForAction } from '@/utils/authCheck.js'

const route = useRoute()
const mv = ref(null)
const loading = ref(true)
const isFavorited = ref(false)
const isToggling = ref(false) // 防止重复点击

// 用户管理
const getCurrentUserId = () => {
  return localStorage.getItem('userId') || localStorage.getItem('currentUserId') || '1'
}

// 加载收藏状态
const loadFavoriteStatus = async () => {
  try {
    const userId = getCurrentUserId()
    const mvId = route.params.id
    
    // CONSOLE LOG REMOVED: console.log('🔍 开始加载MV收藏状态:', { userId, mvId })
    
    // 首先尝试使用收藏状态检查API
    try {
      const { getMvFavoriteStatus } = await import('@/api/favorite.js')
      const response = await getMvFavoriteStatus(mvId, userId)
      
      // CONSOLE LOG REMOVED: console.log('📡 收藏状态API响应:', response)
      
      if (response.code === 200) {
        const favoriteStatus = response.data?.isFavorited === true || response.data === true
        isFavorited.value = favoriteStatus
        // CONSOLE LOG REMOVED: console.log('✅ 通过API获取收藏状态:', favoriteStatus)
        return
      }
    } catch (apiError) {
      // CONSOLE LOG REMOVED: console.warn('⚠️ 收藏状态API调用失败，使用备用方法:', apiError)
    }
    
    // 备用方法：通过获取用户收藏列表来检查
    try {
      const mvListResponse = await getUserFavoriteMvs(userId)
      // CONSOLE LOG REMOVED: console.log('📡 收藏列表API响应:', mvListResponse)
      
      if (mvListResponse.code === 200 && mvListResponse.data) {
        const favoriteMvIds = mvListResponse.data.map(mv => mv.id)
        const favoriteStatus = favoriteMvIds.includes(parseInt(mvId))
        isFavorited.value = favoriteStatus
        // CONSOLE LOG REMOVED: console.log('✅ 通过收藏列表检查状态:', favoriteStatus, 'MV ID:', mvId, '收藏列表:', favoriteMvIds)
      } else {
        isFavorited.value = false
        // CONSOLE LOG REMOVED: console.log('❌ 收藏列表为空或获取失败，设为未收藏')
      }
    } catch (listError) {
      // CONSOLE LOG REMOVED: console.error('❌ 获取收藏列表失败:', listError)
      isFavorited.value = false
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('❌ 获取收藏状态失败:', error)
    isFavorited.value = false
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  // 防止重复点击
  if (isToggling.value) {
    return
  }
  
  try {
    isToggling.value = true
    const userId = getCurrentUserId()
    const mvId = mv.value.id
    const originalStatus = isFavorited.value
    
    // CONSOLE LOG REMOVED: console.log('🎯 开始收藏操作:', { userId, mvId, currentStatus: originalStatus, targetAction: originalStatus ? '取消收藏' : '收藏' });
    
    const response = await favoriteMv(mvId, userId, originalStatus)
    
    if (response.code === 200) {
      const newStatus = !originalStatus
      isFavorited.value = newStatus
      ElMessage.success(newStatus ? '已收藏MV' : '已取消收藏MV')
      
      // CONSOLE LOG REMOVED: console.log('✅ 收藏操作成功:', { originalStatus, newStatus, response: response.data });
      
      // 发送收藏状态变化事件，通知我的音乐页面刷新
      window.dispatchEvent(new CustomEvent('mvFavoriteChanged', {
        detail: {
          mvId: mvId,
          isFavorited: newStatus
        }
      }))
      
      // 等待一小段时间后重新验证状态，确保持久化成功
      setTimeout(async () => {
        await loadFavoriteStatus()
        // CONSOLE LOG REMOVED: console.log('🔄 重新验证收藏状态:', isFavorited.value)
      }, 500)
      
    } else {
      // CONSOLE LOG REMOVED: console.error('❌ 收藏操作失败:', response)
      ElMessage.error(response.message || '操作失败')
      
      // 重新加载状态以确保UI与服务器同步
      await loadFavoriteStatus()
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('❌ 收藏操作异常:', error)
    
    // 如果是重复数据错误或其他数据库错误，重新加载状态
    if (error.message && (
      error.message.includes('Duplicate entry') || 
      error.message.includes('收藏') ||
      error.response?.status === 500
    )) {
      ElMessage.warning('状态可能已变更，正在刷新...')
      await loadFavoriteStatus()
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    isToggling.value = false
  }
}

// 获取MV详情
const fetchMvDetail = async () => {
  try {
    loading.value = true
    const mvId = route.params.id
    const response = await getMVById(mvId)
    
    if (response && response.code === 200) {
      mv.value = response.data
      // CONSOLE LOG REMOVED: console.log('🎬 MV详情加载完成:', mv.value)
      // 加载收藏状态
      await loadFavoriteStatus()
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

// 播放次数是否已增加（防止重复增加）
const playCountIncremented = ref(false)

// 处理播放事件
const handlePlay = async (event) => {
  // 检查登录状态
  const isLoggedIn = await checkLoginForAction('播放视频')
  if (!isLoggedIn) {
    // 未登录，暂停视频
    if (event && event.target) {
      event.target.pause()
    }
    return
  }

  // 只在第一次播放时增加播放次数
  if (mv.value && mv.value.id && !playCountIncremented.value) {
    try {
      const response = await incrementMVPlayCount(mv.value.id)
      if (response && response.code === 200) {
        playCountIncremented.value = true
        // 更新本地播放次数显示
        if (mv.value.playCount !== undefined) {
          mv.value.playCount = (mv.value.playCount || 0) + 1
        }
        // CONSOLE LOG REMOVED: console.log('✅ MV播放次数已增加:', mv.value.playCount)
      }
    } catch (error) {
      // CONSOLE LOG REMOVED: console.error('❌ 增加播放次数失败:', error)
      // 静默失败，不影响用户体验
    }
  }
}


// 页面可见性变化时刷新收藏状态
const handleVisibilityChange = () => {
  if (!document.hidden && mv.value) {
    // CONSOLE LOG REMOVED: console.log('🔄 页面重新可见，刷新收藏状态')
    loadFavoriteStatus()
  }
}

// 监听来自其他页面的收藏状态变化事件
const handleMvFavoriteChanged = (event) => {
  const { mvId, isFavorited: newStatus } = event.detail
  if (mv.value && mv.value.id == mvId) {
    // CONSOLE LOG REMOVED: console.log('🔄 收到收藏状态变化事件:', { mvId, newStatus })
    isFavorited.value = newStatus
  }
}

onMounted(() => {
  fetchMvDetail()
  
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange)
  
  // 监听收藏状态变化事件
  window.addEventListener('mvFavoriteChanged', handleMvFavoriteChanged)
  
  // 检查是否需要自动播放
  if (route.query.autoPlay === 'true') {
    // CONSOLE LOG REMOVED: console.log('🎬 检测到自动播放参数，准备自动播放视频')
    // 延迟一下确保视频元素加载完成
    setTimeout(() => {
      const videoElement = document.querySelector('.video-player')
      if (videoElement && videoElement.src) {
        // CONSOLE LOG REMOVED: console.log('▶️ 自动播放视频:', mv.value?.name)
        videoElement.play().catch(error => {
          // CONSOLE LOG REMOVED: console.warn('自动播放失败（可能需要用户交互）:', error)
          ElMessage.info('请点击播放按钮开始播放视频')
        })
      }
    }, 1500)
  }
})

// 清理事件监听器
onUnmounted(() => {
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  window.removeEventListener('mvFavoriteChanged', handleMvFavoriteChanged)
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

.mv-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.mv-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  flex: 1;
}

.mv-actions {
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

.action-btn {
  background: var(--background);
  color: var(--text-primary);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 16px !important;
  font-size: 14px !important;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s ease, background-color 0.2s ease, border-color 0.2s ease !important;
  display: flex;
  align-items: center;
  gap: 6px;
  width: auto !important;
  height: auto !important;
  line-height: 1.4 !important;
  box-sizing: border-box !important;
  flex-shrink: 0 !important;
  transform: none !important;
}

.action-btn:hover {
  background: var(--background-hover);
  border-color: var(--primary);
  color: var(--primary);
  transform: none !important;
  padding: 12px 16px !important;
  width: auto !important;
  height: auto !important;
}

.action-btn:focus, .action-btn:active {
  transform: none !important;
  padding: 12px 16px !important;
}

.action-btn.is-favorited {
  transform: none !important;
  padding: 12px 16px !important;
  width: auto !important;
  height: auto !important;
}

.action-btn.is-favorited:hover {
  transform: none !important;
  padding: 12px 16px !important;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

.heart-icon-svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.heart-icon-svg.outline {
  color: #999;
}

.heart-icon-svg.filled {
  color: #d33a31;
}

.action-btn:hover .heart-icon-svg {
  transform: scale(1.1);
}

.action-btn:hover .heart-icon-svg.outline {
  color: var(--accent);
}

.action-btn:hover .heart-icon-svg.filled {
  color: var(--accent);
}

/* 黑色主题下的收藏按钮样式 */
[data-theme="black"] .action-btn {
  background: #000000 !important;
  color: white !important;
  border: 1px solid white !important;
}

[data-theme="black"] .action-btn:hover {
  background: #1a1a1a !important;
  color: white !important;
  border-color: white !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2) !important;
}

[data-theme="black"] .heart-icon-svg.filled {
  color: #dc2626 !important; /* 红色爱心 */
}

[data-theme="black"] .heart-icon-svg.outline {
  color: white !important;
  stroke: white !important;
}

[data-theme="black"] .action-btn:hover .heart-icon-svg.outline {
  color: #dc2626 !important;
  stroke: #dc2626 !important;
}

/* 黑色主题下的页面样式 */
[data-theme="black"] .mv-detail {
  background: var(--background);
  color: var(--text-primary);
}

[data-theme="black"] .mv-header {
  background: var(--background-card);
}

[data-theme="black"] .mv-title {
  color: var(--text-primary);
}

[data-theme="black"] .mv-artist {
  color: var(--text-secondary);
}

[data-theme="black"] .mv-meta {
  color: var(--text-tertiary);
}

[data-theme="black"] .mv-description {
  color: var(--text-secondary);
}

[data-theme="black"] .play-btn {
  background: #1e40af;
  color: white;
  border: none;
}

[data-theme="black"] .play-btn:hover {
  background: #1d4ed8;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

[data-theme="black"] .section-title {
  color: var(--text-primary);
  border-left-color: var(--accent);
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