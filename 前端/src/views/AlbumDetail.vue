<template>
  <!-- 二级导航栏 -->
  <SubNav />
  
  <div class="detail-page">
    <div class="loading" v-if="loading">
      <i class="el-icon-loading"></i>
      <span>正在加载专辑详情...</span>
    </div>
    
    <div v-else-if="album" class="album-detail-container">
      <!-- 左侧专辑封面 -->
      <div class="album-cover-section">
        <div class="album-cover">
          <img :src="album.cover || '/src/assets/1音乐.png'" :alt="album.name" class="cover-image" />
        </div>
      </div>
      
      <!-- 右侧专辑信息 -->
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
          
          <div class="meta-item" v-if="album.description">
            <span class="meta-label">类型：</span>
            <span class="meta-value">专辑</span>
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
            <img v-if="isFavorited" class="favorite-icon-img" src="/src/assets/已收藏.png" alt="已收藏" />
            <i v-else class="heart-icon">♡</i>
            {{ isFavorited ? '已收藏' : '收藏' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- 右上角简介 -->
    <div class="top-right-intro" v-if="album">
      <div class="introduction-section">
        <h3 class="intro-title">简介</h3>
        <div class="intro-content" :class="{ 'expanded': isIntroExpanded }">
          <p class="intro-text">{{ album.description || '暂无专辑简介' }}</p>
          <button v-if="shouldShowExpandButton" class="expand-btn" @click="toggleIntroExpansion">
            {{ isIntroExpanded ? '收起' : '...' }}
          </button>
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
              <div class="table-col col-artist">{{ song.artistName || artistName }}</div>
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
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAlbumById, getSongsByAlbumId } from '@/api/album.js'
import { getArtistById } from '@/api/artist.js'
import { ElMessage } from 'element-plus'
import SubNav from '@/components/SubNav.vue'
import { playSong, addToPlaylist, addMultipleToPlaylist, addToPlayNext, playlist } from '@/utils/musicPlayer.js'

const route = useRoute()
const router = useRouter()
const album = ref(null)
const songs = ref([])
const artist = ref(null)
const loading = ref(true)
const isFavorited = ref(false)
const isIntroExpanded = ref(false)

// 计算属性：歌手名称
const artistName = computed(() => {
  return artist.value?.name || '未知歌手'
})

// 计算属性：是否需要显示展开按钮
const shouldShowExpandButton = computed(() => {
  const description = album.value?.description || '暂无专辑简介'
  return description.length > 100
})

// 切换简介展开状态
const toggleIntroExpansion = () => {
  isIntroExpanded.value = !isIntroExpanded.value
}

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
          console.warn('获取歌手信息失败:', error)
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
      
      console.log('💿 专辑详情加载完成:')
      console.log('专辑信息:', album.value)
      console.log('歌手信息:', artist.value)
      console.log('歌曲数量:', songs.value?.length || 0)
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
    
    console.log('🎵 准备播放专辑:', album.value.name, songs.value.length, '首歌曲')
    
    // 将所有歌曲添加到播放列表并播放第一首
    const added = addMultipleToPlaylist(songs.value, true)
    
    if (added) {
      console.log('✅ 专辑歌曲已添加到播放列表')
      console.log('📋 当前播放列表长度:', playlist.value.length)
      ElMessage.success(`开始播放专辑《${album.value.name}》`)
    } else {
      ElMessage.error('播放失败，请稍后重试')
    }
  } catch (error) {
    console.error('播放专辑失败:', error)
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 播放单首歌曲
const playCurrentSong = async (song) => {
  try {
    console.log('🎵 准备播放歌曲:', song.name)
    
    const added = addToPlaylist(song, true)
    
    if (added) {
      console.log('✅ 歌曲已添加到播放列表:', song.name)
      ElMessage.success(`开始播放: ${song.name}`)
    } else {
      ElMessage.error('播放失败')
    }
  } catch (error) {
    console.error('播放歌曲失败:', error)
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 添加歌曲到下一首播放
const addSongToPlayNext = async (song) => {
  try {
    console.log('⏭️ 准备将歌曲添加到下一首播放:', song.name)
    
    const added = addToPlayNext(song)
    
    if (added) {
      console.log('✅ 歌曲已添加到下一首播放:', song.name)
      ElMessage.success(`${song.name} 已添加到下一首播放`)
    } else {
      ElMessage.error('添加失败')
    }
  } catch (error) {
    console.error('添加歌曲到下一首播放失败:', error)
    ElMessage.error('添加失败，请稍后重试')
  }
}

// 切换收藏状态
const toggleFavorite = () => {
  isFavorited.value = !isFavorited.value
  ElMessage.success(isFavorited.value ? '收藏成功' : '取消收藏成功')
  // TODO: 实现收藏功能
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

onMounted(() => {
  fetchAlbumDetail()
})
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

.album-detail-container {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

.album-cover-section {
  flex: 0 0 300px;
}

.album-cover {
  position: relative;
  width: 300px;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* 黑色主题下的特殊样式 */
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
  color: var(--text-primary);
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.play-all-btn {
  background: var(--success);
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
  background: #16a34a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.action-btn {
  background: var(--background-card);
  color: var(--text-primary);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-btn:hover {
  background: rgba(34, 197, 94, 0.1);
  border-color: var(--success);
  color: var(--success);
}

/* 收藏按钮的PNG图片样式 */
.favorite-icon-img {
  width: 16px;
  height: 16px;
  object-fit: contain;
  flex-shrink: 0;
}

/* 收藏按钮的红色爱心状态 */
.action-btn .heart-icon {
  transition: color 0.2s ease;
}

.action-btn.is-favorited .heart-icon {
  color: #d33a31 !important; /* 收藏成功的红色 */
}

.top-right-intro {
  position: absolute;
  top: 32px;
  right: 32px;
  z-index: 10;
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
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
  transition: background-color 0.2s;
}

.song-row:hover {
  background-color: var(--background-hover);
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

.introduction-section {
  background: var(--background-card);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--border);
}

.intro-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 16px 0;
}

.intro-content {
  position: relative;
}

.intro-content:not(.expanded) {
  max-height: 120px;
  overflow: hidden;
}

.intro-content.expanded {
  max-height: none;
}

.intro-text {
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-secondary);
  margin: 0;
}

.expand-btn {
  background: none;
  border: none;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  margin-top: 8px;
  padding: 0;
  transition: color 0.2s ease;
}

.expand-btn:hover {
  color: var(--primary-dark);
}

.more-link {
  color: var(--success);
  cursor: pointer;
  font-weight: 500;
}

.more-link:hover {
  text-decoration: underline;
}
</style>