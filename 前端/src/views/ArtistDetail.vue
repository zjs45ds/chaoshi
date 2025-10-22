// 歌手详情页面
<template>
  <div class="detail-page">
    <div class="loading" v-if="loading">
      <i class="el-icon-loading"></i>
      <span>正在加载歌手详情...</span>
    </div>
    
    <div v-else-if="artist" class="artist-detail">
      <div class="artist-header">
        <img :src="artist.avatar || '/default-avatar.png'" :alt="artist.name" class="artist-avatar" />
        <div class="artist-info">
            <div class="artist-name">{{ artist.name }}</div>
            <div class="artist-desc-wrapper">
              <div 
                class="artist-desc"
              >
                <template v-for="(paragraph, index) in splitDescription(artist.description)" :key="index">
                  <span class="desc-paragraph">{{ paragraph }}</span>
                  <br v-if="index < splitDescription(artist.description).length - 1" />
                </template>
              </div>
              <button 
                v-if="shouldShowExpandButton" 
                class="expand-btn" 
                @click="openDescModal"
              >
                <span class="expand-text">展开</span>
                <svg class="expand-icon" viewBox="0 0 24 24" width="14" height="14">
                  <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" fill="currentColor"/>
                </svg>
              </button>
            </div>
          </div>
      </div>
      
      <!-- 标签页头部 -->
      <div class="tabs-header">
        <button 
          class="tab-button" 
          :class="{ active: activeTab === 'songs' }"
          @click="activeTab = 'songs'"
        >
          热门歌曲
        </button>
        <button 
          class="tab-button" 
          :class="{ active: activeTab === 'albums' }"
          @click="activeTab = 'albums'"
        >
          专辑
        </button>
      </div>
      
      <!-- 内容区域 -->
      <div class="content-container">
        <!-- 热门歌曲模块 -->
        <div v-if="activeTab === 'songs' && songs && songs.length > 0" class="tab-content">
          <div class="section-header">
            <div class="section-title">热门歌曲</div>
            <button class="play-all-btn" @click="playAllSongs" :title="`播放全部 ${songs.length} 首歌曲`">
              <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
              </svg>
              播放全部
            </button>
          </div>
          <ul>
            <li v-for="song in songs" :key="song.id" class="song-item">
              <div class="song-content" @click="goToSong(song.id)">
                <span class="song-name">{{ song.name }}</span>
                <span class="song-album" v-if="song.albumName">· {{ song.albumName }}</span>
              </div>
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
            </li>
          </ul>
        </div>
        
        <!-- 专辑模块 -->
        <div v-else-if="activeTab === 'albums' && albums && albums.length > 0" class="tab-content">
          <div class="section-title">专辑</div>
          <ul>
            <li v-for="album in albums" :key="album.id" @click="goToAlbum(album.id)" class="album-item">
              <img :src="album.cover || require('@/assets/1音乐.png')" :alt="album.name" class="album-cover-mini" />
              <span class="album-name">{{ album.name }}</span>
              <span class="album-date" v-if="album.releaseDate">({{ album.releaseDate }})</span>
            </li>
          </ul>
        </div>
      </div>
      
      <!-- 无内容提示，根据当前选中的标签显示不同的提示 -->
      <div v-if="activeTab === 'songs' && (!songs || songs.length === 0)" class="no-content">
        <i class="el-icon-info"></i>
        <span>暂无热门歌曲</span>
      </div>
      <div v-else-if="activeTab === 'albums' && (!albums || albums.length === 0)" class="no-content">
        <i class="el-icon-info"></i>
        <span>暂无专辑</span>
      </div>
    </div>
    
    <div v-else class="error-message">
        <i class="el-icon-warning"></i>
        <span>歌手不存在或已被删除</span>
      </div>

      <!-- 歌手简介弹窗 -->
      <div v-if="isDescModalVisible" class="desc-modal-overlay" @click="closeDescModal">
        <div class="desc-modal" @click.stop>
          <div class="desc-modal-header">
            <h3>{{ artist.name }} - 详细介绍</h3>
            <button class="close-btn" @click="closeDescModal">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="currentColor"/>
              </svg>
            </button>
          </div>
          <div class="desc-modal-content">
            <div class="modal-desc-text">
                <p v-for="(paragraph, index) in splitDescription(artist.description)" 
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
import { getArtistById, getAlbumsByArtist, getSongsByArtist } from '@/api/artist.js'
import { ElMessage } from 'element-plus'
import { playSong, addToPlaylist, addMultipleToPlaylist, addToPlayNext, playlist } from '@/utils/musicPlayer.js'

const route = useRoute()
const router = useRouter()
const artist = ref(null)
const albums = ref([])
const songs = ref([])
const loading = ref(true)
const isDescModalVisible = ref(false)
const descContentRef = ref(null)
// 标签页状态，默认显示热门歌曲
const activeTab = ref('songs')

// 获取歌手详情
const fetchArtistDetail = async () => {
  try {
    loading.value = true
    const artistId = route.params.id
    
    // 并行获取歌手信息、专辑和歌曲
    const [artistResponse, albumsResponse, songsResponse] = await Promise.all([
      getArtistById(artistId).catch(() => null),
      getAlbumsByArtist(artistId).catch(() => null),
      getSongsByArtist(artistId).catch(() => null)
    ])
    
    // 按照前端数据处理规范，正确处理API响应
    if (artistResponse && artistResponse.code === 200) {
      artist.value = artistResponse.data
    }
    
    if (albumsResponse && albumsResponse.code === 200) {
      if (albumsResponse.data && albumsResponse.data.content) {
        albums.value = albumsResponse.data.content
      } else if (albumsResponse.data && Array.isArray(albumsResponse.data)) {
        albums.value = albumsResponse.data
      }
    }
    
    if (songsResponse && songsResponse.code === 200) {
      if (songsResponse.data && songsResponse.data.content) {
        songs.value = songsResponse.data.content
      } else if (songsResponse.data && Array.isArray(songsResponse.data)) {
        songs.value = songsResponse.data
      }
    }
    
    console.log('🎤 歌手详情加载完成:')
    console.log('歌手信息:', artist.value)
    console.log('专辑数量:', albums.value?.length || 0)
    console.log('歌曲数量:', songs.value?.length || 0)
    
    if (!artist.value) {
      ElMessage.error('歌手不存在')
    }
  } catch (error) {
    console.error('获取歌手详情失败:', error)
    ElMessage.error('获取歌手详情失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 跳转到专辑详情
const goToAlbum = (albumId) => {
  const targetPath = `/album/${albumId}`
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}

// 跳转到歌曲详情
const goToSong = (songId) => {
  const targetPath = `/song/${songId}`
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}

// 播放歌曲
const playCurrentSong = async (song) => {
  try {
    console.log('🎵 准备播放歌曲:', song.name)
    
    // 先添加到播放列表
    const added = addToPlaylist(song, true) // playNow = true 表示立即播放
    
    if (added) {
      console.log('✅ 歌曲已添加到播放列表:', song.name)
      console.log('📋 当前播放列表长度:', playlist.value.length)
      ElMessage.success(`开始播放: ${song.name}`)
    } else {
      console.warn('⚠️ 歌曲添加到播放列表失败')
      ElMessage.error('添加到播放列表失败')
    }
  } catch (error) {
    console.error('播放歌曲失败:', error)
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 播放全部歌曲
const playAllSongs = async () => {
  try {
    if (!songs.value || songs.value.length === 0) {
      ElMessage.warning('没有可播放的歌曲')
      return
    }
    
    console.log('🎵 准备播放全部歌曲:', songs.value.length, '首')
    
    // 将所有歌曲添加到播放列表并播放第一首
    const added = addMultipleToPlaylist(songs.value, true) // playFirst = true
    
    if (added) {
    
      ElMessage.success(`开始播放全部 ${songs.value.length} 首歌曲`)
    } else {
      console.warn('⚠️ 歌曲添加到播放列表失败')
      ElMessage.error('添加到播放列表失败')
    }
  } catch (error) {
    console.error('播放全部歌曲失败:', error)
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
      console.log('📋 当前播放列表长度:', playlist.value.length)
      ElMessage.success(`${song.name} 已添加到下一首播放`)
    } else {
      console.warn('⚠️ 歌曲添加到下一首播放失败')
      ElMessage.error('添加失败')
    }
  } catch (error) {
    console.error('添加歌曲到下一首播放失败:', error)
    ElMessage.error('添加失败，请稍后重试')
  }
}

// 计算属性：是否需要显示展开按钮
const shouldShowExpandButton = computed(() => {
  if (!artist.value || !artist.value.description) return false
  // 如果描述文字超过100个字符，显示展开按钮
  return artist.value.description.length > 100
})

// 打开简介弹窗
const openDescModal = () => {
  isDescModalVisible.value = true
  // 阻止事件冒泡
  event.stopPropagation()
}

// 关闭简介弹窗
const closeDescModal = () => {
  isDescModalVisible.value = false
}

// 分段处理简介内容
const splitDescription = (description) => {
  if (!description) return ['暂无介绍']
  
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
    // 查找20xx年作为可能的段落分割点（歌手简介中常有年份记录）
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

onMounted(() => {
  fetchArtistDetail()
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
  color: #000000;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
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

.artist-detail {
  max-width: 900px;
  margin: 0 auto;
}

.artist-header {
  display: flex;
  align-items: center;
  gap: 32px;
  margin-bottom: 32px;
  background: var(--background-card);
  padding: 24px;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.artist-avatar {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--primary-light);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.artist-info {
  flex: 1;
}

.artist-name {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.artist-desc-wrapper {
  position: relative;
  width: 100%;
}

.artist-desc {
  font-size: 16px;
  line-height: 1.8;
  color: var(--text-secondary);
  word-wrap: break-word;
  word-break: break-word;
  text-align: justify;
  position: relative;
  max-height: 80px;
  overflow: hidden;
  padding-right: 8px;
}

.desc-paragraph {
  display: inline;
}

/* 渐变遮罩效果 */
.artist-desc {
  mask-image: linear-gradient(to bottom, black 60%, transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, black 60%, transparent 100%);
  height: auto;
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

/* 歌手简介弹窗样式 */
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

/* 自定义滚动条样式 */
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

/* 黑色主题下的样式 */
[data-theme="black"] .artist-desc {
  color: #d1d5db;
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

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 24px 0 16px 0;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  border-left: 4px solid var(--primary);
  padding-left: 12px;
  margin: 0;
}

.play-all-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background-color: var(--primary);
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.play-all-btn:hover {
  background-color: var(--primary-dark);
  transform: translateY(-1px);
}

.play-all-btn .play-icon-svg {
  width: 12px;
  height: 12px;
}

ul {
  padding-left: 0;
  list-style: none;
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.album-item,
.song-item {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
  font-size: 16px;
  color: var(--text-primary);
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.song-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  cursor: pointer;
}

.album-item:hover {
  background: var(--background-hover);
  color: var(--primary);
  transform: translateX(4px);
}

.song-content:hover {
  color: var(--primary);
}

.song-item:hover {
  background: var(--background-hover);
}

.album-item:last-child,
.song-item:last-child {
  border-bottom: none;
}

.album-cover-mini {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.album-name,
.song-name {
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
}

.album-date {
  color: var(--text-secondary);
  font-size: 14px;
}

.song-album {
  color: var(--text-secondary);
  font-size: 14px;
}

.song-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.song-item:hover .song-actions {
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
  filter: brightness(0) invert(1); /* 将SVG变为白色 */
}

.no-content,
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

.no-content i {
  font-size: 48px;
  color: var(--text-secondary);
}

/* 黑色主题下的文字颜色增强 */
[data-theme="black"] .artist-name,
[data-theme="black"] .section-title {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
}

/* 黑色主题下的加载文字样式 */
[data-theme="black"] .loading {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}

/* 标签页样式 */
.tabs-header {
  display: flex;
  gap: 0;
  margin-top: 32px;
  border-bottom: 2px solid var(--border);
}

.tab-button {
  padding: 12px 24px;
  border: none;
  background: none;
  color: var(--text-secondary);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.tab-button:hover {
  color: var(--primary);
}

.tab-button.active {
  color: var(--primary);
  font-weight: 600;
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--primary);
}

/* 内容容器样式 */
.content-container {
  width: 100%;
  margin-top: 0;
}

.tab-content {
  margin-top: 24px;
}

/* 确保ul列表占满容器宽度 */
.content-column ul {
  width: 100%;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-page {
    width: 100%;
    padding: 16px;
  }
  
  .artist-detail {
    max-width: 100%;
  }
  
  .artist-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .artist-avatar {
    width: 120px;
    height: 120px;
  }
  
  .artist-name {
    font-size: 24px;
  }
  
  .album-item,
  .song-item {
    padding: 12px 16px;
  }
  
  /* 小屏幕下的标签页样式 */
  .tabs-header {
    flex-direction: column;
    margin-top: 24px;
  }
  
  .tab-button {
    text-align: left;
    padding: 10px 16px;
  }
  
  .content-container {
    margin-top: 16px;
  }
  
  .tab-content {
    margin-top: 16px;
  }
}
/* 黑色主题下的按钮样式 */
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
</style>