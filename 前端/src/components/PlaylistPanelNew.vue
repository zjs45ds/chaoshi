// 播放列表面板 
<template>
  <div class="playlist-panel" :class="{ 'is-open': isOpen, 'fullscreen-mode': fullscreenMode }">
    <!-- 面板背景 -->
    <div class="panel-overlay" @click="closePanel"></div>
    
    <!-- 面板内容 -->
    <div class="panel-content">
      <!-- 面板头部 -->
      <div class="panel-header">
        <div class="header-left">
          <h3 class="panel-title">播放列表</h3>
          <span class="playlist-count">({{ playlist.length }})</span>
        </div>
        
        <div class="header-right">
          <button class="header-btn text-btn favorite-all-btn" @click="handleFavoriteAll" :disabled="playlist.length === 0" :title="favoriteAllButtonText" :class="{ 'all-favorited': allSongsFavorited }">
            <img v-if="allSongsFavorited" class="favorite-all-icon-img" src="/src/assets/已收藏.png" alt="已收藏" />
            <i v-else class="icon-heart"></i>
            <span class="btn-text">{{ favoriteAllButtonText }}</span>
          </button>
          
          <button class="header-btn text-btn clear-btn" @click="handleClearPlaylist" :disabled="playlist.length === 0" :title="'清空'">
            <span class="btn-icon">🗑</span>
            <span class="btn-text">清空</span>
          </button>
        </div>
      </div>
      
      <!-- 播放列表内容 -->
      <div class="playlist-content">
        <!-- 空状态 -->
        <div class="empty-state" v-if="playlist.length === 0">
          <div class="empty-icon">
            <i class="icon-music-empty"></i>
          </div>
          <div class="empty-text">播放列表为空</div>
          <div class="empty-hint">添加喜欢的音乐开始享受吧</div>
        </div>
        
        <!-- 歌曲列表 -->
        <div class="songs-list" v-else>
          <div 
            class="song-item"
            v-for="(song, index) in playlist" 
            :key="song.id || index"
            :class="{ 
              'is-current': index === currentIndex,
              'is-playing': index === currentIndex && isPlaying 
            }"
            @click="playSongAtIndex(index)"
          >
            <!-- 播放状态指示器 -->
            <div class="play-indicator">
              <span class="track-number">{{ String(index + 1).padStart(2, '0') }}</span>
              <div class="playing-animation" v-if="index === currentIndex && isPlaying">
                <div class="wave-bar" v-for="i in 3" :key="i"></div>
              </div>
            </div>
            
            <!-- 歌曲封面 -->
            <div class="song-cover">
              <img 
                :src="song.cover || song.albumCover || '/src/assets/1音乐.png'" 
                :alt="song.name"
                @error="handleImageError"
              />
            </div>
            
            <!-- 歌曲信息 -->
            <div class="song-info">
              <div class="song-name" :title="song.name">{{ song.name }}</div>
              <div class="song-artist" :title="song.artist">{{ song.artist }}</div>
            </div>
            
            <!-- 歌曲时长 -->
            <div class="song-duration">
              {{ formatDuration(song.duration) }}
            </div>
            
            <!-- 操作按钮 -->
            <div class="song-actions">
              <button class="action-btn love-btn" @click.stop="toggleLove(song)" :class="{ 'is-loved': isLoved(song.id) }">
                <img v-if="isLoved(song.id)" class="love-icon-img" src="/src/assets/已收藏.png" alt="已收藏" />
                <i v-else class="icon-heart"></i>
              </button>
              
              <button class="action-btn remove-btn" @click.stop="removeFromPlaylist(index)">
                <span class="btn-icon">🗑</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  isPlaying, currentIndex, playlist, playMode, playModeText, togglePlayMode, formatTime, clearPlaylist
} from '@/utils/musicPlayer.js'
import { isSongLiked, toggleSongLike } from '@/utils/favoriteManager.js'

const props = defineProps({
  isOpen: { type: Boolean, default: false },
  fullscreenMode: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'play-song'])

// 记录最近一次批量收藏的歌曲ID列表
const lastBatchFavoritedSongs = ref([])
// 强制更新的触发器
const forceUpdate = ref(0)

// 键盘事件处理
const handleKeydown = (event) => {
  if (props.isOpen && event.key === 'Escape') {
    emit('close')
  }
}

// 监听收藏状态变化
onMounted(() => {
  const handleSongLikeChanged = () => {
    // 触发组件重新渲染
    forceUpdate.value++
  }
  window.addEventListener('songLikeChanged', handleSongLikeChanged)
  
  // 添加键盘事件监听
  document.addEventListener('keydown', handleKeydown)
  
  // 清理函数
  onBeforeUnmount(() => {
    window.removeEventListener('songLikeChanged', handleSongLikeChanged)
    document.removeEventListener('keydown', handleKeydown)
  })
})

// 计算属性
const totalDuration = computed(() => {
  if (playlist.value.length === 0) return '00:00'
  
  const totalSeconds = playlist.value.reduce((total, song) => {
    return total + (song.duration || 0)
  }, 0)
  
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  
  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  } else {
    return `${minutes}:${seconds.toString().padStart(2, '0')}`
  }
})

// 检查是否所有歌曲都已收藏
const allSongsFavorited = computed(() => {
  // 使用forceUpdate触发响应式更新
  forceUpdate.value
  if (playlist.value.length === 0) return false
  return playlist.value.every(song => isSongLiked(song.id))
})

// 收藏全部按钮的文本
const favoriteAllButtonText = computed(() => {
  if (playlist.value.length === 0) return '收藏全部'
  if (allSongsFavorited.value) {
    return '取消收藏'
  }
  return '收藏全部'
})

// 监听播放列表变化，清空批量收藏记录
watch(
  () => playlist.value.length,
  (newLength, oldLength) => {
    // 如果播放列表长度发生变化（歌曲被添加或移除），清空批量收藏记录
    if (newLength !== oldLength) {
      lastBatchFavoritedSongs.value = []
    }
  }
)

// 方法
const getModeIcon = () => {
  const modeIcons = { sequence: 'icon-sequence', loop: 'icon-loop-one', shuffle: 'icon-shuffle' }
  return modeIcons[playMode.value] || 'icon-sequence'
}

const closePanel = () => emit('close')
const playSongAtIndex = (index) => emit('play-song', index)

// 收藏全部歌曲
const handleFavoriteAll = async () => {
  if (playlist.value.length === 0) return
  
  // 检查是否所有歌曲都已收藏
  const allFavorited = playlist.value.every(song => isSongLiked(song.id))
  
  if (allFavorited) {
    // 如果全部已收藏，则取消所有已收藏的歌曲
    const favoritedSongs = playlist.value.filter(song => isSongLiked(song.id))
    
    if (favoritedSongs.length === 0) {
      console.log('没有已收藏的歌曲')
      return
    }
    
    try {
      let successCount = 0
      let failCount = 0
      
      console.log(`开始取消收藏 ${favoritedSongs.length} 首歌曲...`)
      
      // 逐个取消收藏歌曲
      for (let i = 0; i < favoritedSongs.length; i++) {
        const song = favoritedSongs[i]
        
        try {
          const result = await toggleSongLike(song)
          if (!result) {
            successCount++
            console.log(`✓ 已取消收藏：${song.name} - ${song.artist} (${i + 1}/${favoritedSongs.length})`)
          } else {
            failCount++
            console.log(`✗ 取消收藏失败：${song.name} - ${song.artist}`)
          }
        } catch (error) {
          failCount++
          console.error(`取消收藏失败：${song.name}`, error)
        }
        
        // 添加小延迟避免请求过于频繁
        if (i < favoritedSongs.length - 1) {
          await new Promise(resolve => setTimeout(resolve, 150))
        }
      }
      
      // 清空批量收藏记录
      lastBatchFavoritedSongs.value = []
      
      console.log(`取消收藏操作完成：成功 ${successCount} 首，失败 ${failCount} 首`)
      
    } catch (error) {
      console.error('取消收藏全部歌曲失败:', error)
    }
  } else {
    // 如果有未收藏的歌曲，则收藏所有未收藏的歌曲
    const unlovedSongs = playlist.value.filter(song => !isSongLiked(song.id))
    
    if (unlovedSongs.length === 0) {
      console.log('全部已收藏')
      return
    }
    
    try {
      let successCount = 0
      let failCount = 0
      const newlyFavoritedSongs = []
      
      console.log(`开始收藏 ${unlovedSongs.length} 首歌曲...`)
      
      // 逐个收藏歌曲
      for (let i = 0; i < unlovedSongs.length; i++) {
        const song = unlovedSongs[i]
        
        try {
          const result = await toggleSongLike(song)
          if (result) {
            successCount++
            newlyFavoritedSongs.push(song.id)
            console.log(`✓ 已收藏：${song.name} - ${song.artist} (${i + 1}/${unlovedSongs.length})`)
          } else {
            failCount++
            console.log(`✗ 收藏失败：${song.name} - ${song.artist}`)
          }
        } catch (error) {
          failCount++
          console.error(`收藏失败：${song.name}`, error)
        }
        
        // 添加小延迟避免请求过于频繁
        if (i < unlovedSongs.length - 1) {
          await new Promise(resolve => setTimeout(resolve, 150))
        }
      }
      
      // 记录本次批量收藏的歌曲ID
      lastBatchFavoritedSongs.value = newlyFavoritedSongs
      
      console.log(`收藏操作完成：成功 ${successCount} 首，失败 ${failCount} 首`)
      
    } catch (error) {
      console.error('收藏全部歌曲失败:', error)
    }
  }
}

// 清空播放列表
const handleClearPlaylist = () => {
  if (playlist.value.length === 0) return
  
  // 确认清空操作
  if (confirm(`确定要清空播放列表吗？将移除所有 ${playlist.value.length} 首歌曲。`)) {
    clearPlaylist()
    console.log('播放列表已清空')
  }
}

const toggleLove = async (song) => {
  try {
    await toggleSongLike(song)
  } catch (error) {
    console.error('切换收藏状态失败:', error)
  }
}

const isLoved = (songId) => {
  // 使用forceUpdate触发响应式更新
  forceUpdate.value
  return isSongLiked(songId)
}

const removeFromPlaylist = (index) => {
  if (index < 0 || index >= playlist.value.length) return
  
  const song = playlist.value[index]
  
  // 确认移除操作
  if (confirm(`确定要从播放列表中移除「${song.name}」吗？`)) {
    // 如果移除的是当前播放的歌曲，需要特殊处理
    if (index === currentIndex.value) {
      // 如果是最后一首歌，停止播放
      if (playlist.value.length === 1) {
        clearPlaylist()
        return
      }
      // 否则播放下一首
      else {
        const nextIndex = index < playlist.value.length - 1 ? index : 0
        emit('play-song', nextIndex)
      }
    }
    
    // 移除歌曲
    playlist.value.splice(index, 1)
    
    // 调整当前索引
    if (index < currentIndex.value) {
      currentIndex.value--
    }
    
    console.log(`已从播放列表中移除「${song.name}」`)
  }
}
const handleImageError = (event) => { event.target.src = '/src/assets/1音乐.png' }
const formatDuration = (seconds) => formatTime(seconds)
</script>

<style scoped>
.playlist-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100000;
  visibility: hidden;
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
}

.playlist-panel.is-open {
  visibility: visible;
  opacity: 1;
  pointer-events: all;
}

.panel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  cursor: pointer;
}

.panel-content {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 80px; /* 紧挨着播放器（播放器高度为80px） */
  width: 400px;
  background: #fff;
  display: flex;
  flex-direction: column;
  transform: translateX(100%);
  transition: transform 0.3s ease;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
}

/* 全屏模式下调整播放列表位置，与控制区域紧密相接 */
.playlist-panel.fullscreen-mode .panel-content {
  bottom: 100px; /* 与全屏播放器控制区域紧密相接 */
}

.playlist-panel.is-open .panel-content {
  transform: translateX(0);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e7e7e7;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.playlist-count {
  font-size: 14px;
  color: #999;
}

.header-right {
  display: flex;
  gap: 8px;
}

.header-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  width: 32px;
  height: 32px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 14px;
}

/* 文字按钮特殊样式 */
.text-btn {
  width: auto;
  padding: 6px 12px;
  border-radius: 16px;
  border: none; /* 移除边框 */
  background: none; /* 移除背景色 */
  gap: 4px;
  white-space: nowrap;
}

.text-btn .btn-icon {
  font-size: 16px;
  opacity: 0.7;
}

.text-btn .btn-text {
  font-size: 12px;
  color: #666;
}

/* 移除所有悬停效果 */

.text-btn:disabled {
  cursor: not-allowed;
}

.text-btn:disabled .btn-text {
  color: #ccc;
}

.text-btn:disabled .btn-icon {
  opacity: 0.3;
}

.header-btn:hover:not(:disabled) {
  color: #333;
  background: #f5f5f5;
}

.header-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.playlist-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: #e7e7e7;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 14px;
  color: #999;
}

.songs-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.song-item {
  display: flex;
  align-items: center;
  padding: 8px 20px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  position: relative;
}

.song-item:hover {
  background: #f5f5f5;
}

.song-item.is-current {
  background: #f0f0f0;
}

.song-item.is-playing {
  background: #fff2f0;
}

.play-indicator {
  width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.track-number {
  font-size: 12px;
  color: #999;
}

.playing-animation {
  display: flex;
  align-items: center;
  gap: 2px;
}

.wave-bar {
  width: 3px;
  background: #d33a31;
  border-radius: 2px;
  animation: wave 1.2s ease-in-out infinite;
}

.wave-bar:nth-child(1) { height: 12px; animation-delay: 0s; }
.wave-bar:nth-child(2) { height: 8px; animation-delay: 0.2s; }
.wave-bar:nth-child(3) { height: 16px; animation-delay: 0.4s; }

@keyframes wave {
  0%, 40%, 100% { transform: scaleY(0.4); }
  20% { transform: scaleY(1); }
}

.song-cover {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.song-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.song-info {
  flex: 1;
  min-width: 0;
  margin-right: 12px;
}

.song-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 2px;
}

.song-artist {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-duration {
  font-size: 12px;
  color: #999;
  margin-right: 12px;
  flex-shrink: 0;
}

.song-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
  flex-shrink: 0;
}

.song-item:hover .song-actions {
  opacity: 1;
}

.action-btn {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 12px;
}

.action-btn:hover {
  color: #333;
  background: rgba(0, 0, 0, 0.05);
}

.action-btn.is-loved {
  color: #d33a31 !important;
}

/* 修复单首歌曲收藏按钮的红色爱心显示 */
.action-btn.love-btn.is-loved {
  color: #d33a31 !important;
}

.action-btn.love-btn.is-loved .icon-heart-filled::before,
.action-btn.love-btn.is-loved .icon-heart::before {
  filter: brightness(0) saturate(100%) invert(26%) sepia(89%) saturate(1583%) hue-rotate(343deg) brightness(97%) contrast(90%);
}

/* emoji爱心的红色样式 */
.action-btn.love-btn .emoji-heart {
  color: #d33a31;
  font-size: 14px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 删除按钮的emoji图标样式 */
.remove-btn .btn-icon {
  font-size: 16px;
  opacity: 0.7;
}

/* 图标样式 - 使用assets中的SVG图标 */
.icon-sequence::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url('@/assets/顺序播放.svg') no-repeat center;
  background-size: contain;
}

.icon-loop-one::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url('@/assets/循环播放.svg') no-repeat center;
  background-size: contain;
}

.icon-shuffle::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url('@/assets/随机播放.svg') no-repeat center;
  background-size: contain;
}

.icon-trash::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url('@/assets/展开.svg') no-repeat center;
  background-size: contain;
  transform: rotate(180deg);
}

.icon-close::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url('@/assets/left.svg') no-repeat center;
  background-size: contain;
  transform: rotate(180deg);
}

.icon-music-empty::before {
  content: '';
  display: inline-block;
  width: 48px;
  height: 48px;
  background: url('@/assets/播放列表.svg') no-repeat center;
  background-size: contain;
}

.icon-heart::before,
.icon-heart-filled::before {
  content: '';
  display: inline-block;
  width: 14px;
  height: 14px;
  background: url('@/assets/heart.svg') no-repeat center;
  background-size: contain;
}

.icon-heart-filled::before {
  content: '';
  display: inline-block;
  width: 14px;
  height: 14px;
  background: url('@/assets/heart-filled.svg') no-repeat center;
  background-size: contain;
}

/* 播放列表单首歌曲收藏按钮的PNG图片样式 */
.action-btn.love-btn .love-icon-img {
  width: 16px;
  height: 16px;
  object-fit: contain;
  flex-shrink: 0;
}

/* 收藏全部按钮的PNG图片样式 */
.favorite-all-icon-img {
  width: 16px;
  height: 16px;
  object-fit: contain;
  flex-shrink: 0;
}

/* 收藏全部按钮的特殊样式 */
.favorite-all-btn .icon-heart-filled::before {
  width: 16px;
  height: 16px;
}

/* 收藏全部按钮 - 全部已收藏状态 */
.favorite-all-btn.all-favorited .btn-icon.favorited {
  color: #d33a31; /* 红色 */
}

.favorite-all-btn .btn-icon {
  transition: color 0.2s ease;
}

.icon-remove::before {
  content: '';
  display: inline-block;
  width: 14px;
  height: 14px;
  background: url('@/assets/展开.svg') no-repeat center;
  background-size: contain;
  transform: rotate(180deg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .panel-content {
    width: 100%;
    right: 0;
    left: 0;
  }
  
  .playlist-panel.is-open .panel-content {
    transform: translateX(0);
  }
  
  /* 移动端全屏模式下也调整播放列表位置 */
  .playlist-panel.fullscreen-mode .panel-content {
    bottom: 90px; /* 移动端与控制区域紧密相接 */
  }
  
  /* 移动端普通模式下也要紧挨着播放器 */
  .panel-content {
    bottom: 80px; /* 移动端也紧挨着播放器 */
  }
}

/* 粉色主题下的播放列表样式 */
[data-theme="pink"] .panel-content {
  background: #f9a8d4 !important;
  border-left: 1px solid rgba(236, 72, 153, 0.3) !important;
  box-shadow: -4px 0 20px rgba(236, 72, 153, 0.2) !important;
}

[data-theme="pink"] .panel-title {
  color: var(--text-primary) !important;
}

[data-theme="pink"] .playlist-count {
  color: var(--text-secondary) !important;
}

[data-theme="pink"] .song-item:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

[data-theme="pink"] .song-item.is-current {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="pink"] .song-item.is-playing {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* 浅粉色主题下的播放列表样式 */
[data-theme="lightPink"] .panel-content {
  background: #f7b9c8 !important;
  border-left: 1px solid rgba(247, 185, 200, 0.8) !important;
  box-shadow: -4px 0 20px rgba(247, 185, 200, 0.5) !important;
}

[data-theme="lightPink"] .panel-title {
  color: var(--text-primary) !important;
}

[data-theme="lightPink"] .playlist-count {
  color: var(--text-secondary) !important;
}

[data-theme="lightPink"] .song-item:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

[data-theme="lightPink"] .song-item.is-current {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="lightPink"] .song-item.is-playing {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* 绿色主题下的播放列表样式 */
[data-theme="green"] .panel-content {
  background: #8dffb6 !important;
  border-left: 1px solid rgba(74, 222, 128, 0.3) !important;
  box-shadow: -4px 0 20px rgba(74, 222, 128, 0.2) !important;
}

[data-theme="green"] .panel-title {
  color: var(--text-primary) !important;
}

[data-theme="green"] .playlist-count {
  color: var(--text-secondary) !important;
}

[data-theme="green"] .song-item:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

[data-theme="green"] .song-item.is-current {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="green"] .song-item.is-playing {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* 橙色主题下的播放列表样式 */
[data-theme="orange"] .panel-content {
  background: #fdba74 !important;
  border-left: 1px solid rgba(249, 115, 22, 0.3) !important;
  box-shadow: -4px 0 20px rgba(249, 115, 22, 0.2) !important;
}

[data-theme="orange"] .panel-title {
  color: var(--text-primary) !important;
}

[data-theme="orange"] .playlist-count {
  color: var(--text-secondary) !important;
}

[data-theme="orange"] .song-item:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

[data-theme="orange"] .song-item.is-current {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="orange"] .song-item.is-playing {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* 蓝色主题下的播放列表样式 */
[data-theme="blue"] .panel-content {
  background: #93c5fd !important;
  border-left: 1px solid rgba(59, 130, 246, 0.3) !important;
  box-shadow: -4px 0 20px rgba(59, 130, 246, 0.2) !important;
}

[data-theme="blue"] .panel-title {
  color: var(--text-primary) !important;
}

[data-theme="blue"] .playlist-count {
  color: var(--text-secondary) !important;
}

[data-theme="blue"] .song-item:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

[data-theme="blue"] .song-item.is-current {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="blue"] .song-item.is-playing {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* 紫色主题下的播放列表样式 */
[data-theme="purple"] .panel-content {
  background: #c2a0d9 !important;
  border-left: 1px solid rgba(139, 92, 246, 0.3) !important;
  box-shadow: -4px 0 20px rgba(139, 92, 246, 0.2) !important;
}

[data-theme="purple"] .panel-title {
  color: var(--text-primary) !important;
}

[data-theme="purple"] .playlist-count {
  color: var(--text-secondary) !important;
}

[data-theme="purple"] .song-item:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

[data-theme="purple"] .song-item.is-current {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="purple"] .song-item.is-playing {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* 红色主题下的播放列表样式 - 优雅优化版 */
[data-theme="red"] .panel-content {
  background-color: var(--background-light) !important;
  border: 1px solid var(--border) !important;
  box-shadow: 0 0 24px rgba(255, 79, 79, 0.12) !important;
  border-radius: 12px 12px 0 0 !important;
}

[data-theme="red"] .panel-header {
  border-bottom: 1px solid var(--border) !important;
  background: linear-gradient(to bottom, rgba(255, 79, 79, 0.05), transparent) !important;
}

[data-theme="red"] .panel-title {
  color: var(--text-primary) !important;
  font-weight: 600 !important;
  letter-spacing: 0.3px !important;
}

[data-theme="red"] .playlist-count {
  color: var(--text-tertiary) !important;
  font-size: 14px !important;
}

[data-theme="red"] .song-item {
  color: var(--text-secondary) !important;
  border-bottom: 1px solid var(--border) !important;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

[data-theme="red"] .song-item:hover {
  background-color: rgba(255, 79, 79, 0.1) !important;
  transform: translateX(4px) !important;
}

[data-theme="red"] .song-item.is-current {
  background-color: rgba(255, 79, 79, 0.15) !important;
  border-left: 3px solid var(--primary) !important;
}

[data-theme="red"] .song-item.is-playing {
  background-color: rgba(255, 79, 79, 0.08) !important;
}

[data-theme="red"] .song-item.is-playing .song-name {
  color: var(--primary) !important;
  font-weight: 600 !important;
}

[data-theme="red"] .track-number {
  color: var(--text-tertiary) !important;
}

[data-theme="red"] .track-number.playing {
  color: var(--primary) !important;
}

[data-theme="red"] .song-name {
  color: var(--text-primary) !important;
  font-weight: 500 !important;
}

[data-theme="red"] .song-artist {
  color: var(--text-secondary) !important;
}

[data-theme="red"] .song-duration {
  color: var(--text-tertiary) !important;
  font-size: 12px !important;
  font-weight: 500 !important;
}

[data-theme="red"] .close-btn {
  color: var(--text-tertiary) !important;
}

[data-theme="red"] .close-btn:hover {
  color: var(--primary) !important;
}

/* 黑色主题下的播放列表样式 */
[data-theme="black"] .panel-content {
  background: #000000 !important;
  border-left: 1px solid #333333 !important;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.5) !important;
}

[data-theme="black"] .panel-title {
  color: white !important;
}

[data-theme="black"] .playlist-count {
  color: #cccccc !important;
}

[data-theme="black"] .header-btn {
  color: #cccccc !important;
  border: 1px solid #333333 !important;
}

[data-theme="black"] .header-btn:hover:not(:disabled) {
  color: white !important;
  background: #1a1a1a !important;
  border-color: #555555 !important;
}

[data-theme="black"] .header-btn.all-favorited {
  color: #dc2626 !important;
}

[data-theme="black"] .empty-state {
  color: #cccccc !important;
}

[data-theme="black"] .empty-text {
  color: white !important;
}

[data-theme="black"] .empty-hint {
  color: #888888 !important;
}

[data-theme="black"] .song-item {
  border-bottom: 1px solid #222222 !important;
}

[data-theme="black"] .song-item:hover {
  background: #1a1a1a !important;
}

[data-theme="black"] .song-item.is-current {
  background: #2a2a2a !important;
}

[data-theme="black"] .song-item.is-playing {
  background: #1a1a1a !important;
}

[data-theme="black"] .track-number {
  color: #888888 !important;
}

[data-theme="black"] .song-name {
  color: white !important;
}

[data-theme="black"] .song-artist {
  color: #cccccc !important;
}

[data-theme="black"] .song-duration {
  color: #888888 !important;
}

[data-theme="black"] .action-btn {
  background: transparent !important;
  color: #cccccc !important;
  border: 1px solid #333333 !important;
}

[data-theme="black"] .action-btn:hover {
  color: white !important;
  background: #1a1a1a !important;
  border-color: #555555 !important;
}

[data-theme="black"] .action-btn.is-loved {
  color: #dc2626 !important;
}

[data-theme="black"] .wave-bar {
  background: #dc2626 !important;
}
</style>