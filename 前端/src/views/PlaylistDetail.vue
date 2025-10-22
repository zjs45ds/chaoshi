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
          <div class="playlist-count">歌曲数量：{{ songs.length }}</div>
          <div class="playlist-desc" v-if="playlist.description">{{ playlist.description }}</div>
        </div>
      </div>
      
      <div class="song-list-header">
        <div class="song-list-title">歌曲列表</div>
        <div class="song-list-count">共 {{ songs.length }} 首歌曲</div>
      </div>
      
      <div class="song-list-content" v-if="songs.length > 0">
        <div class="song-list-header-row">
          <div class="col-index">序号</div>
          <div class="col-title">歌曲标题</div>
          <div class="col-album">专辑</div>
          <div class="col-duration">时长</div>
          <div class="col-operation">操作</div>
        </div>
        <div 
          v-for="(song, index) in songs" 
          :key="song.id" 
          @click.stop="goToSongDetail(song)" 
          :class="['song-item', { active: activeSongIndex === index }]">
          <div class="col-index">
            <span class="index-number">{{ index + 1 }}</span>
            <span class="play-btn" @click.stop="playSong(song, index)">
              <svg t="1760664769854" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15591" style="width: 35px; height: 35px;">
                <path d="M959.901158 643.014672a36.966795 36.966795 0 0 0-47.839382 21.399228c-62.8139 164.373745-223.283398 274.878764-399.271042 274.878764-235.63861 0-427.292664-191.654054-427.292665-427.243243 0-235.63861 191.654054-427.292664 427.292665-427.292664 221.257143 0 408.315058 172.528185 425.859459 392.796911l36.966795-2.965251-36.522007 9.241699 37.016216-2.32278 36.472587-9.834749C991.925869 213.152124 772.398456 10.625483 512.790734 10.625483 236.281081 10.625483 11.366795 235.539768 11.366795 512.049421c0 276.460232 224.914286 501.374517 501.423939 501.374517 206.579151 0 394.823166-129.630888 468.460231-322.520463a37.016216 37.016216 0 0 0-21.349807-47.888803z" fill="var(--primary)" p-id="15592"></path>
                <path d="M443.898069 309.275676a67.706564 67.706564 0 0 0-68.497297 0 67.706564 67.706564 0 0 0-34.248648 59.305019v286.83861c0 24.759846 12.8 46.949807 34.248648 59.35444 10.724324 6.177606 22.486486 9.29112 34.199228 9.29112 11.762162 0 23.573745-3.113514 34.248649-9.29112l248.389189-143.419305c21.448649-12.355212 34.199228-34.495753 34.199228-59.305019a67.706564 67.706564 0 0 0-34.199228-59.255599L443.898069 309.275676z m-28.664092 336.407722v-267.366796l231.487259 133.683398-231.487259 133.683398z" fill="var(--primary)" p-id="15593"></path>
              </svg>
            </span>
          </div>
          <div class="col-title">
            <div class="song-name clickable-item" :title="song.name" @click.stop="goToSongDetail(song)">{{ song.name }}</div>
            <div class="song-artist clickable-item" :title="song.artistName || '未知歌手'" @click.stop="goToArtistDetail(song)">{{ song.artistName || '未知歌手' }}</div>
          </div>
          <div class="col-album clickable-item" :title="song.albumName || '未知专辑'" @click.stop="goToAlbumDetail(song)">{{ song.albumName || '未知专辑' }}</div>
          <div class="col-duration"><span>{{ formatDuration(song.duration || 0) }}</span></div>
          <div class="col-operation">
              <button class="operation-btn favorite-btn" @click.stop="toggleFavorite(song.id)">              <!-- 使用正确的条件判断，确保只有用户手动收藏的歌曲才显示红色爱心 -->
              <svg v-if="favorites.has(song.id)" class="heart-icon-svg filled" viewBox="0 0 24 24" width="18" height="18">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="currentColor"/>
              </svg>
              <svg v-else class="heart-icon-svg" viewBox="0 0 24 24" width="18" height="18">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="none" stroke="currentColor" stroke-width="2"/>
              </svg>
            </button>
            <button class="operation-btn" @click.stop="addToPlaylistHandler(song)">
              <svg class="add-icon-svg" viewBox="0 0 24 24" width="18" height="18">
                <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="songs.length === 0" class="no-songs">
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

<style scoped>
/* 可点击元素的视觉效果 */
.clickable-item {
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-block;
}

.song-name.clickable-item:hover {
  color: #007bff;
  transform: translateX(2px);
}

/* 歌手名称点击效果 */
.song-artist.clickable-item:hover {
  color: #6c5ce7;
  transform: translateX(2px);
  background: linear-gradient(90deg, #6c5ce7, #a29bfe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 专辑名称点击效果 */
.col-album.clickable-item:hover {
  color: #00b894;
  transform: translateX(2px);
}

/* 歌曲行悬停效果 */
.song-item:hover {
  background-color: rgba(240, 240, 240, 0.3);
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 点击时的缩放效果 */
.clickable-item:active {
  transform: scale(0.98);
}
</style>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPlaylistById, getPlaylistSongs } from '@/api/playlist.js'
import { isSongFavorited, favoriteSong } from '@/api/song.js'
import { playSong as playMusic, addToPlaylist, isPlaying as isMusicPlaying } from '@/utils/musicPlayer.js'
import { toggleSongLike, getFavoriteSongs, initFavoriteSongs, isSongLiked } from '@/utils/favoriteManager.js'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const playlist = ref(null)
const songs = ref([])
const loading = ref(true)
const favorites = ref(new Set())
const activeSongIndex = ref(-1)
const currentUserId = ref(1) // 模拟当前用户ID

favorites.value.clear()

// 获取歌单详情和歌曲列表
const fetchPlaylistDetail = async function() {
  try {
    loading.value = true;
    const playlistId = route.params.id;
    
    // 首先获取歌单基本信息
    const playlistResponse = await getPlaylistById(playlistId);
    
    if (playlistResponse && playlistResponse.code === 200) {
      playlist.value = playlistResponse.data;
      
      // 然后获取歌单中的歌曲列表
      const songsResponse = await getPlaylistSongs(playlistId);
      
      if (songsResponse && songsResponse.code === 200) {
        let songList = songsResponse.data || [];
        songList = songList.map(function(song) {
          return {
            ...song,
            artistName: song.artistName || song.artist || '未知歌手',
            albumName: song.albumName || song.album || '未知专辑'
          };
        });
        if (songList.length === 0) {
          songList = getPlaylistSampleSongs();
        }
        songs.value = songList;
      } else {
        ElMessage.warning('获取歌曲列表失败');
        songs.value = getPlaylistSampleSongs();
      }
      
      // 获取歌曲收藏状态前先清空之前的状态
      favorites.value.clear();
      await fetchSongsFavoriteStatus();
    } else {
      ElMessage.error('歌单不存在');
      playlist.value = null;
      songs.value = [];
    }
  } catch (error) {
    ElMessage.error('获取歌单详情失败: ' + error.message);
    console.error('获取歌单详情时的错误:', error);
    playlist.value = null;
    songs.value = [];
  } finally {
    loading.value = false;
  }
}

// 获取歌曲收藏状态
const fetchSongsFavoriteStatus = async () => {
  if (!songs.value || songs.value.length === 0) return
  
  try {
    // 先清空收藏集合，确保初始状态为空
    favorites.value.clear();
    console.log('已清空收藏状态，开始获取新的收藏状态...');
    
    // 从全局的favoriteManager获取用户已收藏的歌曲状态
    const favoriteSongsList = getFavoriteSongs();
    
    // 将已收藏的歌曲ID添加到当前页面的收藏集合中
    favoriteSongsList.forEach(favoriteSong => {
      // 确保这个歌曲ID在当前歌单中存在
      if (songs.value.some(song => song.id === favoriteSong.id)) {
        favorites.value.add(favoriteSong.id);
        console.log(`添加已收藏歌曲: ID=${favoriteSong.id}, 名称=${favoriteSong.name}`);
      }
    });
    
    console.log('当前收藏数:', favorites.value.size)
    console.log('当前收藏列表:', Array.from(favorites.value))
  } catch (error) {
    console.error('获取歌曲收藏状态失败:', error)
    // 发生错误时清空收藏集合
    favorites.value.clear();
  }
}

// 跳转到歌曲详情页
const goToSongDetail = (song) => {
  if (!song || !song.id) {
    console.warn('歌曲ID不存在');
    return;
  }
  const songId = song.id;
  const targetPath = `/song/${songId}`;
  if (route.path !== targetPath) {
    router.push(targetPath);
  }
};

// 跳转到歌手详情页
const goToArtistDetail = (song) => {
  if (!song) {
    console.warn('歌曲信息不存在');
    return;
  }
  
  // 尝试多种可能的歌手ID字段
  let artistId = song.artistId || song.artist_id || song.singerId;
  
  // 如果没有直接的歌手ID，尝试从歌曲名称中提取（作为备选方案）
  if (!artistId && (song.artistName || song.artist)) {
    console.warn('歌曲中未找到歌手ID，无法跳转到歌手详情页');
    return;
  }
  
  if (artistId) {
    const targetPath = `/artist/${artistId}`;
    if (route.path !== targetPath) {
      router.push(targetPath);
    }
  }
};

// 跳转到专辑详情页
const goToAlbumDetail = (song) => {
  if (!song) {
    console.warn('歌曲信息不存在');
    return;
  }
  
  // 尝试多种可能的专辑ID字段
  let albumId = song.albumId || song.album_id;
  
  // 如果没有直接的专辑ID，尝试从歌曲名称中提取（作为备选方案）
  if (!albumId && (song.albumName || song.album)) {
    console.warn('歌曲中未找到专辑ID，无法跳转到专辑详情页');
    return;
  }
  
  if (albumId) {
    const targetPath = `/album/${albumId}`;
    if (route.path !== targetPath) {
      router.push(targetPath);
    }
  }
};

// 保持原有功能，支持直接通过ID跳转歌曲
const goToSong = (songId) => {
  if (!songId) {
    console.warn('歌曲ID不存在');
    return;
  }
  const targetPath = `/song/${songId}`;
  if (route.path !== targetPath) {
    router.push(targetPath);
  }
}


// 播放歌曲
const playSong = async (song, index) => {
  activeSongIndex.value = index
  ElMessage.success(`开始播放: ${song.name} - ${song.artistName}`)
  
  try {
    const success = await playMusic(song)
    if (!success) {
      ElMessage.warning('歌曲播放失败，请尝试其他歌曲')
    }
  } catch (error) {
    console.error('播放歌曲失败:', error)
    ElMessage.error('播放歌曲时发生错误')
  }
}

// 切换收藏状态
const toggleFavorite = async (songId) => {
  try {
    // 查找当前歌曲完整信息
    const song = songs.value.find(s => s.id === songId)
    if (!song) {
      ElMessage.error('未找到歌曲信息')
      return
    }
    
    console.log(`尝试切换歌曲收藏状态: 歌曲ID=${songId}, 歌曲名称=${song.name}`)
    console.log(`切换前本地收藏状态: ${favorites.value.has(songId)}`)
    console.log(`切换前全局收藏状态: ${isSongLiked(songId)}`)
    
    // 调用toggleSongLike函数来更新收藏状态，只有成功添加到后端我喜欢的列表才会返回true
    const newFavoriteState = await toggleSongLike({
      id: song.id,
      name: song.name,
      artist: song.artistName,
      album: song.albumName,
      artistId: song.artistId,
      albumId: song.albumId,
      duration: song.duration,
      cover: song.coverUrl,
      audioUrl: song.audioUrl
    })
    
    // 只有在toggleSongLike返回true（表示歌曲已成功添加到我喜欢的列表）时，才更新本地状态
    if (newFavoriteState) {
      favorites.value.add(songId)
      ElMessage.success(`已添加到我喜欢的音乐: ${song.name}`)
    } else {
      favorites.value.delete(songId)
      ElMessage.success(`已取消收藏: ${song.name}`)
    }
    
    console.log(`切换后本地收藏状态: ${favorites.value.has(songId)}`)
    console.log(`切换后全局收藏状态: ${isSongLiked(songId)}`)
    console.log(`当前收藏列表: ${Array.from(favorites.value).join(', ')}`)
    console.log(`全局收藏列表数量: ${getFavoriteSongs().length}`)
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    ElMessage.error('网络错误，请重试')
  }
}

// 添加到播放列表
const addToPlaylistHandler = (song) => {
  try {
    const success = addToPlaylist(song, false)
    if (success) {
      ElMessage.success(`已添加到播放列表: ${song.name}`)
    } else {
      ElMessage.error('添加失败，请重试')
    }
  } catch (error) {
    console.error('添加到播放列表失败:', error)
    ElMessage.error('网络错误，请重试')
  }
}

// 格式化歌曲时长
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 获取模拟歌单数据
const getPlaylistSampleSongs = () => {
  return [
    {
      id: 1,
      name: '暧昧',
      artistName: '薛之谦',
      albumName: '暧昧',
      duration: 200,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 2,
      name: 'Nothing',
      artistName: '薛之谦',
      albumName: '意外',
      duration: 210,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 3,
      name: '一半',
      artistName: '薛之谦',
      albumName: '一半',
      duration: 220,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 4,
      name: '为了遇见你',
      artistName: '薛之谦',
      albumName: '深深爱过你',
      duration: 230,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 5,
      name: '你还要我怎样',
      artistName: '薛之谦',
      albumName: '意外',
      duration: 240,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 6,
      name: '像风一样',
      artistName: '薛之谦',
      albumName: '渡',
      duration: 250,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 7,
      name: '违背的青春',
      artistName: '薛之谦',
      albumName: '渡',
      duration: 260,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 8,
      name: '演员',
      artistName: '薛之谦',
      albumName: '绅士',
      duration: 270,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 9,
      name: '刚刚好',
      artistName: '薛之谦',
      albumName: '初学者',
      duration: 280,
      cover: '/src/assets/1音乐.png'
    },
    {
      id: 10,
      name: '认真的雪',
      artistName: '薛之谦',
      albumName: '薛之谦',
      duration: 290,
      cover: '/src/assets/1音乐.png'
    }
  ]
}

onMounted(async () => {
  // 先初始化用户的收藏列表
  await initFavoriteSongs();
  console.log('已初始化用户收藏列表，开始获取歌单详情...');
  // 然后获取歌单详情
  fetchPlaylistDetail();
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

/* 歌单头部样式 */
.playlist-header {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}

.playlist-cover {
  width: 200px;
  height: 200px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.playlist-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.playlist-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.playlist-count {
  font-size: 16px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.playlist-desc {
  font-size: 14px;
  color: var(--text-tertiary);
  line-height: 1.6;
  max-width: 600px;
}

/* 歌曲列表样式 */
.song-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.song-list-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
}

.song-list-count {
  font-size: 14px;
  color: var(--text-secondary);
}

.song-list-content {
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.song-list-header-row {
  display: grid;
  grid-template-columns: 80px 1fr 200px 100px 80px;
  padding: 16px 24px;
  background: var(--background-header);
  font-size: 14px;
  color: var(--text-tertiary);
  font-weight: bold;
  border-bottom: 1px solid var(--border);
}

.song-item {
  display: grid;
  grid-template-columns: 80px 1fr 200px 100px 80px;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border);
  transition: all 0.3s ease;
  cursor: pointer;
}

.song-item:hover {
  background: var(--background-hover);
}

.song-item.active {
  background: var(--background-active);
}

.col-index {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: var(--text-secondary);
}

.index-number {
  display: block;
}

.play-btn {
  display: none;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  background: transparent;
}

/* 黑色主题下SVG图标显示为白色 */
[data-theme="black"] .play-btn svg path,
[data-theme="dark"] .play-btn svg path {
  fill: #ffffff !important;
}

.song-item:hover .play-btn {
  display: flex;
}

.song-item:hover .index-number {
  display: none;
}

.play-icon-svg {
  width: 16px;
  height: 16px;
  margin-left: 1px;
}

.col-title {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.song-name {
  font-size: 16px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.song-artist {
  font-size: 14px;
  color: var(--text-secondary);
}

.col-album {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: var(--text-secondary);
}

.col-duration {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: var(--text-tertiary);
}

.col-operation {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.operation-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 50%;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  cursor: pointer;
}

.operation-btn:hover {
  background: var(--background-hover);
  color: var(--primary);
}

.heart-icon-svg.filled {
  color: #ff4d4f; /* 设置为红色 */
}

.heart-icon-svg,
.add-icon-svg {
  width: 18px;
  height: 18px;
}

/* 错误提示样式 */
.error-message {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 12px;
}

.no-songs {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .detail-page {
    width: 100%;
    padding: 24px;
  }
}

@media (max-width: 768px) {
  .playlist-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .playlist-cover {
    width: 150px;
    height: 150px;
  }
  
  .playlist-title {
    font-size: 24px;
  }
  
  .song-list-header-row,
  .song-item {
    grid-template-columns: 60px 1fr 80px 60px;
  }
  
  .col-album {
    display: none;
  }
}
</style>