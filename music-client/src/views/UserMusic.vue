// 用户音乐页面
<template>
  <div :class="isLoggedIn ? 'user-music-page' : 'user-music-login-page'">
    <!-- 未登录提示 -->
    <LoginRequired 
      v-if="!isLoggedIn" 
      message="登录后即可查看您的音乐收藏、创建歌单，享受个性化的音乐体验！" 
    />
    
    <!-- 主内容区 -->
    <div class="main-content" v-else>
      <!-- 用户信息横幅 -->
      <div class="user-banner">
        <div class="banner-bg" :style="{ 'background-image': `url(${bannerBg})`, 'background-size': 'cover', 'background-position': 'center' }"></div>
        <div class="user-profile-container">
          <!-- 用户头像和基本信息 -->
          <div class="user-main-info">
            <div class="user-avatar-section">
              <div class="user-avatar-container" @click="changeAvatar">
                <img :src="avatarImg" alt="用户头像" class="user-avatar">
                <input 
                  type="file" 
                  ref="avatarInput" 
                  @change="onAvatarChange" 
                  accept="image/*" 
                  style="display: none;"
                />
              </div>
              <div class="user-name-container">
                <h1 class="user-name">{{ nickname }}</h1>
              </div>
            </div>
          </div>
          
          <!-- 内容导航标签 -->
          <div class="content-tabs">
            <div class="tab-item" :class="{ active: currentTab === 'liked' }" @click="switchTab('liked')">
              我喜欢
            </div>
            <div class="tab-item" :class="{ active: currentTab === 'albums' }" @click="switchTab('albums')">
              专辑
            </div>
            <div class="tab-item" :class="{ active: currentTab === 'videos' }" @click="switchTab('videos')">
              视频
            </div>
          </div>
          
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content-area">
        <!-- 内容头部 -->
        <div class="content-header">
          <div class="content-title">
            <h2>{{ currentTabTitle }}</h2>
            <span class="count">({{ currentTabCount }})</span>
          </div>
          <div class="action-buttons">
            <button 
              v-if="currentTab === 'liked' && currentTabCount > 0" 
              class="btn btn-secondary" 
              @click="playAll"
            >
              <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
              </svg>播放全部
            </button>
            <button 
              v-if="currentTab === 'liked' && currentTabCount > 0" 
              class="btn btn-secondary" 
              @click="downloadAll"
            >
              <i class="download-icon"></i>下载
            </button>
            <button 
              v-if="['liked', 'albums', 'videos'].includes(currentTab) && currentTabCount > 0" 
              class="btn btn-secondary" 
              @click="showBatchOptions"
            >
              <i class="batch-icon"></i>批量操作
            </button>
          </div>
        </div>

        <!-- 歌曲列表 -->
                  <div class="song-list-container" v-if="currentTab === 'liked'">
            <div class="song-table-header">
              <div class="table-col col-index">#</div>
              <div class="table-col col-song">歌曲</div>
              <div class="table-col col-artist">歌手</div>
              <div class="table-col col-album">专辑</div>
              <div class="table-col col-duration">时长</div>
              <div class="table-col col-actions"></div>
            </div>
          <div class="song-table-body">
            <div v-for="(song, index) in likedSongs" :key="index" class="song-row" @click="playSong(song, index)">
              <div class="table-col col-index">{{ index + 1 }}</div>
              <div class="table-col col-song">
                <div class="song-info">
                  <div class="song-name">
                    {{ song.name }}
                  </div>
                </div>
              </div>
              <div class="table-col col-artist">{{ song.artist }}</div>
              <div class="table-col col-album">{{ song.album }}</div>
              <div class="table-col col-duration">{{ song.duration }}</div>
              <div class="table-col col-actions">
                <div class="more-dropdown" :class="{ 'dropdown-open': activeDropdown === `song_${index}` }">
                  <button 
                    class="more-btn" 
                    @click.stop="toggleDropdown(`song_${index}`, $event)"
                    title="更多操作"
                  >
                    <svg class="more-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <circle cx="12" cy="5" r="2" fill="currentColor"/>
                      <circle cx="12" cy="12" r="2" fill="currentColor"/>
                      <circle cx="12" cy="19" r="2" fill="currentColor"/>
                    </svg>
                  </button>
                  <div class="dropdown-menu" v-if="activeDropdown === `song_${index}`">
                    <button class="dropdown-item" @click.stop="goToSongDetail(song)">
                      <i class="item-icon">🎵</i>
                      <span>歌曲详情</span>
                    </button>
                    <button class="dropdown-item" @click.stop="goToArtistDetail(song)">
                      <i class="item-icon">👤</i>
                      <span>歌手详情</span>
                    </button>
                    <button class="dropdown-item" @click.stop="goToAlbumDetail(song)">
                      <i class="item-icon">💿</i>
                      <span>专辑详情</span>
                    </button>
                    <button class="dropdown-item" @click.stop="handleAddToPlayNext(song)">
                      <i class="item-icon">⏭️</i>
                      <span>下一首播放</span>
                    </button>
                    <button class="dropdown-item" @click.stop="downloadSong(song)">
                      <i class="item-icon">⬇️</i>
                      <span>下载歌曲</span>
                    </button>
                    <button class="dropdown-item danger" @click.stop="removeFavorite(song, index)">
                      <i class="item-icon">❌</i>
                      <span>从我喜欢中移除</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 专辑列表 -->
        <div class="album-list-container" v-else-if="currentTab === 'albums'">
          <!-- 加载状态 -->
          <div v-if="loadingAlbums" class="loading-albums">
            <div class="loading-spinner"></div>
            <span>正在加载专辑...</span>
          </div>
          
          <!-- 专辑网格 -->
          <div v-else-if="favoriteAlbums.length > 0" class="albums-grid">
            <div 
              v-for="album in favoriteAlbums" 
              :key="album.id" 
              class="album-card"
              @click="goToAlbumDetail(album.id)"
            >
              <div class="album-cover">
                <img :src="album.cover || require('@/assets/1音乐.png')" :alt="album.name" class="cover-image">
                <div class="album-overlay">
                  <button class="play-album-btn" @click.stop="playAlbum(album)" title="播放专辑">
                    <img src="/src/assets/开始.svg" alt="播放" class="play-icon-img" />
                  </button>
                </div>
              </div>
              <div class="album-info">
                <div class="album-title">{{ album.name }}</div>
                <div class="album-artist">{{ album.artistName }}</div>
                <div class="album-date">{{ album.releaseDate ? formatDate(album.releaseDate) : '未知日期' }}</div>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-else class="empty-albums">
            <div class="empty-icon-container">
              <div class="empty-icon-bg"></div>
              <div class="empty-icon">💿</div>
            </div>
            <div class="empty-content">
              <h3 class="empty-title">您还没有收藏任何专辑</h3>
              <p class="empty-description">去发现一些好听的专辑吧！</p>
              <button class="discover-btn" @click="goToAlbumPage">
                <i class="btn-icon">🎵</i>
                立即发现精彩专辑
              </button>
            </div>
          </div>
        </div>

        <!-- 视频列表 -->
        <div class="video-list-container" v-else-if="currentTab === 'videos'">
          <!-- 加载状态 -->
          <div v-if="loadingVideos" class="loading-videos">
            <div class="loading-spinner"></div>
            <span>正在加载视频...</span>
          </div>
          
          <!-- 有视频时显示网格 -->
          <div v-else-if="favoriteVideos.length > 0" class="videos-grid">
            <div 
              v-for="video in favoriteVideos" 
              :key="video.id" 
              class="video-card"
              @click="goToVideoDetail(video.id)"
            >
              <div class="video-cover">
                <img :src="video.cover || '/src/assets/1音乐.png'" :alt="video.title" class="cover-image">
                <div class="video-overlay">
                  <button class="play-video-btn" @click.stop="playVideo(video)" title="播放视频">
                    <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                      <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
                    </svg>
                  </button>
                </div>
                <div class="video-duration">{{ formatDuration(video.duration) }}</div>
              </div>
              <div class="video-info">
                <div class="video-title">{{ video.title }}</div>
                <div class="video-artist">{{ video.artistName }}</div>
                <div class="video-date">2017/12/25</div>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-else class="empty-videos">
            <div class="empty-icon-container">
              <div class="empty-icon-bg"></div>
              <div class="empty-icon">📹</div>
            </div>
            <div class="empty-content">
              <h3 class="empty-title">您还没有收藏任何视频</h3>
              <p class="empty-description">去发现一些精彩的MV吧！</p>
              <button class="discover-btn" @click="goToMVPage">
                <i class="btn-icon">🎬</i>
                立即发现精彩MV
              </button>
            </div>
          </div>
        </div>

        <!-- 其他标签内容 -->
        <div v-else>
          <div class="empty-state">
            <p>该内容区域正在建设中...</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 背景选择器模态框 -->
    <div v-if="showBackgroundModal" class="background-modal" @click="closeBackgroundModal">
      <div class="background-modal-content" @click.stop>
        <div class="modal-header">
          <h3>选择背景墙</h3>
          <button class="close-btn" @click="closeBackgroundModal">×</button>
        </div>
        
        <div class="background-options">
          <!-- 预设背景 -->
          <div class="option-section">
            <h4>预设背景</h4>
            <div class="background-grid">
              <div 
                v-for="(bg, index) in presetBackgrounds" 
                :key="index"
                class="background-option"
                :class="{ active: bannerBg === bg.url }"
                @click="selectBackground(bg.url)"
              >
                <img :src="bg.url" :alt="bg.name">
                <div class="bg-overlay">
                  <span class="bg-name">{{ bg.name }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 自定义上传 -->
          <div class="option-section">
            <h4>自定义背景</h4>
            <div class="custom-upload">
              <input 
                type="file" 
                ref="backgroundInput" 
                @change="onBackgroundUpload" 
                accept="image/*" 
                style="display: none;"
              />
              <button class="upload-btn" @click="$refs.backgroundInput.click()">
                <i class="upload-icon">📁</i>
                选择图片
              </button>
              <p class="upload-tip">支持 JPG、PNG 格式，建议尺寸 1920x1080</p>
            </div>
          </div>
          
          <!-- 最近使用 -->
          <div class="option-section" v-if="recentBackgrounds.length > 0">
            <h4>最近使用</h4>
            <div class="background-grid">
              <div 
                v-for="(bg, index) in recentBackgrounds" 
                :key="index"
                class="background-option"
                :class="{ active: bannerBg === bg }"
                @click="selectBackground(bg)"
              >
                <img :src="bg" alt="最近背景">
                <div class="bg-overlay">
                  <span class="bg-name">最近背景</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 歌曲批量操作模态框 -->
    <div v-if="showBatchModal" class="batch-modal" @click="closeBatchModal">
      <div class="batch-modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量操作 - 我喜欢的歌曲</h3>
          <button class="close-btn" @click="closeBatchModal">×</button>
        </div>
        
        <div class="batch-content">
          <div class="batch-header">
            <div class="select-info">
              <span>已选择 {{ selectedSongs.length }} / {{ likedSongs.length }} 首歌曲</span>
            </div>
            <div class="batch-actions">
              <button class="btn btn-secondary" @click="selectAllSongs">
                {{ selectedSongs.length === likedSongs.length ? '取消全选' : '全选' }}
              </button>
            </div>
          </div>
          
          <div class="batch-list">
            <div v-for="(song, index) in likedSongs" :key="index" class="batch-item">
              <label class="checkbox-wrapper">
                <input 
                  type="checkbox" 
                  :checked="selectedSongs.some(s => s.id === `${song.name}_${index}`)"
                  @change="toggleSongSelection(song, index)"
                />
                <div class="song-info">
                  <span class="song-name">{{ song.name }}</span>
                  <span class="song-artist">{{ song.artist }}</span>
                </div>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeBatchModal">取消</button>
          <button 
            class="btn btn-danger" 
            @click="batchDeleteSongs"
            :disabled="selectedSongs.length === 0"
          >
            从收藏中移除 ({{ selectedSongs.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- 专辑批量操作模态框 -->
    <div v-if="showAlbumBatchModal" class="batch-modal" @click="closeBatchModal">
      <div class="batch-modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量操作 - 我收藏的专辑</h3>
          <button class="close-btn" @click="closeBatchModal">×</button>
        </div>
        
        <div class="batch-content">
          <div class="batch-header">
            <div class="select-info">
              <span>已选择 {{ selectedAlbums.length }} / {{ favoriteAlbums.length }} 个专辑</span>
            </div>
            <div class="batch-actions">
              <button class="btn btn-secondary" @click="selectAllAlbums">
                {{ selectedAlbums.length === favoriteAlbums.length ? '取消全选' : '全选' }}
              </button>
            </div>
          </div>
          
          <div class="batch-list">
            <div v-for="album in favoriteAlbums" :key="album.id" class="batch-item">
              <label class="checkbox-wrapper">
                <input 
                  type="checkbox" 
                  :checked="selectedAlbums.some(a => a.id === album.id)"
                  @change="toggleAlbumSelection(album)"
                />
                <div class="playlist-info">
                  <img :src="album.cover || '/src/assets/1音乐.png'" alt="专辑封面" class="playlist-cover-small">
                  <div class="playlist-details">
                    <span class="playlist-name">{{ album.name }}</span>
                    <span class="playlist-desc">{{ album.artistName }}</span>
                  </div>
                </div>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeBatchModal">取消</button>
          <button 
            class="btn btn-danger" 
            @click="batchRemoveAlbums"
            :disabled="selectedAlbums.length === 0"
          >
            取消收藏 ({{ selectedAlbums.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- 视频批量操作模态框 -->
    <div v-if="showVideoBatchModal" class="batch-modal" @click="closeBatchModal">
      <div class="batch-modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量操作 - 我收藏的视频</h3>
          <button class="close-btn" @click="closeBatchModal">×</button>
        </div>
        
        <div class="batch-content">
          <div class="batch-header">
            <div class="select-info">
              <span>已选择 {{ selectedVideos.length }} / {{ favoriteVideos.length }} 个视频</span>
            </div>
            <div class="batch-actions">
              <button class="btn btn-secondary" @click="selectAllVideos">
                {{ selectedVideos.length === favoriteVideos.length ? '取消全选' : '全选' }}
              </button>
            </div>
          </div>
          
          <div class="batch-list">
            <div v-for="video in favoriteVideos" :key="video.id" class="batch-item">
              <label class="checkbox-wrapper">
                <input 
                  type="checkbox" 
                  :checked="selectedVideos.some(v => v.id === video.id)"
                  @change="toggleVideoSelection(video)"
                />
                <div class="playlist-info">
                  <img :src="video.cover || '/src/assets/1音乐.png'" alt="视频封面" class="playlist-cover-small">
                  <div class="playlist-details">
                    <span class="playlist-name">{{ video.title }}</span>
                    <span class="playlist-desc">{{ video.artistName }}</span>
                  </div>
                </div>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeBatchModal">取消</button>
          <button 
            class="btn btn-danger" 
            @click="batchRemoveVideos"
            :disabled="selectedVideos.length === 0"
          >
            取消收藏 ({{ selectedVideos.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- 下载确认模态框 -->
    <div v-if="showDownloadModal" class="download-modal" @click="closeDownloadModal">
      <div class="download-modal-content" @click.stop>
        <div class="modal-header">
          <h3>下载歌曲</h3>
          <button class="close-btn" @click="closeDownloadModal">×</button>
        </div>
        
        <div class="download-content">
          <div class="download-info">
            <div class="info-item">
              <i class="info-icon">🎵</i>
              <span>准备下载 {{ downloadSongs_list.length }} 首歌曲</span>
            </div>
            <div class="info-item">
              <i class="info-icon">📁</i>
              <span>文件将保存到浏览器默认下载目录</span>
            </div>
            <div class="info-item">
              <i class="info-icon">ℹ️</i>
              <span>支持格式：MP3、文件名格式：歌手 - 歌曲名.mp3</span>
            </div>
          </div>
          
          <!-- 下载进度条 -->
          <div v-if="isDownloading" class="download-progress">
            <div class="progress-info">
              <span>下载中...</span>
              <span>{{ downloadProgress }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: downloadProgress + '%' }"></div>
            </div>
            <div class="download-status">
              <span>成功: {{ downloadResults.success }}</span>
              <span>失败: {{ downloadResults.failed }}</span>
              <span>跳过: {{ downloadResults.skipped }}</span>
            </div>
          </div>
          
          <!-- 歌曲列表 -->
          <div class="download-song-list">
            <div class="list-header">
              <h4>下载列表 ({{ downloadSongs_list.length }} 首)</h4>
            </div>
            <div class="song-list">
              <div v-for="(song, index) in downloadSongs_list.slice(0, 5)" :key="index" class="download-song-item">
                <div class="song-info">
                  <span class="song-name">{{ song.name }}</span>
                  <span class="song-artist">{{ song.artist }}</span>
                </div>
                <span class="file-status">
                  <i v-if="isSongDownloadable(song)" class="status-icon ready">✓</i>
                  <i v-else class="status-icon unavailable">×</i>
                </span>
              </div>
              <div v-if="downloadSongs_list.length > 5" class="more-songs">
                <span>还有 {{ downloadSongs_list.length - 5 }} 首歌曲...</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeDownloadModal" :disabled="isDownloading">取消</button>
          <button 
            class="btn btn-primary" 
            @click="confirmDownloadSongs"
            :disabled="downloadSongs_list.length === 0 || isDownloading"
          >
            {{ isDownloading ? '下载中...' : `开始下载 (${downloadSongs_list.length})` }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
// 文件上传功能已移除
import { ElMessage, ElMessageBox } from 'element-plus'
import { initFavoriteSongs, getFavoriteSongs, refreshFavoriteSongs, getUserId, favoriteStatus } from '@/utils/favoriteManager.js'
import { favoriteSong } from '@/api/song.js'
import { downloadSongs, getDownloadableSongs, showDownloadSummary, isSongDownloadable } from '@/utils/downloadManager.js'
import { addToPlaylist, addMultipleToPlaylist, addToPlayNext, addToPlaylistFirst } from '@/utils/musicPlayer.js'
import { getCurrentUserInfo, getUsername, getUserAvatar, getUserBio, initUserInfo, updateUserInfo } from '@/utils/userStore.js'
import { updateUserAvatar, updateUserProfile } from '@/api/user.js'
import LoginRequired from '@/components/LoginRequired.vue'
// import { getAllPlaylists, createPlaylist, addToPlaylist as addSongToPlaylist } from '@/utils/playlistManager.js' // 文件不存在，已注释

export default {
  components: {
    LoginRequired
  },
  setup() {
    const router = useRouter()
    
    // 登录状态检查
    const isLoggedIn = ref(false)
    
    // 检查用户登录状态
    const checkLoginStatus = () => {
      // 同时检查localStorage和sessionStorage，支持记住我功能
      const loginStatus = localStorage.getItem('isLogin') || sessionStorage.getItem('isLogin')
      const userId = localStorage.getItem('userId') || localStorage.getItem('currentUserId') || 
                     sessionStorage.getItem('userId') || sessionStorage.getItem('currentUserId')
      isLoggedIn.value = loginStatus === '1' && userId
      // CONSOLE LOG REMOVED: console.log('🔍 用户登录状态检查:', { loginStatus, userId, isLoggedIn: isLoggedIn.value });
      return isLoggedIn.value
    }
    
    // 使用统一的用户信息管理，确保从数据库获取最新数据
    const globalUserInfo = getCurrentUserInfo()
    const avatarImg = computed(() => globalUserInfo.avatar || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100')
    const nickname = computed(() => globalUserInfo.username || '用户昵称')
    const userBio = computed(() => globalUserInfo.bio || '热爱音乐，分享美好。')
    const bannerBg = ref(localStorage.getItem('userBannerBg') || 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&q=80') // 背景图
    const currentTab = ref('liked')
    const avatarInput = ref(null)
    const saving = ref(false)

    // 查看歌曲详情
    const viewSongDetail = (song) => {
      router.push(`/song/${song.id}`)
    }

    // 查看歌手详情
    const viewArtistDetail = (song) => {
      router.push(`/artist/${song.artistId || song.artistId}`)
    }

    // 查看专辑详情
    const viewAlbumDetail = (song) => {
      router.push(`/album/${song.albumId}`)
    }
    
    // 导航到专辑详情
    const navigateToAlbum = (albumId) => {
      router.push(`/album/${albumId}`)
    }


    // 复制音乐链接
    const copyMusicLink = (song) => {
      const musicLink = `${window.location.origin}/song/${song.id}`
      
      navigator.clipboard.writeText(musicLink)
        .then(() => {
          ElMessage.success('音乐链接已复制到剪贴板')
        })
        .catch(() => {
          ElMessage.warning('复制到剪贴板失败，请手动复制')
        })
    }
    
   
    
    // 批量操作相关状态
    const showBatchModal = ref(false)
    const showAlbumBatchModal = ref(false)
    const showVideoBatchModal = ref(false)
    const selectedSongs = ref([])
    const selectedAlbums = ref([])
    const selectedVideos = ref([])
    const isSelectMode = ref(false)
    
    // 下载相关状态
    const showDownloadModal = ref(false)
    const downloadSongs_list = ref([])
    const isDownloading = ref(false)
    const downloadProgress = ref(0)
    const currentDownloadIndex = ref(0)
    const downloadResults = ref({
      total: 0,
      success: 0,
      failed: 0,
      skipped: 0
    })
    
    // 更多下拉框相关状态
    const activeDropdown = ref(null)
    
    // 更多下拉框相关方法
    const toggleDropdown = (dropdownId, event) => {
      if (activeDropdown.value === dropdownId) {
        activeDropdown.value = null
      } else {
        activeDropdown.value = dropdownId
        
        // 动态计算下拉框位置
        nextTick(() => {
          const button = event.target.closest('.more-btn')
          const dropdown = button.parentElement.querySelector('.dropdown-menu')
          if (button && dropdown) {
            const buttonRect = button.getBoundingClientRect()
            dropdown.style.top = `${buttonRect.bottom + 4}px`
            dropdown.style.left = `${buttonRect.right - 200}px` // 右对齐
          }
        })
      }
    }
    
    // 关闭下拉框
    const closeDropdown = (event) => {
      // 如果点击的是下拉框内部或更多按钮，不关闭下拉框
      if (event && event.target) {
        const clickedElement = event.target.closest('.more-dropdown')
        if (clickedElement) {
          return // 点击在下拉框内部，不关闭
        }
      }
      
      activeDropdown.value = null
    }
    
    // 处理ESC键关闭下拉框
    const handleEscKey = (event) => {
      if (event.key === 'Escape' && activeDropdown.value) {
        closeDropdown()
      }
    }
    
    // 处理滚动关闭下拉框
    const handleScroll = () => {
      if (activeDropdown.value) {
        closeDropdown()
      }
    }
    
    // 处理窗口大小改变关闭下拉框
    const handleResize = () => {
      if (activeDropdown.value) {
        closeDropdown()
      }
    }
    
    // 跳转到歌曲详情
    const goToSongDetail = (song) => {
      closeDropdown()
      if (song.id) {
        router.push(`/song/${song.id}`)
      } else {
        ElMessage.warning('歌曲信息不完整')
      }
    }
    
    // 跳转到歌手详情
    const goToArtistDetail = (song) => {
      closeDropdown()
      if (song.artistId) {
        router.push(`/artist/${song.artistId}`)
      } else {
        ElMessage.warning('歌手信息不完整')
      }
    }
    
    // 跳转到专辑详情
    const goToAlbumDetail = (param) => {
      closeDropdown()
      // 检查参数类型：如果是对象，则使用albumId；如果是数字或字符串，则直接使用
      const albumId = typeof param === 'object' ? param.albumId : param
      if (albumId) {
        router.push(`/album/${albumId}`)
      } else {
        ElMessage.warning('专辑信息不完整')
      }
    }
    
    // 添加到下一首播放
    const handleAddToPlayNext = (song) => {
      closeDropdown()
      if (song && song.id) {
        const success = addToPlayNext({
          id: song.id,
          name: song.name,
          artist: song.artist || '未知歌手',
          album: song.album || '未知专辑',
          duration: song.duration || 0,
          cover: song.cover || '/src/assets/1音乐.png',
          audioUrl: song.audioUrl || song.filePath || ''
        })
        
        if (success) {
          ElMessage.success(`《${song.name}》已添加到下一首播放`)
        } else {
          ElMessage.error('添加失败，请稍后重试')
        }
      } else {
        ElMessage.warning('歌曲信息不完整')
      }
    }
    

    // 批量操作相关方法
    const toggleSongSelection = (song, index) => {
      const songId = `${song.name}_${index}`
      const existingIndex = selectedSongs.value.findIndex(s => s.id === songId)
      
      if (existingIndex > -1) {
        selectedSongs.value.splice(existingIndex, 1)
      } else {
        selectedSongs.value.push({ 
          ...song, 
          id: songId, 
          songId: song.id,
          index 
        })
      }
    }

    const toggleAlbumSelection = (album) => {
      const existingIndex = selectedAlbums.value.findIndex(a => a.id === album.id)
      
      if (existingIndex > -1) {
        selectedAlbums.value.splice(existingIndex, 1)
      } else {
        selectedAlbums.value.push(album)
      }
    }

    const toggleVideoSelection = (video) => {
      const existingIndex = selectedVideos.value.findIndex(v => v.id === video.id)
      
      if (existingIndex > -1) {
        selectedVideos.value.splice(existingIndex, 1)
      } else {
        selectedVideos.value.push(video)
      }
    }

    const selectAllSongs = () => {
      if (selectedSongs.value.length === likedSongs.value.length) {
        selectedSongs.value = []
      } else {
        selectedSongs.value = likedSongs.value.map((song, index) => ({
          ...song,
          id: `${song.name}_${index}`,
          songId: song.id,
          index
        }))
      }
    }

    const selectAllAlbums = () => {
      if (selectedAlbums.value.length === favoriteAlbums.value.length) {
        selectedAlbums.value = []
      } else {
        selectedAlbums.value = [...favoriteAlbums.value]
      }
    }

    const selectAllVideos = () => {
      if (selectedVideos.value.length === favoriteVideos.value.length) {
        selectedVideos.value = []
      } else {
        selectedVideos.value = [...favoriteVideos.value]
      }
    }

    const batchDeleteSongs = async () => {
      if (selectedSongs.value.length === 0) {
        ElMessage.warning('请选择要删除的歌曲')
        return
      }

      try {
        const userId = getUserId()
        if (!userId) {
          ElMessage.warning('请先登录')
          return
        }

        const deletePromises = selectedSongs.value.map(async (selectedSong) => {
          try {
            const response = await favoriteSong(userId, selectedSong.songId, 'unlike')
            if (response && response.code === 200) {
              return selectedSong.songId
            } else {
              return null
            }
          } catch (error) {
            return null
          }
        })

        const results = await Promise.all(deletePromises)
        const successCount = results.filter(result => result !== null).length

        if (successCount > 0) {
          ElMessage.success(`成功从收藏中移除 ${successCount} 首歌曲`)
          // 刷新歌曲列表
          refreshFavoriteSongs()
        } else {
          ElMessage.error('批量删除失败，请稍后重试')
        }
        
        selectedSongs.value = []
        showBatchModal.value = false
        
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('批量删除收藏歌曲失败:', error)
        ElMessage.error('批量删除失败')
      }
    }

    const batchRemoveAlbums = async () => {
      if (selectedAlbums.value.length === 0) {
        ElMessage.warning('请选择要取消收藏的专辑')
        return
      }

      try {
        const userId = getUserId()
        if (!userId) {
          ElMessage.warning('请先登录')
          return
        }

        // 导入API函数
        const { favoriteAlbum } = await import('@/api/favorite.js')

        const removePromises = selectedAlbums.value.map(async (album) => {
          try {
            const response = await favoriteAlbum(userId, album.id, 'unlike')
            if (response && response.code === 200) {
              return album.id
            } else {
              return null
            }
          } catch (error) {
            // CONSOLE LOG REMOVED: console.error('取消收藏专辑失败:', error)
            return null
          }
        })

        const results = await Promise.all(removePromises)
        const successCount = results.filter(result => result !== null).length

        if (successCount > 0) {
          ElMessage.success(`成功取消收藏 ${successCount} 个专辑`)
          // 从界面列表中移除成功的专辑
          const successIds = results.filter(result => result !== null)
          favoriteAlbums.value = favoriteAlbums.value.filter(album => !successIds.includes(album.id))
        } else {
          ElMessage.error('批量取消收藏失败，请稍后重试')
        }
        
        selectedAlbums.value = []
        showAlbumBatchModal.value = false
        
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('批量取消收藏专辑失败:', error)
        ElMessage.error('批量取消收藏失败')
      }
    }

    const batchRemoveVideos = async () => {
      if (selectedVideos.value.length === 0) {
        ElMessage.warning('请选择要取消收藏的视频')
        return
      }

      try {
        const userId = getUserId()
        if (!userId) {
          ElMessage.warning('请先登录')
          return
        }

        // 导入API函数
        const { favoriteMv } = await import('@/api/favorite.js')

        const removePromises = selectedVideos.value.map(async (video) => {
          try {
            const response = await favoriteMv(userId, video.id, 'unlike')
            if (response && response.code === 200) {
              return video.id
            } else {
              return null
            }
          } catch (error) {
            // CONSOLE LOG REMOVED: console.error('取消收藏视频失败:', error)
            return null
          }
        })

        const results = await Promise.all(removePromises)
        const successCount = results.filter(result => result !== null).length

        if (successCount > 0) {
          ElMessage.success(`成功取消收藏 ${successCount} 个视频`)
          // 从界面列表中移除成功的视频
          const successIds = results.filter(result => result !== null)
          favoriteVideos.value = favoriteVideos.value.filter(video => !successIds.includes(video.id))
          
          // 触发全局事件通知其他组件更新
          window.dispatchEvent(new CustomEvent('mvFavoriteChanged', {
            detail: { 
              mvIds: successIds, 
              isLiked: false
            }
          }))
        } else {
          ElMessage.error('批量取消收藏失败，请稍后重试')
        }
        
        selectedVideos.value = []
        showVideoBatchModal.value = false
        
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('批量取消收藏视频失败:', error)
        ElMessage.error('批量取消收藏失败')
      }
    }

    const closeBatchModal = () => {
      showBatchModal.value = false
      showAlbumBatchModal.value = false
      showVideoBatchModal.value = false
      selectedSongs.value = []
      selectedAlbums.value = []
      selectedVideos.value = []
    }

    // 显示批量操作模态框
    const showBatchOptions = () => {
      switch(currentTab.value) {
        case 'liked':
          if (likedSongs.value.length === 0) {
            ElMessage.warning('暂无歌曲可进行批量操作')
            return
          }
          showBatchModal.value = true
          break
        case 'albums':
          if (favoriteAlbums.value.length === 0) {
            ElMessage.warning('暂无专辑可进行批量操作')
            return
          }
          showAlbumBatchModal.value = true
          break
        case 'videos':
          if (favoriteVideos.value.length === 0) {
            ElMessage.warning('暂无视频可进行批量操作')
            return
          }
          showVideoBatchModal.value = true
          break
        default:
          ElMessage.warning('当前标签页不支持批量操作')
      }
    }

    // 用户喜欢的歌曲列表（从 favoriteManager 获取）
    const likedSongs = computed(() => getFavoriteSongs())


    // 用户收藏的专辑列表
    const favoriteAlbums = ref([])
    const loadingAlbums = ref(false)

    // 用户收藏的视频列表
    const favoriteVideos = ref([])
    const loadingVideos = ref(false)

    // 计算属性
    const currentTabTitle = computed(() => {
        switch(currentTab.value) {
          case 'liked': return '我喜欢的歌曲'
          case 'albums': return '我收藏的专辑'
          case 'videos': return '我收藏的视频'
          default: return ''
        }
      })

    const currentTabCount = computed(() => {
        switch(currentTab.value) {
          case 'liked': return likedSongs.value.length
          case 'albums': return favoriteAlbums.value.length
          case 'videos': return favoriteVideos.value.length
          default: return 0
        }
      })

    // 加载收藏的专辑
    const loadFavoriteAlbums = async () => {
      loadingAlbums.value = true
      try {
        const userId = getUserId()
        if (!userId) {
          favoriteAlbums.value = []
          return
        }
        
        // 导入API函数
        const { getUserFavoriteAlbums } = await import('@/api/favorite.js')
        const { getArtistById } = await import('@/api/artist.js')
        
        const response = await getUserFavoriteAlbums(userId)
        if (response.code === 200 && response.data) {
          // 为每个专辑获取艺术家信息
          const albumsWithArtist = await Promise.all(
            response.data.map(async (album) => {
              let artistName = '未知歌手'
              if (album.artistId) {
                try {
                  const artistResponse = await getArtistById(album.artistId)
                  if (artistResponse.code === 200 && artistResponse.data) {
                    artistName = artistResponse.data.name
                  }
                } catch (error) {
                  // CONSOLE LOG REMOVED: console.warn('获取歌手信息失败:', error)
                }
              }
              return {
                ...album,
                artistName
              }
            })
          )
          favoriteAlbums.value = albumsWithArtist
        } else {
          favoriteAlbums.value = []
        }
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('加载收藏专辑失败:', error)
        ElMessage.error('加载收藏专辑失败')
        favoriteAlbums.value = []
      } finally {
        loadingAlbums.value = false
      }
    }

    // 加载收藏的视频
    const loadFavoriteVideos = async () => {
      loadingVideos.value = true
      try {
        const userId = getUserId()
        if (!userId) {
          favoriteVideos.value = []
          return
        }
        
        // 导入API函数
        const { getUserFavoriteMvs } = await import('@/api/favorite.js')
        const { getArtistById } = await import('@/api/artist.js')
        
        const response = await getUserFavoriteMvs(userId)
        if (response.code === 200 && response.data) {
          // 为每个MV获取艺术家信息
          const videosWithArtist = await Promise.all(
            response.data.map(async (mv) => {
              let artistName = '未知歌手'
              if (mv.artistId) {
                try {
                  const artistResponse = await getArtistById(mv.artistId)
                  if (artistResponse.code === 200 && artistResponse.data) {
                    artistName = artistResponse.data.name
                  }
                } catch (error) {
                  // CONSOLE LOG REMOVED: console.warn('获取歌手信息失败:', error)
                }
              }
              return {
                id: mv.id,
                title: mv.title || mv.name,
                artistName,
                cover: mv.cover || mv.thumbnail,
                duration: mv.duration,
                viewCount: mv.viewCount || mv.playCount || 0
              }
            })
          )
          favoriteVideos.value = videosWithArtist
        } else {
          favoriteVideos.value = []
        }
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('加载收藏视频失败:', error)
        ElMessage.error('加载收藏视频失败')
        favoriteVideos.value = []
      } finally {
        loadingVideos.value = false
      }
    }

    // 方法
    const switchTab = (tab) => {
      currentTab.value = tab
      
      // 根据标签页加载对应数据
      if (tab === 'liked') {
        // 每次切换到喜欢标签页都重新加载，确保数据是最新的
        refreshFavoriteSongs()
      } else if (tab === 'albums' && favoriteAlbums.value.length === 0) {
        loadFavoriteAlbums()
      } else if (tab === 'videos') {
        // 每次切换到视频标签页都重新加载，确保数据是最新的
        loadFavoriteVideos()
      }
    }

    const changeAvatar = () => {
      // 触发文件选择
      avatarInput.value.click()
    }

    const onAvatarChange = async (e) => {
      const file = e.target.files[0]
      if (!file) return
      
      try {
        // 验证文件类型和大小
        if (!file.type.startsWith('image/')) {
          ElMessage.error('请选择图片文件')
          return
        }
        
        if (file.size > 5 * 1024 * 1024) { // 5MB限制
          ElMessage.error('文件大小不能超过5MB')
          return
        }
        
        saving.value = true
        
        // 创建FormData
        const formData = new FormData()
        formData.append('file', file)
        formData.append('userId', localStorage.getItem('userId') || localStorage.getItem('currentUserId'))
        
        // 上传头像到MinIO
        const uploadResponse = await updateUserAvatar(formData)
        
        // 检查响应是否成功（支持多种格式）
        if (uploadResponse && (uploadResponse.code === 200 || uploadResponse.code === 0 || uploadResponse.success === true)) {
          // 获取上传后的头像URL（支持多种字段名）
          const avatarUrl = uploadResponse.data?.avatar || uploadResponse.data || uploadResponse.url
          
          if (!avatarUrl) {
            throw new Error('无法获取头像URL')
          }
          
          // 更新用户资料中的头像
          const userId = localStorage.getItem('userId') || localStorage.getItem('currentUserId')
          const profileData = {
            avatar: avatarUrl
          }
          
          // 立即保存头像信息
          await updateUserProfile(userId, profileData)
          
          // 更新本地存储和用户信息
          localStorage.setItem('userAvatar', avatarUrl)
          updateUserInfo({ avatar: avatarUrl })
          
          // 重新初始化用户信息以更新显示
          await initUserInfo()
          
          ElMessage.success('头像上传成功！')
        } else {
          throw new Error(uploadResponse?.message || '头像上传失败')
        }
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('头像上传失败:', error)
        ElMessage.error(`上传失败: ${error.message || '未知错误'}`)
      } finally {
        saving.value = false
        // 清空文件输入，以便可以重新选择同一文件
        e.target.value = ''
      }
    }

    // 用户信息更新处理函数
    const handleUserInfoUpdate = async () => {
      // CONSOLE LOG REMOVED: console.log('🔔 UserMusic: 接收到用户信息更新事件')
      // globalUserInfo是响应式的，会自动更新显示
      // 但我们需要重新初始化用户信息以确保最新数据
      try {
        await initUserInfo()
        // CONSOLE LOG REMOVED: console.log('✅ UserMusic: 用户信息已从数据库更新')
      } catch (error) {
        // CONSOLE LOG REMOVED: console.warn('⚠️ UserMusic: 无法从数据库获取最新用户信息')
      }
    }

    const updateBackground = (event) => {
      bannerBg.value = event.detail.url
    }

    const playSong = (song, index) => {
      closeDropdown() // 播放歌曲时关闭下拉框
      if (song && song.id) {
        // CONSOLE LOG REMOVED: console.log('🎵 准备播放歌曲:', song.name, '来自我的音乐 - 将移动/添加到播放列表第一位')
        
        // 添加到播放列表第一位并立即播放
        const success = addToPlaylistFirst({
          id: song.id,
          name: song.name,
          artist: song.artist || '未知歌手',
          album: song.album || '未知专辑',
          duration: song.duration || 0,
          cover: song.cover || '/src/assets/1音乐.png',
          audioUrl: song.audioUrl || song.filePath || ''
        })
        
        if (success) {
          // CONSOLE LOG REMOVED: console.log('✅ 歌曲已添加到播放列表第一位并开始播放:', song.name)
          ElMessage.success(`开始播放：${song.name}`)
        } else {
          // CONSOLE LOG REMOVED: console.error('❌ 添加歌曲到播放列表失败')
          ElMessage.error('播放失败，请稍后重试')
        }
      } else {
        // CONSOLE LOG REMOVED: console.warn('⚠️ 歌曲信息不完整:', song)
        ElMessage.warning('歌曲信息不完整')
      }
    }

    const playAll = () => {
      if (currentTab.value === 'liked') {
        if (likedSongs.value.length === 0) {
          ElMessage.warning('暂无收藏的歌曲')
          return
        }
        
        // 格式化歌曲数据并批量添加到播放列表
        const formattedSongs = likedSongs.value.map(song => ({
          id: song.id,
          name: song.name,
          artist: song.artist || '未知歌手',
          album: song.album || '未知专辑',
          duration: song.duration || 0,
          cover: song.cover || '/src/assets/1音乐.png',
          audioUrl: song.audioUrl || song.filePath || ''
        }))
        
        // 添加到播放列表并播放第一首
        const success = addMultipleToPlaylist(formattedSongs, true)
        
        if (success) {
          ElMessage.success(`开始播放我喜欢的音乐，共${likedSongs.value.length}首歌曲`)
        }
      } else if (currentTab.value === 'albums') {
        if (favoriteAlbums.value.length === 0) {
          ElMessage.warning('暂无收藏的专辑')
          return
        }
        ElMessage.info('专辑播放功能正在开发中，敬请期待')
      } else if (currentTab.value === 'videos') {
        if (favoriteVideos.value.length === 0) {
          ElMessage.warning('暂无收藏的视频')
          return
        }
        ElMessage.info('视频播放功能正在开发中，敬请期待')
      } else {
        ElMessage.warning('当前标签页不支持播放全部功能')
      }
    }

    // 更多按钮相关方法已移除
    
    
    // 从我喜欢中移除
    const removeFavorite = async (song, index) => {
      closeDropdown()
      try {
        const userId = getUserId()
        if (!userId) {
          ElMessage.warning('请先登录')
          return
        }

        const response = await favoriteSong(userId, song.id, 'unlike')
        if (response && response.code === 200) {
          // 从界面列表中移除
          likedSongs.value.splice(index, 1)
          
          // 更新收藏状态映射
          favoriteStatus.set(song.id, false)
          
          // 触发全局事件通知其他组件更新
          window.dispatchEvent(new CustomEvent('songLikeChanged', {
            detail: { 
              songId: song.id, 
              isLiked: false
            }
          }))
          
          ElMessage.success(`已将《${song.name}》从我喜欢中移除`)
        } else {
          ElMessage.error('移除失败：' + (response?.message || '请重试'))
        }
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('移除收藏失败:', error)
        ElMessage.error('移除失败，请检查网络连接')
      }
    }

    const showAddToModal = () => {

    }

    // 真正的下载功能
    const downloadAll = () => {
      if (currentTab.value === 'liked') {
        if (likedSongs.value.length === 0) {
          ElMessage.warning('暂无可下载的歌曲')
          return
        }
        
        // 过滤可下载的歌曲
        const downloadableSongs = getDownloadableSongs(likedSongs.value)
        
        if (downloadableSongs.length === 0) {
          ElMessage.warning('暂无可下载的音频文件')
          return
        }
        
        // 显示下载确认模态框
        downloadSongs_list.value = downloadableSongs
        showDownloadModal.value = true
      }
    }
    
    // 下载单首歌曲
    const downloadSong = (song) => {
      closeDropdown()
      if (!isSongDownloadable(song)) {
        ElMessage.warning('该歌曲暂无可下载的音频文件')
        return
      }
      
      downloadSongs_list.value = [song]
      showDownloadModal.value = true
    }
    
    // 显示下载模态框
    const showDownloadConfirm = (songs) => {
      downloadSongs_list.value = songs
      showDownloadModal.value = true
    }
    
    // 关闭下载模态框
    const closeDownloadModal = () => {
      if (!isDownloading.value) {
        showDownloadModal.value = false
        downloadSongs_list.value = []
        resetDownloadProgress()
      }
    }
    
    // 重置下载进度
    const resetDownloadProgress = () => {
      downloadProgress.value = 0
      currentDownloadIndex.value = 0
      downloadResults.value = {
        total: 0,
        success: 0,
        failed: 0,
        skipped: 0
      }
    }
    
    // 确认下载歌曲
    const confirmDownloadSongs = async () => {
      if (downloadSongs_list.value.length === 0) {
        ElMessage.warning('暂无可下载的歌曲')
        return
      }
      
      try {
        isDownloading.value = true
        resetDownloadProgress()
        
        // CONSOLE LOG REMOVED: console.log('🇝 开始下载歌曲:', downloadSongs_list.value.length, '首')
        
        // 调用下载管理器
        const results = await downloadSongs(
          downloadSongs_list.value,
          // 总体进度回调
          (progress, currentResults) => {
            downloadProgress.value = progress
            downloadResults.value = currentResults
          },
          // 单个文件进度回调
          (index, song, status, progress = 0) => {
            currentDownloadIndex.value = index
            // CONSOLE LOG REMOVED: console.log(`下载进度: ${song.name} - ${status} (${progress}%)`)
          }
        )
        
        // 显示下载结果
        showDownloadSummary(results)
        
        // 下载完成后稍后关闭模态框
        setTimeout(() => {
          closeDownloadModal()
        }, 2000)
        
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('下载歌曲失败:', error)
        ElMessage.error('下载失败，请稍后重试')
      } finally {
        isDownloading.value = false
      }
    }




    // 跳转到视频详情
    const goToVideoDetail = (videoId) => {
      router.push(`/mv/${videoId}`)
    }

    // 播放专辑
    const playAlbum = async (album) => {
      try {
        // CONSOLE LOG REMOVED: console.log('🎵 准备播放专辑:', album.name, '专辑ID:', album.id)
        
        // 跳转到专辑详情页，并传递自动播放参数
        router.push({
          path: `/album/${album.id}`,
          query: { autoPlay: 'true' }
        })
        
        ElMessage.success(`正在跳转到专辑《${album.name}》`)
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('播放专辑失败:', error)
        ElMessage.error('播放失败')
      }
    }

    // 播放视频
    const playVideo = async (video) => {
      try {
        // CONSOLE LOG REMOVED: console.log('🎬 准备播放视频:', video.title, '视频ID:', video.id)
        
        // 跳转到视频详情页，并传递自动播放参数
        router.push({
          path: `/mv/${video.id}`,
          query: { autoPlay: 'true' }
        })
        
        ElMessage.success(`正在跳转到视频《${video.title}》`)
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error('播放视频失败:', error)
        ElMessage.error('播放失败')
      }
    }


    // 刷新收藏的视频列表
    const refreshFavoriteVideos = async () => {
      // 无论当前在哪个标签页，都刷新视频列表数据
      // 这样当用户切换到视频标签页时就能看到最新的收藏状态
      await loadFavoriteVideos()
    }

    // 跳转到MV页面
    const goToMVPage = () => {
      router.push('/mv')
    }

    // 跳转到专辑页面
    const goToAlbumPage = () => {
      router.push('/album')
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }

    // 格式化播放次数
    const formatViews = (count) => {
      if (count >= 10000) {
        return Math.floor(count / 10000) + '万'
      }
      return count.toString()
    }

    // 格式化时长
    const formatDuration = (seconds) => {
      if (!seconds) return '00:00'
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
    }

    // 新增功能方法已移除

    onBeforeUnmount(() => {
      window.removeEventListener('user-info-updated', handleUserInfoUpdate)
      window.removeEventListener('background-changed', updateBackground)
      window.removeEventListener('songLikeChanged', refreshFavoriteSongs)
      window.removeEventListener('mvFavoriteChanged', refreshFavoriteVideos)
      document.removeEventListener('click', closeDropdown)
      document.removeEventListener('keydown', handleEscKey)
      window.removeEventListener('scroll', handleScroll)
      window.removeEventListener('resize', handleResize)
      
      // 清理滚动阻止事件监听器
      if (window.scrollPreventListeners) {
        window.scrollPreventListeners.forEach(cleanup => cleanup())
        delete window.scrollPreventListeners
      }
      
      // 恢复 body 和 html 样式
      document.body.style.overflow = ''
      document.body.style.position = ''
      document.body.style.width = ''
      document.body.style.height = ''
      document.body.style.top = ''
      document.body.style.left = ''
      document.body.style.margin = ''
      document.body.style.padding = ''
      
      document.documentElement.style.overflow = ''
      document.documentElement.style.height = ''
      document.documentElement.style.margin = ''
      document.documentElement.style.padding = ''
      
      // 移除我的音乐页面样式类
      document.body.classList.remove('my-music-page')
    })

    // 组件挂载时初始化数据
    onMounted(async () => {
      // 首先检查登录状态
      const loginStatus = checkLoginStatus()
      
      if (!loginStatus) {
        // CONSOLE LOG REMOVED: console.log('👤 用户未登录，显示登录提示页面')
        
        // 强制禁用页面滚动 - 多重保险
        document.body.style.overflow = 'hidden !important'
        document.body.style.position = 'fixed !important'
        document.body.style.width = '100vw !important'
        document.body.style.height = '100vh !important'
        document.body.style.top = '0 !important'
        document.body.style.left = '0 !important'
        document.body.style.margin = '0 !important'
        document.body.style.padding = '0 !important'
        
        // 同时禁用html元素滚动
        document.documentElement.style.overflow = 'hidden !important'
        document.documentElement.style.height = '100vh !important'
        document.documentElement.style.margin = '0 !important'
        document.documentElement.style.padding = '0 !important'
        
        // 阻止滚动事件 - 加强版
        const preventScroll = (e) => {
          e.preventDefault()
          e.stopPropagation()
          e.stopImmediatePropagation()
          return false
        }
        
        // 阻止键盘滚动
        const preventKeyboardScroll = (e) => {
          const scrollKeys = [32, 33, 34, 35, 36, 37, 38, 39, 40]
          if (scrollKeys.includes(e.keyCode)) {
            e.preventDefault()
            e.stopPropagation()
            return false
          }
        }
        
        // 添加所有可能的滚动事件监听器
        const events = ['scroll', 'wheel', 'mousewheel', 'DOMMouseScroll', 'touchstart', 'touchmove', 'touchend']
        events.forEach(event => {
          window.addEventListener(event, preventScroll, { passive: false, capture: true })
          document.addEventListener(event, preventScroll, { passive: false, capture: true })
          document.body.addEventListener(event, preventScroll, { passive: false, capture: true })
        })
        
        // 阻止键盘滚动
        document.addEventListener('keydown', preventKeyboardScroll, { passive: false, capture: true })
        
        // 强制滚动位置为0
        const forceScrollTop = () => {
          window.scrollTo(0, 0)
          document.body.scrollTop = 0
          document.documentElement.scrollTop = 0
        }
        
        // 立即设置滚动位置为0
        forceScrollTop()
        
        // 定期检查并重置滚动位置
        const scrollResetInterval = setInterval(forceScrollTop, 16)
        
        // 存储事件监听器以便清理
        window.scrollPreventListeners = [
          () => {
            events.forEach(event => {
              window.removeEventListener(event, preventScroll, { capture: true })
              document.removeEventListener(event, preventScroll, { capture: true })
              document.body.removeEventListener(event, preventScroll, { capture: true })
            })
            document.removeEventListener('keydown', preventKeyboardScroll, { capture: true })
            clearInterval(scrollResetInterval)
          }
        ]
        
        return // 未登录则不继续初始化其他数据
      }
      
      // CONSOLE LOG REMOVED: console.log('✅ 用户已登录，初始化我的音乐数据')
      
      // 初始化用户喜欢的歌曲
      await initFavoriteSongs()
      
      // 预加载专辑和视频数据（如果当前就在这些标签页）
      if (currentTab.value === 'albums') {
        await loadFavoriteAlbums()
      } else if (currentTab.value === 'videos') {
        await loadFavoriteVideos()
      }
      
      // 初始化用户信息
      await initUserInfo()
      
      // 监听全局事件
      window.addEventListener('user-info-updated', handleUserInfoUpdate)
      window.addEventListener('background-changed', updateBackground)
      window.addEventListener('songLikeChanged', refreshFavoriteSongs)
      
      // 点击外部关闭下拉框
      document.addEventListener('click', closeDropdown)
      
      // 按ESC键关闭下拉框
      document.addEventListener('keydown', handleEscKey)
      
      // 滚动时关闭下拉框
      window.addEventListener('scroll', handleScroll)
      
      // 窗口大小改变时关闭下拉框
      window.addEventListener('resize', handleResize)
    })

    return {
      isLoggedIn,
      avatarImg,
      nickname,
      userBio,
      bannerBg,
      currentTab,
      avatarInput,
      likedSongs,
      nickname,
      userBio,
      bannerBg,
      currentTab,
      avatarInput,
      likedSongs,
      favoriteAlbums,
      loadingAlbums,
      favoriteVideos,
      loadingVideos,
      currentTabTitle,
      currentTabCount,
      switchTab,
      changeAvatar,
      onAvatarChange,
      playSong,
      playAll,
      removeFavorite,
      // 批量操作相关
      showBatchModal,
      showAlbumBatchModal,
      showVideoBatchModal,
      showBatchOptions,
      selectedSongs,
      selectedAlbums,
      selectedVideos,
      isSelectMode,
      toggleSongSelection,
      toggleAlbumSelection,
      toggleVideoSelection,
      selectAllSongs,
      selectAllAlbums,
      selectAllVideos,
      batchDeleteSongs,
      batchRemoveAlbums,
      batchRemoveVideos,
      closeBatchModal,
      // 下载相关
      showDownloadModal,
      downloadSongs_list,
      isDownloading,
      downloadProgress,
      currentDownloadIndex,
      downloadResults,
      downloadSong,
      downloadAll,
      showDownloadConfirm,
      closeDownloadModal,
      confirmDownloadSongs,
      isSongDownloadable,
      // 专辑和视频相关
      loadFavoriteAlbums,
      loadFavoriteVideos,
      goToAlbumDetail,
      goToVideoDetail,
      playAlbum,
      playVideo,
      goToMVPage,
      goToAlbumPage,
      formatDate,
      formatViews,
      formatDuration,
      // 更多下拉框相关
      activeDropdown,
      toggleDropdown,
      closeDropdown,
      goToSongDetail,
      goToArtistDetail,
      goToAlbumDetail,
      handleAddToPlayNext,
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.user-music-page {
  min-height: 100vh;
  background-color: var(--background);
  color: var(--text-primary);
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 80px;
}

/* 用户信息横幅 */
.user-banner {
  height: 400px;
  position: relative;
  overflow: hidden;
  margin-bottom: 0;
  background-color: var(--background-card);
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  filter: brightness(0.7);
}

.user-profile-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  z-index: 10;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.user-main-info {
  display: flex;
  justify-content: center;
  margin-bottom: 0;
}

.user-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.user-avatar-container {
  position: relative;
  margin-bottom: 0;
  transform: translateY(-20px); /* 向上移动头像 */
}

.user-avatar {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  border: 4px solid white;
  cursor: pointer;
  transition: transform 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-name-container {
  text-align: center;
}

.user-name {
  font-size: 28px;
  font-weight: bold;
  margin: -5px 0 0;
  color: white;
}

/* 背景更换按钮 */
.background-changer {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.bg-change-btn {
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s ease;
}

.bg-change-btn:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

.bg-icon {
  font-size: 18px;
}

/* 内容导航标签 */
.content-tabs {
  display: flex;
  gap: 40px;
  justify-content: center;
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  width: auto;
}

.tab-item {
  padding: 8px 0;
  font-size: 16px;
  cursor: pointer;
  position: relative;
  color: rgba(255, 255, 255, 0.7);
  transition: color 0.3s ease;
  border-bottom: 2px solid transparent;
}

.tab-item:hover {
  color: white;
}

.tab-item.active {
  color: white;
  font-weight: 500;
  border-bottom-color: #4ade80;
}


/* 内容区域 */
.content-area {
  background-color: var(--background-card);
  border-radius: 0;
  box-shadow: none;
  padding: var(--space-lg);
  margin-top: 0;
  border-top: 1px solid var(--border-color);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--border-color-light);
}

.content-title {
  display: flex;
  align-items: center;
}

.content-title h2 {
  font-size: 18px;
  margin-right: 10px;
  color: var(--text-color);
}

.count {
  color: var(--text-color-light);
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 6px 16px;
  border-radius: 30px;
  font-size: 14px;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn .play-icon-svg {
  width: 12px;
  height: 12px;
  margin-right: 6px;
}

.btn-primary {
  background-color: var(--primary);
  color: white;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background-color: var(--primary-dark);
}

.btn-secondary {
  background-color: var(--background-card);
  color: var(--text-primary);
  border: 1px solid var(--border);
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background-color: var(--primary);
  color: white;
  border-color: var(--primary);
}

.icon {
  margin-right: 6px;
}

.play-icon::before {
  content: '▶';
  font-size: 14px;
  margin-right: 6px;
}

.add-icon::before {
  content: '+';
  font-size: 14px;
  margin-right: 6px;
}

.download-icon::before {
  content: '↓';
  font-size: 14px;
  margin-right: 6px;
}

.batch-icon::before {
  content: '☰';
  font-size: 14px;
  margin-right: 6px;
}

/* 歌曲列表 */
.song-list-container {
  width: 100%;
  position: relative;
  overflow: visible;
}

.song-table-header {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color-light);
  color: var(--text-color-light);
  font-size: 14px;
  background-color: var(--card-bg);
}

.song-table-body {
  /* 禁用滚动效果，显示所有内容 */
  max-height: none;
  overflow: visible;
  overflow-x: hidden;
  overflow-y: visible;
  position: relative;
}

.song-row {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color-light);
  transition: background-color 0.2s ease;
  position: relative;
  border-radius: 8px;
  margin: 2px 0;
}

.song-row:hover {
  background-color: var(--row-hover-bg);
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
  flex: 0 0 60px;
  text-align: center;
  color: var(--text-color-light);
  font-size: 14px;
  justify-content: center;
  font-weight: 500;
  position: relative;
  padding: 0 8px;
  transition: color 0.2s ease;
}


.song-row:hover .col-index {
  color: var(--primary);
}

.song-row:hover .song-name {
  color: var(--primary);
}

.song-row:hover .col-artist {
  color: var(--primary);
}

.song-row:hover .col-album {
  color: var(--primary);
}

.song-row:hover .col-duration {
  color: var(--primary);
}

.col-song {
  flex: 2.5;
  min-width: 280px;
  padding: 0 8px;
}

.col-artist {
  flex: 1.2;
  min-width: 160px;
  color: var(--text-primary);
  padding: 0 8px;
  transition: color 0.2s ease;
}

.col-album {
  flex: 1.2;
  min-width: 160px;
  color: var(--text-primary);
  padding: 0 8px;
  transition: color 0.2s ease;
}

.col-duration {
  flex: 0 0 100px;
  text-align: right;
  color: var(--text-color-light);
  justify-content: flex-end;
  padding: 0 8px;
  transition: color 0.2s ease;
}

.col-actions {
  flex: 0 0 80px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 0 8px;
  overflow: visible;
}

/* 更多下拉框样式 */
.more-dropdown {
  position: relative;
  display: inline-block;
}

.more-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s ease, opacity 0.2s ease;
  color: var(--text-secondary);
  opacity: 0.7;
}

.more-btn:hover {
  color: var(--primary, #FF6B9B);
  opacity: 1;
}

.more-icon {
  width: 16px;
  height: 16px;
  transition: transform 0.2s ease;
}


.dropdown-menu {
  position: fixed;
  min-width: 200px;
  max-width: 250px;
  width: max-content;
  background: var(--background-card, white);
  border: 1px solid var(--border, rgba(0, 0, 0, 0.1));
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 99999;
  padding: 8px 0;
  max-height: none;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.dropdown-item {
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: transparent;
  text-align: left;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: var(--text-primary, #374151);
  transition: background-color 0.2s ease, color 0.2s ease;
}

.dropdown-item:hover {
  background-color: var(--background-hover, rgba(255, 107, 155, 0.1));
  color: var(--primary, #FF6B9B);
}

.dropdown-item.danger {
  color: var(--error, #ef4444);
}

.dropdown-item.danger:hover {
  background-color: rgba(239, 68, 68, 0.1);
  color: var(--error, #ef4444);
}

.item-icon {
  font-size: 16px;
  flex-shrink: 0;
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
  text-shadow: none;
  background: transparent;
}

.tag {
  display: inline-block;
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 2px;
  margin-left: 6px;
  vertical-align: middle;
}

.mv-tag {
  background-color: var(--mv-tag-bg);
  color: white;
}

.vip-tag {
  background-color: var(--vip-tag-bg);
  color: white;
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.6;
  transition: all 0.2s;
  color: var(--text-color-light);
}

.song-row:hover .action-btn {
  opacity: 1;
}

.action-btn:hover {
  background-color: var(--secondary-color);
  color: var(--text-color);
}

.play-btn .play-icon::before {
  margin-right: 0;
  font-size: 16px;
}

.download-btn {
  color: var(--success);
}

.download-btn:hover {
  background-color: var(--success);
  color: white;
}



.playlist-icon::before {
  content: '\1F3B5'; /* 乐谱符号 */
}

.detail-icon::before {
  content: '\1F4D6'; /* 笔记本符号 */
}

.artist-icon::before {
  content: '\1F9D1'; /* 人物符号 */
}

.album-icon::before {
  content: '\1F3B6'; /* 唱片符号 */
}

.share-icon::before {
  content: '\1F385'; /* 礼物分享符号 */
}

.link-icon::before {
  content: '\1F517'; /* 链接符号 */
}

.play-icon::before {
  content: '\25B6'; /* 播放符号 */
}

.add-icon::before {
  content: '\002B'; /* 加号 */
}

.download-icon::before {
  content: '\2193'; /* 下箭头 */
}

.remove-icon::before {
  content: '\00D7'; /* 乘号 */
}

/* 黑色主题特殊调整 */
[data-theme="black"] .content-area {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

[data-theme="black"] .song-table-header {
  background-color: var(--row-hover-bg);
}


.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid var(--border);
}

.modal-footer .btn {
  min-width: 80px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn:disabled:hover {
  background-color: initial;
  color: initial;
  border-color: initial;
}



/* 批量操作模态框样式 */
.batch-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.batch-modal-content {
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.batch-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.batch-header {
  padding: var(--space-md) var(--space-lg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--background);
}

.select-info {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
}

.batch-list {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-sm);
  max-height: 400px;
}

.batch-item {
  margin-bottom: var(--space-xs);
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  padding: var(--space-md);
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.checkbox-wrapper:hover {
  background: var(--background);
  border-color: var(--border-color);
}

.checkbox-wrapper input[type="checkbox"] {
  margin-right: var(--space-md);
  transform: scale(1.2);
  accent-color: var(--primary);
}



.song-artist {
  font-size: 12px;
  color: var(--text-secondary);
}

.playlist-info {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.playlist-cover-small {
  width: 40px;
  height: 40px;
  border-radius: var(--border-radius);
  object-fit: cover;
}

.playlist-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.playlist-name {
  font-weight: 500;
  color: var(--text-primary);
}

.playlist-desc {
  font-size: 12px;
  color: var(--text-secondary);
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 下载模态框样式 */
.download-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  animation: luxuryFadeIn 0.4s ease-out;
}

.download-modal-content {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 
    0 8px 32px rgba(31, 38, 135, 0.37),
    inset 0 1px 0 rgba(255, 255, 255, 0.3),
    0 40px 80px rgba(0, 0, 0, 0.3);
  animation: luxurySlideUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.download-modal-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.5), 
    transparent);
}

.download-content {
  padding: 30px;
  color: white;
  max-height: calc(80vh - 120px);
  overflow-y: auto;
}

.download-info {
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  animation: luxurySlideUp 0.4s ease-out;
  animation-delay: 0.1s;
  animation-fill-mode: both;
}

.info-item:nth-child(2) {
  animation-delay: 0.2s;
}

.info-item:nth-child(3) {
  animation-delay: 0.3s;
}

.info-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
  border-color: rgba(255, 255, 255, 0.2);
}

.info-icon {
  margin-right: 12px;
  font-size: 18px;
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.5));
}

/* 下载进度条 */
.download-progress {
  margin: 30px 0;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: luxurySlideUp 0.5s ease-out;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: white;
  font-weight: 600;
}

.progress-info span:first-child {
  animation: downloadingPulse 2s ease-in-out infinite;
}

.progress-info span:last-child {
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: percentGlow 2s ease-in-out infinite alternate;
}

.progress-bar {
  width: 100%;
  height: 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, 
    #4facfe 0%, 
    #00f2fe 25%,
    #667eea 50%,
    #764ba2 75%,
    #f093fb 100%);
  background-size: 200% 100%;
  border-radius: 8px;
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  animation: rainbowFlow 3s ease-in-out infinite;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.4), 
    transparent);
  animation: luxuryProgressShine 2s ease-in-out infinite;
}

.download-status {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.download-status span {
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 歌曲列表 */
.download-song-list {
  margin-top: 20px;
}

.list-header h4 {
  margin: 0 0 15px 0;
  color: white;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.song-list {
  max-height: 200px;
  overflow-y: auto;
}

.download-song-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.download-song-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
}

.download-song-item .song-info {
  flex: 1;
}

.download-song-item .song-name {
  display: block;
  font-weight: 500;
  color: white;
  margin-bottom: 4px;
}

.download-song-item .song-artist {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.file-status {
  margin-left: 10px;
}

.status-icon {
  font-size: 16px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 6px;
}

.status-icon.ready {
  color: #10b981;
  background: rgba(16, 185, 129, 0.2);
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.status-icon.unavailable {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.more-songs {
  text-align: center;
  padding: 10px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  font-style: italic;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

::-webkit-scrollbar {
  width: 0px;
  height: 0px;
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: transparent;
}

::-webkit-scrollbar-track {
  background: transparent;
}

/* 为Firefox浏览器隐藏滚动条 */
* {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

* {
  -ms-overflow-style: none;
}

body, html {
  overflow-x: hidden;
}

.user-music-page {
  overflow-x: hidden;
}

.user-music-login-page {
  height: 100vh !important;
  max-height: 100vh !important;
  width: 100vw !important;
  background-color: var(--background);
  overflow: hidden !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  z-index: 1 !important;
  margin: 0 !important;
  padding: 0 !important;
  box-sizing: border-box !important;
}

/* 当显示未登录页面时，禁用 body 滚动 */
body:has(.user-music-login-page),
html:has(.user-music-login-page) {
  overflow: hidden !important;
  height: 100vh !important;
  position: fixed !important;
  width: 100vw !important;
  margin: 0 !important;
  padding: 0 !important;
  top: 0 !important;
  left: 0 !important;
}

/* 强制禁用页面滚动，但不影响导航栏显示 */
body:has(.user-music-login-page) {
  overflow-x: hidden !important;
  overflow-y: hidden !important;
}

/* 确保登录页面容器无法滚动 */
.user-music-login-page {
  overflow: hidden !important;
  overscroll-behavior: none !important;
  touch-action: none !important;
  -webkit-overflow-scrolling: auto !important;
}


.batch-list {
  overflow-y: auto;
}

.download-content {
  overflow-y: auto;
}

.song-list {
  overflow-y: auto;
}

/* 标签页数量样式 */
.tab-count {
  margin-left: 6px;
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: normal;
}

.tab-item.active .tab-count {
  color: var(--primary);
}

/* 专辑列表样式 */
.album-list-container {
  width: 100%;
  min-height: 500px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.loading-albums {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  gap: 16px;
  color: var(--text-secondary);
}

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  padding: 20px 0;
  flex: 1;
}

.album-card {
  background: transparent;
  border-radius: 0;
  overflow: visible;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.album-card:hover .album-cover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.album-cover {
  position: relative;
  width: 220px;
  height: 220px;
  overflow: hidden;
  border-radius: 0;
  transition: all 0.3s ease;
}

.album-cover .cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.album-card:hover .cover-image {
  transform: scale(1.05);
}

.album-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.album-card:hover .album-overlay {
  opacity: 1;
}

.play-album-btn {
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.play-album-btn:hover {
  transform: scale(1.1);
}

.play-album-btn .play-icon-img {
  width: 20px;
  height: 20px;
  filter: brightness(0) invert(1);
}

.album-info {
  padding: 12px 0 0 0;
  text-align: center;
  width: 220px;
}

.album-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.2;
}

.album-artist {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.2;
}

.album-date {
  font-size: 11px;
  color: var(--text-tertiary);
  margin: 0;
  line-height: 1.2;
}

.empty-albums {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-secondary);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 20px;
  border: 2px dashed rgba(102, 126, 234, 0.2);
  margin: 40px auto;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  max-width: 600px;
  width: 100%;
}

.empty-albums:hover {
  border-color: rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.1);
}

/* 视频列表样式 */
.video-list-container {
  width: 100%;
  min-height: 500px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.loading-videos {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  gap: 16px;
  color: var(--text-secondary);
}

.videos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  padding: 20px 0;
  flex: 1;
}

.video-card {
  background: transparent;
  border-radius: 0;
  overflow: visible;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.video-card:hover .video-cover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.video-cover {
  position: relative;
  width: 320px;
  height: 180px;
  overflow: hidden;
  border-radius: 0;
  transition: all 0.3s ease;
}

.video-cover .cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  border-radius: 0;
}

.video-card:hover .cover-image {
  transform: scale(1.05);
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.video-card:hover .video-overlay {
  opacity: 1;
}

.play-video-btn {
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.play-video-btn:hover {
  transform: scale(1.1);
}

.play-video-btn .play-icon-svg {
  width: 20px;
  height: 20px;
  fill: white;
}


.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.video-info {
  padding: 12px 0 0 0;
  text-align: center;
  width: 320px;
}

.video-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.2;
}

.video-artist {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.2;
}

.video-date {
  font-size: 11px;
  color: var(--text-tertiary);
  margin: 0;
  line-height: 1.2;
}

.empty-videos {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-secondary);
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.05) 0%, rgba(0, 242, 254, 0.05) 100%);
  border-radius: 20px;
  border: 2px dashed rgba(79, 172, 254, 0.2);
  margin: 40px auto;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  max-width: 600px;
  width: 100%;
}

.empty-videos:hover {
  border-color: rgba(79, 172, 254, 0.4);
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.08) 0%, rgba(0, 242, 254, 0.08) 100%);
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(79, 172, 254, 0.1);
}

.empty-icon-container {
  position: relative;
  display: inline-block;
  margin-bottom: 24px;
}

.empty-icon-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.2) 0%, rgba(0, 242, 254, 0.2) 100%);
  border-radius: 50%;
  animation: iconPulse 2s ease-in-out infinite;
}

.empty-icon {
  position: relative;
  z-index: 2;
  font-size: 56px;
  display: block;
  padding: 20px;
  animation: iconFloat 3s ease-in-out infinite;
  filter: drop-shadow(0 4px 8px rgba(79, 172, 254, 0.3));
}

.empty-content {
  max-width: 400px;
  margin: 0 auto;
}

.empty-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.empty-description {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0 0 24px 0;
  line-height: 1.5;
}

.discover-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  padding: 14px 28px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.3);
  position: relative;
  overflow: hidden;
}

.discover-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
}

.discover-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(79, 172, 254, 0.4);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.discover-btn:hover::before {
  left: 100%;
}

.discover-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.3);
}

.btn-icon {
  font-size: 18px;
  animation: iconBounce 1.5s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
    opacity: 0.8;
  }
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-2px);
  }
}

/* 深色主题适配 */
[data-theme="black"] .empty-videos {
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.08) 0%, rgba(0, 242, 254, 0.08) 100%);
  border-color: rgba(79, 172, 254, 0.3);
}

[data-theme="black"] .empty-videos:hover {
  border-color: rgba(79, 172, 254, 0.5);
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.12) 0%, rgba(0, 242, 254, 0.12) 100%);
  box-shadow: 0 10px 30px rgba(79, 172, 254, 0.2);
}

[data-theme="black"] .empty-icon-bg {
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.3) 0%, rgba(0, 242, 254, 0.3) 100%);
}

[data-theme="black"] .empty-title {
  color: #ffffff;
}

[data-theme="black"] .empty-description {
  color: rgba(255, 255, 255, 0.7);
}

/* 黑色主题下的专辑播放按钮样式 */
[data-theme="black"] .play-album-btn {
  background: #ffffff !important;
  border: 1px solid #cccccc !important;
}

[data-theme="black"] .play-album-btn:hover {
  background: #f0f0f0 !important;
  border-color: #aaaaaa !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

[data-theme="black"] .play-album-btn .play-icon-img {
  filter: brightness(0) !important; /* 黑色三角形 */
}

[data-theme="black"] .play-video-btn {
  background: #ffffff !important;
  border: 1px solid #cccccc !important;
}

[data-theme="black"] .play-video-btn:hover {
  background: #f0f0f0 !important;
  border-color: #aaaaaa !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

[data-theme="black"] .play-video-btn .play-icon-svg {
  fill: #000000 !important; /* 黑色三角形 */
}

/* 背景选择器模态框样式 */
.background-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.background-modal-content {
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 800px;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border);
  background: var(--background);
}

.modal-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: var(--background-hover);
  color: var(--text-primary);
}

.background-options {
  padding: 20px 24px;
  overflow-y: auto;
  flex: 1;
}

.option-section {
  margin-bottom: 30px;
}

.option-section h4 {
  margin: 0 0 16px 0;
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
}

.background-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}

.background-option {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
}

.background-option:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.background-option.active {
  border-color: var(--primary);
}

.background-option img {
  width: 100%;
  height: 100px;
  object-fit: cover;
  display: block;
}

.bg-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-name {
  color: white;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
}

.custom-upload {
  text-align: center;
  padding: 20px;
  border: 2px dashed var(--border);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.custom-upload:hover {
  border-color: var(--primary);
  background: var(--background-hover);
}

.upload-btn {
  background: var(--primary);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.upload-btn:hover {
  background: var(--primary-dark);
}

.upload-icon {
  font-size: 16px;
}

.upload-tip {
  margin-top: 12px;
  font-size: 12px;
  color: var(--text-secondary);
}

/* 黑色主题下的背景选择器样式 */
[data-theme="black"] .background-modal-content {
  background: #000000 !important;
  border: 1px solid #333333 !important;
}

[data-theme="black"] .modal-header {
  background: #000000 !important;
  border-bottom: 1px solid #333333 !important;
}

[data-theme="black"] .modal-header h3 {
  color: #ffffff !important;
}

[data-theme="black"] .close-btn {
  color: #cccccc !important;
}

[data-theme="black"] .close-btn:hover {
  background: #1a1a1a !important;
  color: #ffffff !important;
}

[data-theme="black"] .background-options {
  background: #000000 !important;
}

[data-theme="black"] .option-section {
  background: #000000 !important;
}

[data-theme="black"] .option-section h4 {
  color: #ffffff !important;
}

[data-theme="black"] .background-grid {
  background: #000000 !important;
}

[data-theme="black"] .background-option {
  border-color: #333333 !important;
}

[data-theme="black"] .background-option.active {
  border-color: #ffffff !important;
}

[data-theme="black"] .custom-upload {
  background: #000000 !important;
  border-color: #333333 !important;
}

[data-theme="black"] .custom-upload:hover {
  background: #1a1a1a !important;
  border-color: #ffffff !important;
}

[data-theme="black"] .upload-btn {
  background: #000000 !important;
  color: #ffffff !important;
  border: 1px solid #ffffff !important;
}

[data-theme="black"] .upload-btn:hover {
  background: #1a1a1a !important;
  border-color: #ffffff !important;
}

[data-theme="black"] .upload-tip {
  color: #cccccc !important;
}

[data-theme="black"] .bg-overlay {
  background: rgba(0, 0, 0, 0.8) !important;
}

[data-theme="black"] .bg-name {
  color: #ffffff !important;
}

/* 专辑空状态深色主题适配 */
[data-theme="black"] .empty-albums {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
  border-color: rgba(102, 126, 234, 0.3);
}

[data-theme="black"] .empty-albums:hover {
  border-color: rgba(102, 126, 234, 0.5);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, rgba(118, 75, 162, 0.12) 100%);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.2);
}

/* 全新的未登录页面设计 */
.user-music-login-page {
  min-height: calc(100vh - 152px); /* 减去导航栏72px + 播放器80px */
  width: 100%;
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--background) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 0;
  box-sizing: border-box;
  position: relative;
  margin-top: 0;
}

/* 背景装饰元素 */
.user-music-login-page::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 70% 30%, rgba(255, 255, 255, 0.08) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
  z-index: 1;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 主题特定背景 */
[data-theme="pink"] .user-music-login-page {
  background: linear-gradient(135deg, #fce7f3 0%, #fdf2f8 50%, #fef7f0 100%);
}

[data-theme="lightPink"] .user-music-login-page {
  background: linear-gradient(135deg, #fce7ed 0%, #fef9fa 50%, #fff5f7 100%);
}

[data-theme="blue"] .user-music-login-page {
  background: linear-gradient(135deg, #dbeafe 0%, #eff6ff 50%, #f0f9ff 100%);
}

[data-theme="green"] .user-music-login-page {
  background: linear-gradient(135deg, #dcfce7 0%, #f0fdf4 50%, #f7fee7 100%);
}

[data-theme="purple"] .user-music-login-page {
  background: linear-gradient(135deg, #f3e8ff 0%, #faf5ff 50%, #fefbff 100%);
}

[data-theme="orange"] .user-music-login-page {
  background: linear-gradient(135deg, #fed7aa 0%, #fff7ed 50%, #fffbeb 100%);
}

[data-theme="red"] .user-music-login-page {
  background: linear-gradient(135deg, #fecaca 0%, #fef2f2 50%, #fffbfb 100%);
}

[data-theme="yellow"] .user-music-login-page {
  background: linear-gradient(135deg, #fef3c7 0%, #fefce8 50%, #fefdf0 100%);
}

[data-theme="white"] .user-music-login-page {
  background: linear-gradient(135deg, #f9fafb 0%, #ffffff 50%, #f3f4f6 100%);
}

[data-theme="black"] .user-music-login-page {
  background: linear-gradient(135deg, #1f2937 0%, #111827 50%, #000000 100%);
}

/* 响应式优化 */
@media (max-height: 800px) {
  .user-music-login-page {
    min-height: auto;
    padding: 1rem 0;
  }
}

@media (max-width: 1024px) {
  .user-music-login-page {
    padding: 1.5rem 0;
  }
}

@media (max-width: 768px) {
  .user-music-login-page {
    padding: 1rem 0;
    min-height: calc(100vh - 152px);
  }
  
  .user-banner {
    height: 400px;
  }

  .user-avatar {
    width: 130px;
    height: 130px;
  }

  .user-name {
    font-size: 18px;
  }
}


</style>
