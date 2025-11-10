// 专辑详情页面
<template>
  <!-- 二级导航栏 -->
  <SubNav />
  
  <div class="detail-page">
    <div class="loading" v-if="loading">
      <i class="el-icon-loading"></i>
      <span>正在加载专辑详情...</span>
    </div>
    
    <div v-else-if="album">
      <!-- 顶部内容区域：封面、信息和简介 -->
      <div class="album-header-section">
        <!-- 左侧专辑封面 -->
        <div class="album-cover-section">
          <div class="album-cover">
            <img :src="album.cover || require('@/assets/1音乐.png')" :alt="album.name" class="cover-image" />
          </div>
        </div>
        
        <!-- 中间专辑信息 -->
        <div class="album-info-section">
          <div class="album-header">
            <h1 class="album-title">{{ album.name }}</h1>
            <div class="artist-info">
              <i class="artist-icon">👤</i>
              <span class="artist-name" @click="goToArtist(album.artistId)" style="cursor: pointer;">{{ artistName }}</span>
            </div>
          </div>
          
          <div class="album-meta">
            <div class="meta-item" v-if="album.releaseDate">
              <span class="meta-label">发行时间：</span>
              <span class="meta-value">{{ formatDate(album.releaseDate) }}</span>
            </div>
            
            <div class="meta-item">
              <span class="meta-label">类型：</span>
              <span class="meta-value">{{ album.type || '专辑' }}</span>
            </div>
            
            <div class="meta-item" v-if="album.trackCount">
              <span class="meta-label">总曲目：</span>
              <span class="meta-value">{{ album.trackCount }} 首</span>
            </div>
            
            <div class="meta-item" v-if="album.language">
              <span class="meta-label">语种：</span>
              <span class="meta-value">{{ album.language }}</span>
            </div>
            
            <div class="meta-item" v-if="songs.length > 0">
              <span class="meta-label">歌曲数：</span>
              <span class="meta-value">{{ songs.length }} 首</span>
            </div>
          </div>
          
          <div class="action-buttons">
            <button class="play-all-btn" @click="playAll">
              <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
              </svg>
              播放全部
            </button>
            <button class="action-btn" @click="toggleFavorite" :class="{ 'is-favorited': isFavorited }">
              <svg v-if="isFavorited" class="heart-icon-svg filled" viewBox="0 0 24 24" width="16" height="16">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="currentColor"/>
              </svg>
              <svg v-else class="heart-icon-svg outline" viewBox="0 0 24 24" width="16" height="16">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="none" stroke="currentColor" stroke-width="2"/>
              </svg>
              <span v-if="isFavorited">已收藏</span>
            </button>
          </div>
        </div>
        
        <!-- 右侧简介部分 -->
        <div class="introduction-section" ref="introContentRef">
          <h3 class="intro-title">专辑简介</h3>
          <div class="intro-wrapper">
            <div class="intro-content">
              <!-- 重新排列的专辑简介内容 -->
              <div class="intro-text-wrapper">
                <p class="intro-text">{{ getShortDescription() }}</p>
              </div>
            </div>
            <button v-if="shouldShowExpandButton" class="expand-btn" @click="openDescModal">
              <span class="expand-text">展开</span>
              <svg class="expand-icon" viewBox="0 0 24 24" width="14" height="14">
                <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" fill="currentColor"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 歌曲列表 -->
    <div class="content-section" v-if="album">
      <div class="song-list-section" v-if="songs.length > 0">
        <h3 class="section-title">歌曲列表</h3>
        <div class="song-list-container">
          <div class="song-table-header">
            <div class="table-col col-index">#</div>
            <div class="table-col col-song">歌曲</div>
            <div class="table-col col-artist">歌手</div>
            <div class="table-col col-duration">时长</div>
            <div class="table-col col-actions">操作</div>
          </div>
          <div class="song-table-body">
            <div v-for="(song, index) in songs" :key="song.id" class="song-row">
              <div class="table-col col-index">{{ index + 1 }}</div>
              <div class="table-col col-song" @click="goToSong(song.id)">
                <div class="song-info">
                  <div class="song-name">{{ song.name }}</div>
                </div>
              </div>
              <div class="table-col col-artist">
                <span class="artist-link" @click.stop="goToArtist(song.artistId)" :title="song.artistName || artistName">
                  {{ song.artistName || artistName }}
                </span>
              </div>
              <div class="table-col col-duration">{{ formatDuration(song.duration) || '03:16' }}</div>
              <div class="table-col col-actions">
                <div class="song-actions">
                  <button class="play-next-btn" @click.stop="addSongToPlayNext(song)" :title="`下一首播放 ${song.name}`">
                    <svg class="play-next-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M19 13H13V19H11V13H5V11H11V5H13V11H19V13Z" fill="currentColor"/>
                      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/>
                    </svg>
                  </button>
                  <button class="play-btn" @click.stop="playCurrentSong(song)" :title="`播放 ${song.name}`">
                    <img src="/src/assets/开始.svg" alt="播放" class="play-icon-img" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="error-message">
      <i class="el-icon-warning"></i>
      <span>专辑不存在或已被删除</span>
    </div>
    
    <!-- 专辑简介弹窗 -->
    <div v-if="isDescModalVisible" class="desc-modal-overlay" @click="closeDescModal">
      <div class="desc-modal" @click.stop>
        <div class="desc-modal-header">
          <h3>{{ album.name }} - 专辑介绍</h3>
          <button class="close-btn" @click="closeDescModal">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="currentColor"/>
            </svg>
          </button>
        </div>
        <div class="desc-modal-content">
          <div class="modal-desc-text">
            <p 
              v-for="(paragraph, index) in splitDescription(album.description)" 
              :key="index"
              class="modal-desc-paragraph"
            >
              {{ paragraph }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAlbumById, getSongsByAlbumId } from '@/api/album.js'
import { getArtistById } from '@/api/artist.js'
import { ElMessage } from 'element-plus'
import SubNav from '@/components/SubNav.vue'
import { playSong, addToPlaylist, addMultipleToPlaylist, addToPlayNext, playlist } from '@/utils/musicPlayer.js'
import { getAlbumFavoriteStatus, favoriteAlbum } from '@/api/favorite.js'

const route = useRoute()
const router = useRouter()
const album = ref(null)
const songs = ref([])
const artist = ref(null)
const loading = ref(true)
const isFavorited = ref(false)
const isIntroExpanded = ref(false)
const isDescModalVisible = ref(false)
// 添加一个ref来引用简介内容
const introContentRef = ref(null)

// 获取当前用户ID
const getCurrentUserId = () => {
  return localStorage.getItem('userId') || localStorage.getItem('currentUserId') || '6'
}

// 计算属性：歌手名称
const artistName = computed(() => {
  return artist.value?.name || '未知歌手'
})

// 计算属性：是否需要显示展开按钮
const shouldShowExpandButton = computed(() => {
  const description = album.value?.description || '暂无专辑简介'
  return description.length > 100
})

// 打开简介弹窗
const openDescModal = () => {
  isDescModalVisible.value = true
  // 阻止事件冒泡
  if (event) event.stopPropagation()
}

// 关闭简介弹窗
const closeDescModal = () => {
  isDescModalVisible.value = false
}

// 获取短版本的简介内容（显示在页面上）
const getShortDescription = () => {
  const description = album.value?.description || '暂无专辑简介'
  if (description.length <= 100) {
    return description
  }
  return description.substring(0, 100) + '...'
}

// 分段处理简介内容
const splitDescription = (description) => {
  if (!description) return ['暂无专辑介绍']
  
  // 首先处理可能存在的 \r\n
  let normalizedDesc = description.replace(/\r\n/g, '\n')
  
  // 1. 先尝试按换行符分割
  let paragraphs = normalizedDesc.split('\n')
  
  // 过滤掉空段落
  paragraphs = paragraphs.filter(paragraph => paragraph.trim().length > 0)
  
  // 2. 如果没有找到换行符或者只有一个段落，尝试智能分段
  if (paragraphs.length === 1) {
    const text = paragraphs[0]
    const smartParagraphs = []
    
    // 尝试按照标点符号和年份进行分段
    // 查找20xx年作为可能的段落分割点
    const yearMatches = text.match(/(20\d{2}|19\d{2})年/g)
    
    if (yearMatches && yearMatches.length > 1) {
      // 有多个年份，可以按年份分段
      let lastIndex = 0
      
      text.replace(/(20\d{2}|19\d{2})年/g, (match, year, index) => {
        // 如果不是第一个年份，且前面有足够的内容，则分割
        if (lastIndex < index && index - lastIndex > 10) {
          const paragraph = text.substring(lastIndex, index).trim()
          if (paragraph) {
            smartParagraphs.push(paragraph)
          }
        }
        lastIndex = index
        return match
      })
      
      // 添加最后一段
      if (lastIndex < text.length) {
        const lastParagraph = text.substring(lastIndex).trim()
        if (lastParagraph) {
          smartParagraphs.push(lastParagraph)
        }
      }
    } else {
      // 如果没有明显的年份分割点，尝试按句号和换行符分段
      const sentences = text.split(/[。！？]\s*/)
      
      // 合并短句子成段落，避免段落过多
      let currentParagraph = ''
      sentences.forEach(sentence => {
        const trimmed = sentence.trim()
        if (trimmed) {
          if (currentParagraph && currentParagraph.length + trimmed.length > 100) {
            // 当当前段落足够长时，开始新段落
            smartParagraphs.push(currentParagraph + '。')
            currentParagraph = trimmed
          } else {
            // 否则继续添加到当前段落
            currentParagraph = currentParagraph ? `${currentParagraph}。${trimmed}` : trimmed
          }
        }
      })
      
      // 添加最后一个段落
      if (currentParagraph) {
        smartParagraphs.push(currentParagraph + (currentParagraph.endsWith('。') ? '' : '。'))
      }
    }
    
    // 如果智能分段生成了多个段落，则使用智能分段结果
    if (smartParagraphs.length > 1) {
      paragraphs = smartParagraphs
    }
  }
  
  return paragraphs
}



// 在组件挂载时添加事件监听器
onMounted(async () => {
  await fetchAlbumDetail()
  await loadFavoriteStatus()
  
  // 检查是否需要自动播放
  if (route.query.autoPlay === 'true') {
    // CONSOLE LOG REMOVED: console.log('🎵 检测到自动播放参数，准备播放专辑')
    // 延迟一下确保数据加载完成
    setTimeout(() => {
      playAll()
    }, 1000)
  }
})


// 获取专辑详情
const fetchAlbumDetail = async () => {
  try {
    loading.value = true
    const albumId = route.params.id
    
    // 获取专辑信息
    const albumResponse = await getAlbumById(albumId)
    if (albumResponse && albumResponse.code === 200) {
      album.value = albumResponse.data
      
      // 获取歌手信息
      if (album.value.artistId) {
        try {
          const artistResponse = await getArtistById(album.value.artistId)
          if (artistResponse && artistResponse.code === 200) {
            artist.value = artistResponse.data
          }
        } catch (error) {
          // CONSOLE LOG REMOVED: console.warn('获取歌手信息失败:', error)
        }
      }
      
      // 获取专辑歌曲
      try {
        const songsResponse = await getSongsByAlbumId(albumId)
        if (songsResponse && songsResponse.code === 200) {
          if (songsResponse.data && songsResponse.data.content) {
            songs.value = songsResponse.data.content
          } else if (songsResponse.data && Array.isArray(songsResponse.data)) {
            songs.value = songsResponse.data
          }
        }
      } catch (error) {
        songs.value = []
      }
      
      // CONSOLE LOG REMOVED: console.log('💿 专辑详情加载完成:')
      // CONSOLE LOG REMOVED: console.log('专辑信息:', album.value)
      // CONSOLE LOG REMOVED: console.log('歌手信息:', artist.value)
      // CONSOLE LOG REMOVED: console.log('歌曲数量:', songs.value?.length || 0)
    } else {
      ElMessage.error('专辑不存在')
    }
  } catch (error) {
    ElMessage.error('获取专辑详情失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return ''
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 播放全部
const playAll = async () => {
  try {
    if (songs.value.length === 0) {
      ElMessage.warning('该专辑暂无歌曲')
      return
    }
    
    // CONSOLE LOG REMOVED: console.log('🎵 准备播放专辑:', album.value.name, songs.value.length, '首歌曲')
    
    
    const added = addMultipleToPlaylist(songs.value, true)
    
    if (added) {
    
      // CONSOLE LOG REMOVED: console.log('📋 当前播放列表长度:', playlist.value.length)
      ElMessage.success(`开始播放专辑《${album.value.name}》`)
    } else {
      ElMessage.error('播放失败，请稍后重试')
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('播放专辑失败:', error)
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 播放单首歌曲
const playCurrentSong = async (song) => {
  try {
    // CONSOLE LOG REMOVED: console.log('🎵 准备播放歌曲:', song.name)
    
    const added = addToPlaylist(song, true)
    
    if (added) {
      // CONSOLE LOG REMOVED: console.log('✅ 歌曲已添加到播放列表:', song.name)
      ElMessage.success(`开始播放: ${song.name}`)
    } else {
      ElMessage.error('播放失败')
    }
  } catch (error) {
   
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 添加歌曲到下一首播放
const addSongToPlayNext = async (song) => {
  try {
    // CONSOLE LOG REMOVED: console.log('⏭️ 准备将歌曲添加到下一首播放:', song.name)
    
    const added = addToPlayNext(song)
    
    if (added) {
     
      ElMessage.success(`${song.name} 已添加到下一首播放`)
    } else {
      ElMessage.error('添加失败')
    }
  } catch (error) {
  
    ElMessage.error('添加失败，请稍后重试')
  }
}

// 加载收藏状态
const loadFavoriteStatus = async () => {
  if (!album.value) return
  
  try {
    const userId = getCurrentUserId()
    const response = await getAlbumFavoriteStatus(album.value.id, userId)
    if (response.code === 200) {
      isFavorited.value = response.data.isFavorited || false
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('获取专辑收藏状态失败:', error)
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!album.value) {
    ElMessage.warning('专辑信息不存在')
    return
  }

  try {
    const userId = getCurrentUserId()
    const response = await favoriteAlbum(album.value.id, userId, isFavorited.value)
    
    if (response.code === 200) {
      isFavorited.value = response.data.isFavorited
      const message = isFavorited.value ? '已收藏专辑' : '已取消收藏专辑'
      ElMessage.success(message)
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('专辑收藏操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 跳转到歌手详情
const goToArtist = (artistId) => {
  if (artistId) {
    const targetPath = `/artist/${artistId}`
    if (route.path !== targetPath) {
      router.push(targetPath)
    }
  }
}

// 跳转到歌曲详情
const goToSong = (songId) => {
  const targetPath = `/song/${songId}`
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}

</script>
<style scoped>
.detail-page { 
  width: 1280px; 
  margin: 0 auto; 
  background-color: var(--background); 
  min-height: 400px; 
  padding: 32px; 
  animation: fadein 0.5s; 
  color: var(--text-primary);
  position: relative;
}

@keyframes fadein { 
  from { opacity: 0; } 
  to { opacity: 1; } 
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

/* 顶部内容区域布局 */
  .album-header-section {
    display: flex;
    gap: 40px;
    margin-bottom: 40px;
    align-items: flex-start;
    position: relative;
  }
  
  /* 专辑封面部分 */
  .album-cover-section {
    flex: 0 0 300px;
  }
  
  /* 专辑信息部分 */
  .album-info-section {
    flex: 1;
    min-width: 0;
    max-width: 100%;
  }
  
  /* 简介部分样式 */
  .introduction-section {
    flex: 0 0 600px;
    background: var(--background-card);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    border: 1px solid var(--border);
    height: fit-content;
    position: sticky;
    top: 20px;
  }

/* 专辑封面样式 */
  .album-cover {
    position: relative;
    width: 300px;
    height: 300px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  /* 黑色主题下的播放按钮样式 */
[data-theme="black"] .play-all-btn {
  background: #000000 !important;
  color: white !important;
  border: 1px solid white !important;
}

[data-theme="black"] .play-all-btn:hover {
  background: #1a1a1a !important;
  border-color: white !important;
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2) !important;
}

[data-theme="black"] .play-all-btn {
  background: #000000 !important;
  color: white !important;
  border: 1px solid white !important;
}

[data-theme="black"] .play-all-btn:hover {
  background: #1a1a1a !important;
  border-color: white !important;
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2) !important;
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

/* 黑色主题下的爱心图标样式 */
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



/* 黑色主题下的歌曲操作按钮样式 */
[data-theme="black"] .play-next-btn,
[data-theme="black"] .play-btn {
  background: #000000 !important;
  color: white !important;
  border: 1px solid white !important;
}

[data-theme="black"] .play-next-btn:hover,
[data-theme="black"] .play-btn:hover {
  background: #1a1a1a !important;
  border-color: white !important;
}
[data-theme="black"] .album-cover {
  box-shadow: 0 8px 32px rgba(107, 114, 128, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

[data-theme="black"] .song-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.album-info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.album-header {
  margin-bottom: 24px;
}

.album-title {
  font-size: 32px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 12px 0;
}

.artist-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: var(--text-secondary);
}

.artist-icon {
  font-size: 16px;
}

.artist-name {
  font-weight: 500;
  color: var(--text-primary);
}

.album-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.meta-label {
  color: var(--text-tertiary);
  font-weight: 500;
  min-width: 80px;
}

.meta-value {
  color: var(--primary);
  font-weight: 600;
  transition: color var(--transition-normal);
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.play-all-btn {
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.play-all-btn:hover {
  background: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.3);
}

.play-icon-svg {
  width: 14px;
  height: 14px;
  transition: all 0.3s ease;
}

.play-all-btn:hover .play-icon-svg {
  transform: scale(1.1);
}

.action-btn {
  background: var(--background);
  color: var(--text-primary);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 16px !important; /* 与歌曲详情页面action-btn相同的padding */
  font-size: 14px !important;
  font-weight: 600; /* 与播放全部按钮相同的字重 */
  cursor: pointer;
  transition: color 0.2s ease, background-color 0.2s ease, border-color 0.2s ease !important;
  display: flex;
  align-items: center;
  gap: 6px; /* 与歌曲详情页面action-btn相同的gap */
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

/* 移除收藏状态下的背景颜色变化，保持框架不变 */

/* SVG心形图标样式 */
.heart-icon-svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

/* 未收藏状态的轮廓图标 */
.heart-icon-svg.outline {
  color: var(--text-tertiary); /* 灰色轮廓 */
}

/* 已收藏状态的填充图标 */
.heart-icon-svg.filled {
  color: var(--accent); /* 红色填充 */
}

/* 悬浮时的效果 */
.action-btn:hover .heart-icon-svg {
  transform: scale(1.1);
}

/* 悬浮时图标颜色变化 */
.action-btn:hover .heart-icon-svg.outline {
  color: var(--accent); /* 悬浮时轮廓变红 */
}

.action-btn:hover .heart-icon-svg.filled {
  color: var(--accent); /* 悬浮时保持红色 */
}

/* 移除不再需要的样式，因为现在使用SVG图标 */

/* 移除收藏状态的特殊悬浮效果，使用通用悬浮样式 */

/* 黑色主题下的收藏按钮样式 */
[data-theme="black"] .action-btn {
  background: #1e40af;
  color: white;
  border: 1px solid #3b82f6;
}

[data-theme="black"] .action-btn:hover {
  background: #1d4ed8;
  color: white;
  border-color: #60a5fa;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

[data-theme="black"] .heart-icon-svg {
  color: white !important;
}


  
  /* 响应式设计 - 平板和较小屏幕 */
  @media (max-width: 1200px) {
    .album-header-section {
      flex-wrap: wrap;
    }
    
    .introduction-section {
      flex: 1 1 100%;
      margin-top: 20px;
    }
    
    .intro-content:not(.expanded) {
      max-height: 100px;
    }
  }
  
  /* 响应式设计 - 手机屏幕 */
  @media (max-width: 768px) {
    .album-header-section {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }
    
    .album-cover-section {
      flex: 0 0 200px;
    }
    
    .album-cover {
      width: 200px;
      height: 200px;
    }
    
    .album-info-section {
      flex: 1 1 100%;
      margin-top: 20px;
    }
    
    .introduction-section {
      flex: 1 1 100%;
      margin-top: 20px;
    }
    
    .action-buttons {
      justify-content: center;
    }
    
    .intro-content:not(.expanded) {
      max-height: 80px;
    }
  }
  
  /* 黑色主题下的页面样式 */

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

[data-theme="black"] .song-item {
  background: transparent;
  border-bottom: 1px solid var(--border);
}

[data-theme="black"] .song-item:hover {
  background: var(--background-light);
}

[data-theme="black"] .song-title {
  color: var(--text-primary);
}

[data-theme="black"] .song-artist {
  color: var(--text-secondary);
}

[data-theme="black"] .song-duration {
  color: var(--text-tertiary);
}

/* 简介部分样式 */
.top-right-intro {
  margin-left: auto;
  margin-bottom: 40px;
  width: 350px;
}

.content-section {
  margin-top: 40px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 20px 0;
  border-left: 4px solid var(--primary);
  padding-left: 12px;
}

.song-list-section {
  width: 100%;
}

.song-list-container {
  width: 100%;
}

.song-table-header {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid var(--border);
  color: var(--text-secondary);
  font-size: 14px;
  background-color: var(--background-card);
  font-weight: 600;
}

.song-table-body {
  max-height: none;
  overflow: visible;
  overflow-x: hidden;
  overflow-y: visible;
}

.song-row {
  display: flex;
  align-items: center;
  padding: 12px 8px;
  border-bottom: 1px solid var(--border);
  transition: all 0.3s ease;
  border-radius: 8px;
  margin: 2px 0;
}

.song-row:hover {
  background-color: var(--background-hover);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.song-row:last-child {
  border-bottom: none;
}

.table-col {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 8px;
  display: flex;
  align-items: center;
}

.col-index {
  flex: 0 0 80px;
  color: var(--text-tertiary);
  font-weight: 500;
  justify-content: center;
}

.col-song {
  flex: 3;
  min-width: 300px;
  cursor: pointer;
}

.col-song:hover .song-name {
  color: var(--primary);
}

.col-artist {
  flex: 2;
  min-width: 200px;
  color: var(--text-secondary);
}

.artist-link {
  color: var(--primary);
  cursor: pointer;
  transition: color 0.2s ease;
  text-decoration: none;
}

.artist-link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

.col-duration {
  flex: 0 0 120px;
  color: var(--text-tertiary);
  justify-content: center;
}

.col-actions {
  flex: 0 0 150px;
  justify-content: flex-end;
}

.song-info {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0;
}

.song-name {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-primary);
  text-align: left;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
  transition: color 0.2s ease;
}

.song-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.song-row:hover .song-actions {
  opacity: 1;
  transform: translateX(0);
}

.play-next-btn,
.play-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.play-next-btn {
  background-color: var(--background-card);
  color: var(--text-secondary);
  border: 1px solid var(--border);
}

.play-next-btn:hover {
  background-color: var(--primary);
  color: white;
  border-color: var(--primary);
  transform: scale(1.1);
}

.play-btn {
  background-color: var(--primary);
  color: white;
}

.play-btn:hover {
  background-color: var(--primary-dark);
  transform: scale(1.1);
}

.play-next-icon {
  width: 14px;
  height: 14px;
}

.play-icon-img {
  width: 14px;
  height: 14px;
  filter: brightness(0) invert(1);
}

.intro-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px 0;
  flex-shrink: 0;
}

.intro-wrapper {
  position: relative;
  flex: 1;
  overflow: hidden;
}

.intro-content {
    position: relative;
    max-height: 120px;
    overflow: hidden;
  }

  .intro-content {
    mask-image: linear-gradient(to bottom, black 80%, transparent 100%);
    -webkit-mask-image: linear-gradient(to bottom, black 80%, transparent 100%);
  }

/* 自定义滚动条样式 - 应用于弹窗内容 */
.desc-modal-content::-webkit-scrollbar {
  width: 6px;
}

.desc-modal-content::-webkit-scrollbar-track {
  background: var(--background-hover);
  border-radius: 3px;
}

.desc-modal-content::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 3px;
}

.desc-modal-content::-webkit-scrollbar-thumb:hover {
  background: var(--border-light);
}

.intro-text {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-secondary);
  margin: 0;
  padding-bottom: 10px;
  word-wrap: break-word;
  word-break: break-word;
  white-space: pre-wrap;
  text-align: justify;
}

.expand-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  padding: 8px;
  margin-top: 8px;
  background: none;
  border: 1px solid var(--border);
  border-radius: 6px;
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.expand-btn:hover {
  background: var(--background-hover);
  color: var(--text-primary);
  border-color: var(--border-light);
}

.expand-icon {
  transition: transform 0.3s ease;
}

.expand-btn.expanded .expand-icon {
  transform: rotate(180deg);
}

/* 黑色主题下的样式 */
[data-theme="black"] .introduction-section {
  background: #111827;
  border-color: #374151;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

[data-theme="black"] .intro-title {
  color: #f9fafb;
}

[data-theme="black"] .intro-text {
  color: #d1d5db;
  line-height: 1.8;
}

/* 专辑简介弹窗样式 */
.desc-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.desc-modal {
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

.desc-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border);
}

.desc-modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  padding: 4px;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: var(--text-primary);
}

.desc-modal-content {
  padding: 24px;
  max-height: calc(80vh - 120px);
  overflow-y: auto;
}

.modal-desc-text {
  margin: 0;
}

.modal-desc-paragraph {
  font-size: 16px;
  line-height: 1.8;
  color: var(--text-primary);
  word-wrap: break-word;
  word-break: break-word;
  text-align: justify;
  margin: 0 0 16px 0;
}

.modal-desc-paragraph:last-child {
  margin-bottom: 0;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 黑色主题下的弹窗样式 */
[data-theme="black"] .desc-modal-overlay {
  background-color: rgba(0, 0, 0, 0.8);
}

[data-theme="black"] .desc-modal {
  background: #111827;
  border: 1px solid #374151;
}

[data-theme="black"] .desc-modal-header {
  border-bottom-color: #374151;
}

[data-theme="black"] .desc-modal-header h3 {
  color: #f9fafb;
}

[data-theme="black"] .modal-desc-paragraph {
  color: #d1d5db;
  line-height: 1.8;
}

[data-theme="black"] .artist-link {
  color: #3b82f6;
}

[data-theme="black"] .artist-link:hover {
  color: #60a5fa;
}

[data-theme="black"] .expand-btn {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.3);
  color: #3b82f6;
}

[data-theme="black"] .expand-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
  border-color: #3b82f6;
}
</style>