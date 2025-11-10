// 首页
<template>
  <div class="home-page">
    <!-- 修复背景主题和布局结构 -->
    <div class="banner-section">
      <!-- 使用HomeCarousel组件 -->
      <HomeCarousel />
    </div>

    <div class="content-container">
      <!-- 推荐歌单区域 -->
      <section class="playlist-section">
        <div class="section-title-wrap center-title">
          <h2 class="section-title">推荐歌单</h2>
          <div class="section-line"></div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loadingStates.playlists" class="loading-container">
          <div class="loading-spinner"></div>
          <div class="loading-text">正在加载推荐歌单...</div>
        </div>
        
        <!-- 数据内容 -->
        <template v-else>
          <button class="slider-btn slider-btn-left" @click="prevPlaylistPage" v-show="playlistTotalPages > 1 && playlistCardsFullyVisible">
            <i class="left-icon"></i>
          </button>
          <div class="playlist-slider-container">
            <div class="playlist-list">
              <div v-for="p in playlistPageList" :key="p.id" class="chaoshi-card" @click="goTo(`/playlist/${p.id}`)">
                <div class="chaoshi-image-container">
                  <img :src="p.cover || require('@/assets/1音乐.png')" :alt="p.name" class="chaoshi-image" />
                  <div class="chaoshi-overlay">
                    <div class="chaoshi-play-btn">
                      <svg class="chaoshi-play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                        <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="chaoshi-info">
                  <div class="chaoshi-title">{{ p.name }}</div>
                </div>
              </div>
            </div>
          </div>
          <button class="slider-btn slider-btn-right" @click="nextPlaylistPage" v-show="playlistTotalPages > 1 && playlistCardsFullyVisible">
            <i class="right-icon"></i>
          </button>
          <div class="playlist-dots" v-show="playlistTotalPages > 1">
            <span
              v-for="(dot, idx) in playlistTotalPages"
              :key="idx"
              :class="['dot', {active: idx === playlistPage}]"
              @click="goPlaylistPage(idx)"
            ></span>
          </div>
        </template>
      </section>

      <!-- 热门歌手区域 -->
      <section class="artist-section">
        <div class="section-title-wrap toplist-title-wrap">
          <h2 class="section-title">热门歌手</h2>
          <div class="section-line"></div>
          <div class="section-more" @click="goTo('/artist')">更多 ></div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loadingStates.artists" class="loading-container">
          <div class="loading-spinner"></div>
          <div class="loading-text">正在加载歌手数据...</div>
        </div>
        
        <!-- 数据内容 -->
        <template v-else>
          <button class="slider-btn slider-btn-left" @click="prevArtistPage" v-show="artistTotalPages > 1 && artistCardsFullyVisible">
            <i class="left-icon"></i>
          </button>
          <div class="artist-slider-container">
            <div class="artist-list">
              <div v-for="(a, index) in artistPageList" :key="a.id" class="chaoshi-card" @click="goTo(`/artist/${a.id}`)" :title="`点击查看${a.name}的详情`">
                <div class="chaoshi-image-container">
                  <img :src="a.avatar" :alt="a.name" class="chaoshi-image chaoshi-artist-avatar" />
                  <div class="chaoshi-rank">{{ index + 1 }}</div>
                </div>
                <div class="chaoshi-info">
                  <div class="chaoshi-title">{{ a.name }}</div>
                  <div class="chaoshi-subtitle">{{ a.fansCount ? `${a.fansCount} 粉丝` : '新晋歌手' }}</div>
                </div>
              </div>
            </div>
          </div>
          <button class="slider-btn slider-btn-right" @click="nextArtistPage" v-show="artistTotalPages > 1 && artistCardsFullyVisible">
            <i class="right-icon"></i>
          </button>
          <div class="playlist-dots" v-show="artistTotalPages > 1">
            <span
              v-for="(dot, idx) in artistTotalPages"
              :key="idx"
              :class="['dot', {active: idx === artistPage}]"
              @click="goArtistPage(idx)"
            ></span>
          </div>
        </template>
      </section>

      <!-- 热门专辑区域 -->
      <section class="album-section">
        <div class="section-title-wrap toplist-title-wrap">
          <h2 class="section-title">热门专辑</h2>
          <div class="section-line"></div>
          <div class="section-more" @click="goTo('/album')">更多 ></div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loadingStates.albums" class="loading-container">
          <div class="loading-spinner"></div>
          <div class="loading-text">正在加载专辑数据...</div>
        </div>
        
        <!-- 数据内容 -->
        <template v-else>
          <button class="slider-btn slider-btn-left" @click="prevAlbumPage" v-show="albumTotalPages > 1 && albumCardsFullyVisible">
            <i class="left-icon"></i>
          </button>
          <div class="album-slider-container">
            <div class="album-list">
              <div v-for="(a, index) in albumPageList" :key="a.id" class="chaoshi-card" @click="goTo(`/album/${a.id}`)" :title="`点击查看${a.name}的详情`">
                <div class="chaoshi-image-container">
                  <img :src="a.cover || require('@/assets/1音乐.png')" :alt="a.name" class="chaoshi-image" />
                  <div class="chaoshi-overlay">
                    <div class="chaoshi-play-btn" @click.stop="playAlbum(a)">
                      <svg class="chaoshi-play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                        <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
                      </svg>
                    </div>
                    <div class="chaoshi-rank">{{ index + 1 }}</div>
                  </div>
                </div>
                <div class="chaoshi-info">
                  <div class="chaoshi-title">{{ a.name }}</div>
                  <div class="chaoshi-subtitle">{{ a.artist }}</div>
                </div>
              </div>
            </div>
          </div>
          <button class="slider-btn slider-btn-right" @click="nextAlbumPage" v-show="albumTotalPages > 1 && albumCardsFullyVisible">
            <i class="right-icon"></i>
          </button>
          <div class="playlist-dots" v-show="albumTotalPages > 1">
            <span
              v-for="(dot, idx) in albumTotalPages"
              :key="idx"
              :class="['dot', {active: idx === albumPage}]"
              @click="goAlbumPage(idx)"
            ></span>
          </div>
        </template>
      </section>

      <!-- 热门歌曲区域 -->
      <section class="song-section">
        <div class="section-title-wrap toplist-title-wrap">
            <h2 class="section-title">热门歌曲</h2>
            <div class="section-line"></div>
            <div class="section-more" @click="goTo('/toplist/1')">更多 ></div>
          </div>
        
        <!-- 加载状态 -->
        <div v-if="loadingStates.songs" class="loading-container">
          <div class="loading-spinner"></div>
          <div class="loading-text">正在加载歌曲数据...</div>
        </div>
        
        <!-- 数据内容 -->
        <template v-else>
          <button class="slider-btn slider-btn-left" @click="prevSongPage" v-show="songTotalPages > 1 && songCardsFullyVisible">
            <i class="left-icon"></i>
          </button>
          <div class="song-slider-container">
            <div class="song-list">
              <div v-for="(s, index) in songPageList" :key="s.id" class="chaoshi-card">
                <div class="chaoshi-image-container" @click="goTo(`/song/${s.id}`)" :title="`点击查看${s.name}的详情`">
                  <img :src="s.cover || require('@/assets/1音乐.png')" :alt="s.name" class="chaoshi-image" />
                  <div class="chaoshi-overlay">
                    <div class="chaoshi-play-btn" @click.stop="playSong(s)">
                      <svg class="chaoshi-play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                        <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
                      </svg>
                    </div>
                    <div class="chaoshi-rank">{{ index + 1 }}</div>
                  </div>
                </div>
                <div class="chaoshi-info">
                  <div class="chaoshi-title clickable-item" @click.stop="goToSongDetail(s)">{{ s.name }}</div>
                  <div class="chaoshi-subtitle clickable-item" @click.stop="goToArtistDetail(s)">{{ s.artist }}{{ s.playCount ? ` | ${formatPlayCount(s.playCount)}` : '' }}</div>
                </div>
              </div>
            </div>
          </div>
          <button class="slider-btn slider-btn-right" @click="nextSongPage" v-show="songTotalPages > 1 && songCardsFullyVisible">
            <i class="right-icon"></i>
          </button>
          <div class="playlist-dots" v-show="songTotalPages > 1">
            <span
              v-for="(dot, idx) in songTotalPages"
              :key="idx"
              :class="['dot', {active: idx === songPage}]"
              @click="goSongPage(idx)"
            ></span>
          </div>
        </template>
      </section>

      <!-- 排行榜区域 -->
      <section class="toplist-section">
        <div class="section-title-wrap toplist-title-wrap">
          <h2 class="section-title">排行榜</h2>
          <div class="section-line"></div>
          <div class="section-more" @click="goTo('/toplist')">更多 ></div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loadingStates.toplists" class="loading-container">
          <div class="loading-spinner"></div>
          <div class="loading-text">正在加载排行榜数据...</div>
        </div>
        
        <!-- 数据内容 -->
        <div v-else class="toplist-container">
          <div 
            v-for="(toplist, index) in Array.isArray(hotToplists) ? hotToplists.slice(0, 5) : []" 
            :key="toplist.id" 
            class="toplist-card"
            :class="`toplist-${index + 1}`"
            @click="goTo(`/toplist/${toplist.id}`)"
          >
            <!-- 排行榜标题和播放按钮区域 -->
            <div class="toplist-header">
              <div class="toplist-title-section">
                <div class="toplist-name">{{ toplist.name || '流行指数' }}</div>
                <div class="toplist-play-container">
                  <div class="toplist-line"></div>
                  <div class="toplist-play-btn" @click.stop="playToplist(toplist)">
                    <svg class="toplist-play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                      <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
                    </svg>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="toplist-songs">
              <div v-if="loadingStates[`toplist_${toplist.id}`]" class="toplist-loading">
                <div class="loading-spinner small"></div>
                <span>加载中...</span>
              </div>
              <template v-else>
                <div 
                  v-for="(song, songIndex) in getToplistSongsByCache(toplist, 5)" 
                  :key="song.id || `song_${toplist.id}_${songIndex}`" 
                  class="toplist-song-item"
                  @click.stop="playSong(song)"
                >
                  <span class="song-rank">{{ songIndex + 1 }}</span>
                  <div class="song-info">
                    <div class="song-title clickable-item" @click.stop="goToSongDetail(song)">{{ song.name || song.title || '未知歌曲' }}</div>
                    <div class="song-artist clickable-item" @click.stop="goToArtistDetail(song)">{{ 
                      // 处理多种可能的艺人数据格式
                      song.artist || 
                      song.artistName || 
                      (song.artists && song.artists.length > 0 ? song.artists.map(a => a.name).join('、') : '未知艺人') ||
                      '未知艺人'
                    }}</div>
                  </div>
                </div>
                <!-- 空状态显示 -->
                <div v-if="getToplistSongsByCache(toplist, 5).length === 0" class="toplist-empty">
                  暂无歌曲数据
                </div>
              </template>
            </div>
          </div>
        </div>
      </section>

      <!-- 当红歌手区域 -->
      <section class="song-highlights">
        <div class="section-title-wrap center-title">
          <h2 class="section-title">当红歌手</h2>
          <div class="section-line"></div>
        </div>
        <div class="highlights-container">
          <div v-for="(artist, idx) in (hotArtists || []).slice(0, 9)" :key="artist.id" :class="['highlight-item', {active: idx === 0}]" @click="goTo(`/artist/${artist.id}`)">
            <img :src="artist.avatar" :alt="artist.name" class="highlight-img" />
            <div class="highlight-name">{{ artist.name }}</div>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getBanners, getRecommendPlaylists, getHotArtists, getHotAlbums, getHotSongs, getHotToplists, getToplistSongs as fetchToplistSongs } from '@/api/home.js'
import { getToplistSongs as getToplistSongsDetail } from '@/api/toplist.js'
import { ElMessage } from 'element-plus'
import { playSong as playMusic } from '@/utils/musicPlayer.js'
import HomeCarousel from '@/components/HomeCarousel.vue'

const router = useRouter()
const route = useRoute()
const goTo = (path) => {
  if (route.path !== path) {
    router.push(path)
  }
}

// 跳转到歌曲详情页
const goToSongDetail = (song) => {
  if (!song || !song.id) {
    // CONSOLE LOG REMOVED: console.warn('歌曲ID不存在');
    return;
  }
  const songId = song.id;
  goTo(`/song/${songId}`);
};

// 跳转到歌手详情页
const goToArtistDetail = (song) => {
  if (!song) {
    // CONSOLE LOG REMOVED: console.warn('歌曲信息不存在');
    return;
  }
  
  // 尝试多种可能的歌手ID字段
  let artistId = song.artistId || song.artist_id || song.singerId;
  
  // 如果没有直接的歌手ID，尝试从艺术家数组中获取
  if (!artistId && song.artists && song.artists.length > 0) {
    artistId = song.artists[0].id;
  }
  
  if (!artistId) {
    // CONSOLE LOG REMOVED: console.warn('歌曲中未找到歌手ID，无法跳转到歌手详情页');
    return;
  }
  
  goTo(`/artist/${artistId}`);
};

// 初始化hotToplists变量
const hotToplists = ref([])

const toplistSongsCache = ref({})
const loadingStates = ref({
  banners: true,
  playlists: true,
  artists: true,
  albums: true,
  songs: true,
  toplists: true
})
const formatPlayCount = (count) => {
  if (!count) return '0次播放';
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万次播放';
  }
  return count + '次播放';
};

const playAlbum = async (album) => {
  // CONSOLE LOG REMOVED: console.log('🎵 点击播放专辑:', album);
  
  if (!album || !album.id) {
    ElMessage.warning('专辑信息不完整');
    return;
  }
  
  try {
    ElMessage.success(`开始播放专辑《${album.name}》`);
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('播放专辑错误:', error);
    ElMessage.error('播放失败，请稍后重试');
  }
};

const playSong = async (song) => {
  // CONSOLE LOG REMOVED: console.log('🎵 点击播放歌曲:', song)
  if (!song || !song.id) {
    ElMessage.warning('歌曲信息不完整')
    return
  }

  try {
    const success = await playMusic(song)
    
    if (success) {
      // CONSOLE LOG REMOVED: console.log('播放成功')
    } else {
      // CONSOLE LOG REMOVED: console.log('播放失败')
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('播放错误:', error)
   
  }
}

const getToplistSongsByCache = (toplist, limit = 5) => {
  if (!toplist || !toplist.id || !toplistSongsCache.value) {
    return []
  }
  const cacheKey = `${toplist.id}_${limit}`
  const songs = toplistSongsCache.value[cacheKey]
  return Array.isArray(songs) ? songs : []
}

const playToplist = async (toplist) => {
  try {
    // CONSOLE LOG REMOVED: console.log('准备播放排行榜:', toplist)
    let songs = getToplistSongsByCache(toplist, 10)
    if (!songs || songs.length === 0) {
      songs = await loadToplistSongs(toplist, 10)
    }
    if (songs && songs.length > 0) {
      await playMusic(songs[0])
      ElMessage.success(`开始播放 ${toplist.name}`)
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('播放排行榜错误:', error)
    ElMessage.error('播放失败: ' + error.message)
  }
}

onMounted(async () => {
  try {
    loadingStates.value.toplists = true
    const toplists = await getHotToplists(10)
    hotToplists.value = toplists || []

    if (hotToplists.value.length > 0) {
      const loadPromises = hotToplists.value.slice(0, 5).map(toplist => 
        loadToplistSongs(toplist, 5)
      )
      await Promise.all(loadPromises)
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('获取排行榜数据失败:', error)
    hotToplists.value = [
      { id: 1, name: '热歌榜', cover: 'https://via.placeholder.com/200/ff7700/ffffff?text=热歌榜' },
      { id: 2, name: '新歌榜', cover: 'https://via.placeholder.com/200/0099ff/ffffff?text=新歌榜' },
      { id: 3, name: '飙升榜', cover: 'https://via.placeholder.com/200/66cc99/ffffff?text=飙升榜' },
      { id: 4, name: '原创榜', cover: 'https://via.placeholder.com/200/cc66ff/ffffff?text=原创榜' },
      { id: 5, name: '流行榜', cover: 'https://via.placeholder.com/200/ff6666/ffffff?text=流行榜' }
    ]

    for (const toplist of hotToplists.value) {
      await loadToplistSongs(toplist, 5)
    }
  } finally {
    loadingStates.value.toplists = false
  }
})

const loadToplistSongs = async (toplist, limit = 10) => {
  if (!toplist || !toplist.id) {
    // CONSOLE LOG REMOVED: console.warn('排行榜ID不存在')
    return []
  }

  const cacheKey = `${toplist.id}_${limit}`
  if (toplistSongsCache.value && toplistSongsCache.value[cacheKey]) {
    return toplistSongsCache.value[cacheKey]
  }
  
  try {
    const loadingKey = `toplist_${toplist.id}`
    loadingStates.value[loadingKey] = true

    let songs = []
    let apiSource = '未知'
    
    try {
      apiSource = '详情页API'
      // CONSOLE LOG REMOVED: console.log(`优先使用详情页API获取 ${toplist.name} 歌曲数据`)
      const detailResponse = await getToplistSongsDetail(toplist.id, 1, limit)
      
      // CONSOLE LOG REMOVED: console.log(`详情页API响应:`, detailResponse)
      
      if (detailResponse) {
        if (detailResponse.code === 200 || detailResponse.code === 0) {
          if (Array.isArray(detailResponse.data)) {
            songs = detailResponse.data
          } else if (detailResponse.data && Array.isArray(detailResponse.data.content)) {
            songs = detailResponse.data.content
          } else if (detailResponse.data && Array.isArray(detailResponse.data.songs)) {
            songs = detailResponse.data.songs
          } else if (detailResponse.data && Array.isArray(detailResponse.data.list)) {
            songs = detailResponse.data.list
          }
        } else if (Array.isArray(detailResponse)) {
          songs = detailResponse
        }
      }
    } catch (detailError) {
      // CONSOLE LOG REMOVED: console.warn(`详情页API获取${toplist.name}歌曲失败:`, detailError.message)
    }
    
    if (!songs || songs.length === 0) {
      try {
        apiSource = '首页API'
        // CONSOLE LOG REMOVED: console.log(`降级尝试使用首页API获取 ${toplist.name} 歌曲数据`)
        const homeResponse = await fetchToplistSongs(toplist.id, limit)
        
        // CONSOLE LOG REMOVED: console.log(`首页API响应:`, homeResponse)
        
        if (homeResponse) {
          if (homeResponse.code === 200 || homeResponse.code === 0) {
            if (Array.isArray(homeResponse.data)) {
              songs = homeResponse.data
            } else if (homeResponse.data && Array.isArray(homeResponse.data.content)) {
              songs = homeResponse.data.content
            } else if (homeResponse.data && Array.isArray(homeResponse.data.songs)) {
              songs = homeResponse.data.songs
            } else if (homeResponse.data && Array.isArray(homeResponse.data.list)) {
              songs = homeResponse.data.list
            }
          } else if (Array.isArray(homeResponse)) {
            songs = homeResponse
          }
        }
      } catch (homeApiError) {
        // CONSOLE LOG REMOVED: console.warn(`首页API获取${toplist.name}歌曲失败:`, homeApiError.message)
      }
    }

    if (songs && songs.length > 0) {
      songs = songs.map(song => {
        let artistName = '未知艺人'
        if (song.artist || song.artistName || song.singer) {
          artistName = song.artist || song.artistName || song.singer
        } else if (song.artists && Array.isArray(song.artists)) {
          if (song.artists.length > 0 && song.artists[0].name) {
            artistName = song.artists.map(a => a.name).join('、')
          } else if (song.artists.length > 0) {
            artistName = song.artists.join('、')
          }
        }
        
        return {
          ...song, 
          id: song.id,
          name: song.name || song.title || '未知歌曲',
          artist: artistName,
          duration: song.duration || song.time || '03:45'
        }
      })
      
      // CONSOLE LOG REMOVED: console.log(`从${apiSource}获取到${toplist.name}的歌曲:`, songs.length, '首')
    } else {
      // CONSOLE LOG REMOVED: console.warn(`${toplist.name} 所有API都未获取到有效歌曲数据，将显示空状态`)
      songs = [] 
    }

    if (toplistSongsCache.value) {
      toplistSongsCache.value[cacheKey] = songs
      // CONSOLE LOG REMOVED: console.log(`缓存${toplist.name}歌曲数据:`, songs.length, '首')
    }
    
    return songs
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error(`获取排行榜${toplist.name}歌曲发生严重错误:`, error)
    const fallbackSongs = [
      { id: 1001 + toplist.id, name: '热门歌曲' + toplist.id, artist: '歌手' + toplist.id },
      { id: 1002 + toplist.id, name: '排行榜歌曲' + toplist.id, artist: '艺人' + toplist.id },
      { id: 1003 + toplist.id, name: '流行音乐' + toplist.id, artist: '表演者' + toplist.id }
    ]
    const limitedSongs = fallbackSongs.slice(0, limit)
    
    if (toplistSongsCache.value) {
      toplistSongsCache.value[cacheKey] = limitedSongs
    }
    
    return limitedSongs
  } finally {
    const loadingKey = `toplist_${toplist.id}`
    loadingStates.value[loadingKey] = false
  }
}

const banners = ref([])
const recommendPlaylists = ref([])
const hotArtists = ref([])
const hotAlbums = ref([])
const hotSongs = ref([])
const loading = ref(true)

const loadingProgress = computed(() => {
  const total = Object.keys(loadingStates.value).length
  const completed = Object.values(loadingStates.value).filter(state => !state).length
  return Math.round((completed / total) * 100)
})

const hasAnyLoading = computed(() => {
  return Object.values(loadingStates.value).some(state => state)
})


// banner数据仍保留，以备后续功能需要
// 轮播逻辑已移至HomeCarousel组件

async function loadHomeData() {
  try {
    loading.value = true

    const loadTasks = [
      { key: 'banners', request: getBanners() },
      { key: 'playlists', request: getRecommendPlaylists(10) },
      { key: 'artists', request: getHotArtists(10) },
      { key: 'albums', request: getHotAlbums(10) },
      { key: 'songs', request: getHotSongs(10) },
      { key: 'toplists', request: getHotToplists(5) }
    ]
    
    const promises = loadTasks.map(async (task) => {
      try {
        const response = await task.request

        let data = []
        if (response && response.code === 200) {
          data = response.data || []
        }

        switch (task.key) {
          case 'banners':
            banners.value = data
            break
          case 'playlists':
            recommendPlaylists.value = data
            break
          case 'artists':
            hotArtists.value = data
            break
          case 'albums':
            hotAlbums.value = data
            break
          case 'songs':
            hotSongs.value = data
            break
          case 'toplists':
            // CONSOLE LOG REMOVED: console.log('排序前的排行榜数据:', data.map(item => item.name))
            // 按照指定顺序排序排行榜：飙升榜、原创榜、潮石热歌榜、新歌榜、新专辑榜
            const desiredOrder = ['飙升榜', '原创榜', '潮石热歌榜', '新歌榜', '新专辑榜']
            hotToplists.value = [...data].sort((a, b) => {
              const indexA = desiredOrder.indexOf(a.name)
              const indexB = desiredOrder.indexOf(b.name)
              // 按指定顺序排列，如果不在指定列表中则放在后面
              if (indexA !== -1 && indexB !== -1) {
                return indexA - indexB
              }
              if (indexA !== -1) return -1
              if (indexB !== -1) return 1
              return 0
            })
            // CONSOLE LOG REMOVED: console.log('排序后的排行榜数据:', hotToplists.value.map(item => item.name))
            break
        }
        
        // 标记该区域加载完成
        loadingStates.value[task.key] = false
        
        // CONSOLE LOG REMOVED: console.log(`${task.key} 数据加载完成:`, data.length)
        
        if (task.key === 'toplists' && hotToplists.value.length > 0) {
          // CONSOLE LOG REMOVED: console.log('开始预加载排行榜歌曲数据...')
          hotToplists.value.slice(0, 5).forEach(async (toplist) => {
            try {
              await loadToplistSongs(toplist, 5)
              // CONSOLE LOG REMOVED: console.log(`${toplist.name} 歌曲预加载完成`)
            } catch (error) {
              // CONSOLE LOG REMOVED: console.error(`${toplist.name} 歌曲预加载失败:`, error)
            }
          })
        }
        
      } catch (error) {
        // CONSOLE LOG REMOVED: console.error(`${task.key} 数据加载失败:`, error)
        loadingStates.value[task.key] = false
      }
    })

    await Promise.allSettled(promises)
    
    // CONSOLE LOG REMOVED: console.log('首页所有数据加载完成')
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('首页数据加载失败:', error)
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  await loadHomeData()

  window.addEventListener('scroll', handleScroll)
  window.addEventListener('resize', handleScroll)
  handleScroll()

  setTimeout(() => {
    handleScroll()
  }, 100)
})
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', handleScroll)
})

// 推荐歌单分页轮播
const playlistPage = ref(0)
const pageSize = 5
const playlistAnimating = ref(false)
const playlistCardsFullyVisible = ref(false) // 新增：卡片100%可见性状态
const playlistTotalPages = computed(() => Math.ceil((recommendPlaylists.value?.length || 0) / pageSize))
const playlistPageList = computed(() => {
  const start = playlistPage.value * pageSize
  return (recommendPlaylists.value || []).slice(start, start + pageSize)
})

function goPlaylistPage(idx) {
  if (playlistAnimating.value) return
  const direction = idx > playlistPage.value ? 'next' : 'prev'
  triggerSlideAnimation('.playlist-list', direction, () => {
    playlistPage.value = idx
  })
}

function prevPlaylistPage() {
  if (playlistAnimating.value) return
  triggerSlideAnimation('.playlist-list', 'prev', () => {
    playlistPage.value = (playlistPage.value - 1 + playlistTotalPages.value) % playlistTotalPages.value
  })
}

function nextPlaylistPage() {
  if (playlistAnimating.value) return
  triggerSlideAnimation('.playlist-list', 'next', () => {
    playlistPage.value = (playlistPage.value + 1) % playlistTotalPages.value
  })
}

// 热门歌手分页轮播
const artistPage = ref(0)
const artistAnimating = ref(false)
const artistCardsFullyVisible = ref(false) // 新增：卡片100%可见性状态
const artistTotalPages = computed(() => Math.ceil((hotArtists.value?.length || 0) / pageSize))
const artistPageList = computed(() => {
  const start = artistPage.value * pageSize
  return (hotArtists.value || []).slice(start, start + pageSize)
})

function goArtistPage(idx) {
  if (artistAnimating.value) return
  const direction = idx > artistPage.value ? 'next' : 'prev'
  triggerSlideAnimation('.artist-list', direction, () => {
    artistPage.value = idx
  })
}

function prevArtistPage() {
  if (artistAnimating.value) return
  triggerSlideAnimation('.artist-list', 'prev', () => {
    artistPage.value = (artistPage.value - 1 + artistTotalPages.value) % artistTotalPages.value
  })
}

function nextArtistPage() {
  if (artistAnimating.value) return
  triggerSlideAnimation('.artist-list', 'next', () => {
    artistPage.value = (artistPage.value + 1) % artistTotalPages.value
  })
}

// 热门专辑分页轮播
const albumPage = ref(0)
const albumAnimating = ref(false)
const albumCardsFullyVisible = ref(false) // 新增：卡片100%可见性状态
const albumTotalPages = computed(() => Math.ceil((hotAlbums.value?.length || 0) / pageSize))
const albumPageList = computed(() => {
  const start = albumPage.value * pageSize
  return (hotAlbums.value || []).slice(start, start + pageSize)
})

function goAlbumPage(idx) {
  if (albumAnimating.value) return
  const direction = idx > albumPage.value ? 'next' : 'prev'
  triggerSlideAnimation('.album-list', direction, () => {
    albumPage.value = idx
  })
}

function prevAlbumPage() {
  if (albumAnimating.value) return
  triggerSlideAnimation('.album-list', 'prev', () => {
    albumPage.value = (albumPage.value - 1 + albumTotalPages.value) % albumTotalPages.value
  })
}

function nextAlbumPage() {
  if (albumAnimating.value) return
  triggerSlideAnimation('.album-list', 'next', () => {
    albumPage.value = (albumPage.value + 1) % albumTotalPages.value
  })
}

// 热门歌曲分页轮播
const songPage = ref(0)
const songAnimating = ref(false)
const songCardsFullyVisible = ref(false) // 新增：卡片100%可见性状态
const songTotalPages = computed(() => Math.ceil((hotSongs.value?.length || 0) / pageSize))
const songPageList = computed(() => {
  const start = songPage.value * pageSize
  return (hotSongs.value || []).slice(start, start + pageSize)
})

function goSongPage(idx) {
  if (songAnimating.value) return
  const direction = idx > songPage.value ? 'next' : 'prev'
  triggerSlideAnimation('.song-list', direction, () => {
    songPage.value = idx
  })
}

function prevSongPage() {
  if (songAnimating.value) return
  triggerSlideAnimation('.song-list', 'prev', () => {
    songPage.value = (songPage.value - 1 + songTotalPages.value) % songTotalPages.value
  })
}

function nextSongPage() {
  if (songAnimating.value) return
  triggerSlideAnimation('.song-list', 'next', () => {
    songPage.value = (songPage.value + 1) % songTotalPages.value
  })
}

function triggerSlideAnimation(listSelector, direction, callback) {
  const animatingRef = getAnimatingRef(listSelector)
  if (animatingRef.value) return
  
  animatingRef.value = true
  const listElement = document.querySelector(listSelector)
  
  if (listElement) {
    listElement.style.opacity = '0.3'
    listElement.style.transform = direction === 'next' ? 'translateX(-20px)' : 'translateX(20px)'
    listElement.style.transition = 'all 0.2s ease-out'

    setTimeout(() => {
      callback()
      
      listElement.style.transform = direction === 'next' ? 'translateX(20px)' : 'translateX(-20px)'
      listElement.style.opacity = '0.3'
      
      setTimeout(() => {
        listElement.style.opacity = '1'
        listElement.style.transform = 'translateX(0)'
        listElement.style.transition = 'all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94)'
        
        setTimeout(() => {
          listElement.style.transition = ''
          animatingRef.value = false
        }, 300)
      }, 50)
    }, 200)
  } else {
    callback()
    animatingRef.value = false
  }
}

function getAnimatingRef(listSelector) {
  switch (listSelector) {
    case '.playlist-list': 
    case '.playlist-slider-container .playlist-list': 
      return playlistAnimating
    case '.artist-list': 
    case '.artist-slider-container .artist-list': 
      return artistAnimating
    case '.album-list': 
    case '.album-slider-container .album-list': 
      return albumAnimating
    case '.song-list': 
    case '.song-slider-container .song-list': 
      return songAnimating
    default: return ref(false)
  }
}

// 检查文本元素是否完整显示
function isTextFullyVisible(element, debug = false) {
  if (!element) return true
  
  const style = window.getComputedStyle(element)
  const text = element.textContent || element.innerText
  
  if (debug) {
    // CONSOLE LOG REMOVED: console.log(`检查文本: "${text.substring(0, 20)}..."`)
  }
  
  const lineClamp = style.webkitLineClamp || style.lineClamp
  if (lineClamp && lineClamp !== 'none' && parseInt(lineClamp) > 0) {
    const lineHeight = parseFloat(style.lineHeight) || parseFloat(style.fontSize) * 1.2
    const maxHeight = lineHeight * parseInt(lineClamp)
    const actualHeight = element.scrollHeight
    
    if (debug) {
      // CONSOLE LOG REMOVED: console.log(`  line-clamp: ${lineClamp}, 最大高度: ${maxHeight}, 实际高度: ${actualHeight}`)
    }
    
    if (actualHeight > maxHeight + 3) {
      if (debug) // CONSOLE LOG REMOVED: console.log(`  文本被 line-clamp 截断`)
      return false
    }
  }
  
  if (element.scrollWidth > element.clientWidth + 2) {
    if (debug) // CONSOLE LOG REMOVED: console.log(`  文本水平溢出: ${element.scrollWidth} > ${element.clientWidth}`)
    return false
  }
  
  if (element.scrollHeight > element.clientHeight + 2) {
    if (debug) // CONSOLE LOG REMOVED: console.log(`  文本垂直溢出: ${element.scrollHeight} > ${element.clientHeight}`)
    return false
  }
  
  
  if (text.length > 15 && element.clientWidth < 80) {
    if (debug) // CONSOLE LOG REMOVED: console.log(`  文本可能被强制截断：文本长度${text.length}，显示宽度${element.clientWidth}`)
    return false
  }
  
  if (style.textOverflow === 'ellipsis' && element.scrollWidth > element.clientWidth + 1) {
    if (debug) // CONSOLE LOG REMOVED: console.log(`  检测到省略号截断`)
    return false
  }
  
  if (debug) // CONSOLE LOG REMOVED: console.log(`  文本完整显示`)
  return true
}

function handleScroll() {
  const sections = [
    { 
      selector: '.playlist-section', 
      pageRef: playlistPage, 
      totalPagesRef: playlistTotalPages,
      visibilityRef: playlistCardsFullyVisible
    },
    { 
      selector: '.artist-section', 
      pageRef: artistPage, 
      totalPagesRef: artistTotalPages,
      visibilityRef: artistCardsFullyVisible
    },
    { 
      selector: '.album-section', 
      pageRef: albumPage, 
      totalPagesRef: albumTotalPages,
      visibilityRef: albumCardsFullyVisible
    },
    { 
      selector: '.song-section', 
      pageRef: songPage, 
      totalPagesRef: songTotalPages,
      visibilityRef: songCardsFullyVisible
    }
  ]
  
  sections.forEach(({ selector, pageRef, totalPagesRef, visibilityRef }) => {
    const section = document.querySelector(selector)
    if (!section) return
    
    // 获取该区域的卡片列表容器
    const cardsList = section.querySelector('.playlist-list, .artist-list, .album-list, .song-list') || 
                     section.querySelector('.playlist-slider-container .playlist-list, .artist-slider-container .artist-list, .album-slider-container .album-list, .song-slider-container .song-list')
    if (!cardsList) {
      visibilityRef.value = false
      return
    }
    
    // 获取所有卡片
    const cards = cardsList.querySelectorAll('.chaoshi-card')
    if (cards.length === 0) {
      visibilityRef.value = false
      return
    }
    
    // 检查所有卡片是否100%可见（包括图片和文本）
    let allCardsFullyVisible = true
    
    cards.forEach((card, index) => {
      const rect = card.getBoundingClientRect()
      
      // 获取卡片内的关键元素
      const imageContainer = card.querySelector('.chaoshi-image-container')
      const infoContainer = card.querySelector('.chaoshi-info')
      const title = card.querySelector('.chaoshi-title')
      const subtitle = card.querySelector('.chaoshi-subtitle')
      
      // 检查整个卡片是否完全在视口内（容忍1px误差）
      const tolerance = 1
      const cardFullyVisible = rect.top >= -tolerance && 
                              rect.left >= -tolerance && 
                              rect.bottom <= window.innerHeight + tolerance && 
                              rect.right <= window.innerWidth + tolerance
      
      let contentFullyVisible = cardFullyVisible
      
      if (cardFullyVisible) {
        const elementsToCheck = []
        
        if (imageContainer) elementsToCheck.push(imageContainer)
        if (infoContainer) elementsToCheck.push(infoContainer)
        
        if (title) elementsToCheck.push(title)
        
        if (subtitle && subtitle.offsetHeight > 0 && window.getComputedStyle(subtitle).display !== 'none') {
          elementsToCheck.push(subtitle)
        }
        
        elementsToCheck.forEach(element => {
          if (element && contentFullyVisible) {
            const elementRect = element.getBoundingClientRect()
            const elementVisible = elementRect.top >= -tolerance && 
                                 elementRect.left >= -tolerance && 
                                 elementRect.bottom <= window.innerHeight + tolerance && 
                                 elementRect.right <= window.innerWidth + tolerance
            
            if (elementVisible && (element.classList.contains('chaoshi-title') || element.classList.contains('chaoshi-subtitle'))) {
              const isTextComplete = isTextFullyVisible(element, false)
              if (!isTextComplete) {
                contentFullyVisible = false
              }
            }
            
            if (!elementVisible) {
              contentFullyVisible = false
            }
          }
        })
      }
      
      if (!contentFullyVisible) {
        allCardsFullyVisible = false
      }
    })
    
    visibilityRef.value = allCardsFullyVisible
  })
}

</script>

<style scoped>
.home-page {
  width: 1280px;
  margin: 0 auto;
  background-color: var(--background);
  padding-top: 118px;
}

.content-card {
  background: var(--background-card);
  backdrop-filter: blur(20px);
  border: 1px solid transparent;
  border-radius: var(--border-radius-xl);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  padding: 24px;
  margin: 0 12px 32px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: calc(20% - 24px);
  background-clip: padding-box;
}

.content-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  z-index: -1;
  border-radius: var(--border-radius-xl);
  background: linear-gradient(45deg, var(--primary-light), var(--secondary), var(--accent), var(--primary-light));
  background-size: 400%;
  opacity: 0;
  animation: gradientMove 4s ease infinite;
  transition: opacity 0.3s;
  filter: blur(2px);
}

[data-theme="black"] .content-card::before {
  background: linear-gradient(45deg, #3b82f6, #8b5cf6, #ec4899, #3b82f6);
  filter: blur(3px);
}

[data-theme="black"] .content-card:hover::before {
  opacity: 1;
  filter: blur(4px);
}

.content-card:hover::before {
  opacity: 1;
  filter: blur(3px);
}

@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}
.card-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: var(--border-radius-lg);
  margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;
}

[data-theme="black"] .card-img {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
}

.content-card:hover .card-img {
  transform: scale(1.05);
}

.card-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  opacity: 0.9;
}

.card-subtitle::before {
  content: '\f04b';
  font-family: 'iconfont';
  margin-right: 6px;
  font-size: 12px;
}

.toplist-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  color: #999;
  font-size: 14px;
}

.loading-spinner.small {
  width: 20px;
  height: 20px;
  margin-right: 8px;
  border-width: 2px;
}

.toplist-songs:empty {
  padding: 20px 0;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.toplist-songs:empty::before {
  content: '暂无歌曲数据';
}

.toplist-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.loading-spinner.small {
  width: 16px;
  height: 16px;
  border-width: 2px;
}

/* 排行榜空状态样式 */
.toplist-empty {
  padding: 40px 0;
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}
</style>

<style>
.banner-section {
  width: 100%; 
  margin: 0 auto;
  padding: 0 var(--spacing-md);
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 30%, var(--background-light) 70%, rgba(255,255,255,0.9) 100%) fixed;
  background-size: cover;
  border-radius: 0 0 16px 16px;
}

.content-container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 40px 20px; 
}

.content-container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

.banner-section {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 20px;
}

.banner-carousel {
  width: 100%;
  max-width: 1280px;
  height: 520px;
  margin: 0 auto;
  position: relative;
  border-radius: var(--border-radius-2xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
  margin-bottom: var(--space-2xl);
  transition: box-shadow var(--transition-normal);
}
.banner-carousel:hover {
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 10s ease-in-out;
}

.banner-carousel:hover .banner-img {
  transform: scale(1.1);
}

.banner-dots {
  position: absolute;
  bottom: 24px; 
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  padding: 10px 16px;
  background: rgba(10, 25, 47, 0.5); 
  backdrop-filter: blur(8px);
  border-radius: 20px; 
}

.banner-dots .dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  transition: all 0.3s;
}

.banner-dots .dot.active {
  background: var(--primary);
  width: 28px; 
  border-radius: 5px;
}

.playlist-section, .artist-section, .album-section, .song-section {
  opacity: 1 !important;
  transform: translateY(0) !important;
}

.section-title-wrap {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
  margin-top: 32px;
  border: none !important;
  box-shadow: none !important;
  background: none !important;
}
.section-title-wrap.center-title {
  justify-content: center;
}
.section-title {
  text-align: center;
  border: none !important;
  box-shadow: none !important;
  background: none !important;
  font-size: clamp(1.8rem, 5vw, 3rem);
  background: linear-gradient(90deg, #818cf8, #c084fc, #ec4899);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-shadow: 0 0 20px rgba(129, 140, 248, 0.4), 0 2px 15px rgba(129, 140, 248, 0.3);
  margin-bottom: 40px;
  letter-spacing: 0.8px;
  font-weight: 800;
  position: relative;
  display: inline-block;
}

.section-title:hover {
  animation: titleFloat 3s ease-in-out infinite;
}

.playlist-name, .album-name, .artist-name, .song-name {
  font-size: 17px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 6px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.playlist-count, .album-artist, .song-artist {
  font-size: 14px;
  color: #cbd5e1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}
</style>

<style>
body {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 30%, var(--background-light) 70%, rgba(255,255,255,0.9) 100%) fixed !important;
  background-size: cover;
  background-repeat: no-repeat;
  min-height: 100vh;
  color: #e2e8f0;
}

.banner-section {
  width: 100%; 
  margin: 0 auto;
  padding: 0 var(--spacing-md);
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 30%, var(--background-light) 70%, rgba(255,255,255,0.9) 100%) fixed;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(59, 130, 246, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(168, 85, 247, 0.05) 0%, transparent 50%);
  background-size: cover;
  border-radius: 0 0 16px 16px;
}

.content-container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 40px 20px; 
}

.toplist-section {
  width: 100%;
  margin: 80px auto 0; 
  padding: 0 20px 64px; 
  position: static;
  scroll-margin-top: 120px;
}

.toplist-cards {
  display: flex;
  gap: 24px;
  justify-content: flex-start;
  flex-wrap: nowrap;
  padding: 20px 0; 
  overflow-x: auto;
  scrollbar-width: none;
}

.content-container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 20px 20px; 
}

.toplist-cards {
  display: flex;
  gap: 20px;
  justify-content: flex-start;
  flex-wrap: nowrap;
  padding: 20px 0;
  overflow-x: auto;
  scrollbar-width: none;
}

.toplist-cards::-webkit-scrollbar {
  display: none; 
}

.toplist-card {
  flex: 0 0 280px; 
  max-width: 280px;
  margin: 0;
}

@media (max-width: 992px) {
  .toplist-card {
    flex: 0 0 calc(50% - 12px);
  }
}

@media (max-width: 576px) {
  .toplist-card {
    flex: 0 0 100%;
    max-width: 100%;
  }
}

.banner-section {
    max-width: 1280px;
    margin: 0 auto 0; 
    padding: 0 20px;
}

.banner-carousel {
  width: 100%;
  height: 420px; 
  position: relative;
  border-radius: 16px; 
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15); 
}

.banner-placeholder {
  width: 100%;
  height: 420px;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-content {
  text-align: center;
  color: #000000;
}

.placeholder-content h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.8);
  color: #000000;
}

.placeholder-content p {
  font-size: 20px;
  font-weight: 700;
  color: #000000;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

[data-theme="black"] .placeholder-content {
  color: #ffffff;
}

[data-theme="black"] .placeholder-content h2 {
  color: #ffffff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

[data-theme="black"] .placeholder-content p {
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover; 
  transition: transform 8s ease-in-out; 
}

.banner-dots {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  padding: 6px 12px;
  background: transparent;
  backdrop-filter: none;
  border-radius: 0;
}

.banner-dots .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
}

.banner-dots .dot:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: scale(1.3);
}

.banner-dots .dot.active {
  background: rgba(255, 255, 255, 0.9);
  width: 6px;
  border-radius: 50%;
  box-shadow: none;
}
.playlist-section, .artist-section, .album-section, .song-section {
  opacity: 1 !important;
  transform: translateY(0) !important;
}

.section-title-wrap {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  margin-top: 0;
  border: none !important;
  box-shadow: none !important;
  background: none !important;
}
.section-title-wrap.center-title {
  justify-content: center;
}
.section-title {
  text-align: center;
  border: none !important;
  box-shadow: none !important;
  background: none !important;
  font-size: clamp(1.5rem, 4vw, 2.5rem);
  color: var(--text-primary);
  margin-bottom: 0;
  letter-spacing: 0.5px;
  font-weight: 700;
}

.section-line {
  display: none;
}

.playlist-section, .artist-section, .album-section, .song-section {
  position: relative;
  padding: 0;
  margin-bottom: 70px;
}

.playlist-slider-container {
  width: 100%;
  overflow: visible;
  padding: 36px 0;
  margin: 0;
  min-height: 300px; 
}

.artist-slider-container,
.album-slider-container,
.song-slider-container {
  width: 100%;
  overflow: visible;
  padding: 36px 0;
  margin: 0;
  min-height: 300px; 
}

.slider-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: clamp(100px, 7vw, 150px);
  height: clamp(100px, 7vw, 150px);
  border-radius: 50%;
  background: transparent;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1000;
  transition: all 0.3s ease;
  box-shadow: none;
  opacity: 0.8;
  visibility: visible;
  overflow: hidden;
}

.slider-btn:hover {
  background: transparent;
  transform: translateY(-50%) scale(1.1);
  box-shadow: none;
  opacity: 1;
  transition: all 0.2s ease;
}

.slider-btn-left {
  left: clamp(-150px, -10vw, -80px);
}

.slider-btn-right {
  right: clamp(-150px, -10vw, -80px);
}

@media (max-width: 768px) {
  .slider-btn {
    width: 85px;
    height: 85px;
    opacity: 0.8;
    visibility: visible;
  }
  
  .left-icon, .right-icon {
    width: 40px;
    height: 40px;
  }
}

@media (max-width: 480px) {
  .slider-btn {
    width: 75px;
    height: 75px;
    opacity: 0.7;
    visibility: visible;
  }
  
  .left-icon, .right-icon {
    width: 35px;
    height: 35px;
  }
}

@media (min-width: 1920px) {
  .slider-btn {
    width: 160px;
    height: 160px;
    opacity: 0.9;
    visibility: visible;
  }
  
  .left-icon, .right-icon {
    width: 60px;
    height: 60px;
  }
}

.left-icon, .right-icon {
  width: 50px;
  height: 50px;
  display: inline-block;
}

.left-icon {
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="%23666" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.right-icon {
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="%23666" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18l6-6-6-6"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

[data-theme="black"] .left-icon {
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>');
}

[data-theme="black"] .right-icon {
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18l6-6-6-6"/></svg>');
}

.playlist-list,
.artist-list,
.album-list,
.song-list {
  display: flex;
  gap: 24px;
  flex-wrap: nowrap;
  justify-content: flex-start;
  overflow-x: hidden; /* 去除滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
  padding: 0;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transform: translateX(0);
  will-change: transform;
}

.playlist-list.slide-out,
.artist-list.slide-out,
.album-list.slide-out,
.song-list.slide-out {
  opacity: 0.2;
  transition: all 0.25s cubic-bezier(0.4, 0, 1, 1);
}

.playlist-list.slide-out.slide-left,
.artist-list.slide-out.slide-left,
.album-list.slide-out.slide-left,
.song-list.slide-out.slide-left {
  transform: translateX(-40px) scale(0.9) rotateY(-5deg);
}

.playlist-list.slide-out.slide-right,
.artist-list.slide-out.slide-right,
.album-list.slide-out.slide-right,
.song-list.slide-out.slide-right {
  transform: translateX(40px) scale(0.9) rotateY(5deg);
}

.mcp-transition {
  position: relative;
  overflow: hidden;
}

.mcp-transition::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(129, 140, 248, 0.1), transparent);
  animation: mcpSweep 0.6s ease-out;
  z-index: 1;
}

@keyframes mcpSweep {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

.playlist-list.slide-in,
.artist-list.slide-in,
.album-list.slide-in,
.song-list.slide-in {
  opacity: 0;
  transition: all 0.4s cubic-bezier(0, 0, 0.2, 1);
}

.playlist-list.slide-in.slide-from-right,
.artist-list.slide-in.slide-from-right,
.album-list.slide-in.slide-from-right,
.song-list.slide-in.slide-from-right {
  transform: translateX(40px) scale(0.9) rotateY(5deg);
  animation: slideInFromRightMCP 0.4s cubic-bezier(0, 0, 0.2, 1) forwards;
}

.playlist-list.slide-in.slide-from-left,
.artist-list.slide-in.slide-from-left,
.album-list.slide-in.slide-from-left,
.song-list.slide-in.slide-from-left {
  transform: translateX(-40px) scale(0.9) rotateY(-5deg);
  animation: slideInFromLeftMCP 0.4s cubic-bezier(0, 0, 0.2, 1) forwards;
}

.mcp-enter {
  position: relative;
}

.mcp-enter::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(129, 140, 248, 0.1) 0%, transparent 70%);
  opacity: 0;
  animation: mcpGlow 0.8s ease-out;
  z-index: 1;
  pointer-events: none;
}

@keyframes mcpGlow {
  0% {
    opacity: 0;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
  100% {
    opacity: 0;
    transform: scale(1);
  }
}

@keyframes slideInFromRightMCP {
  0% {
    opacity: 0;
    transform: translateX(40px) scale(0.9) rotateY(5deg);
    filter: blur(3px);
  }
  30% {
    opacity: 0.3;
    transform: translateX(15px) scale(0.95) rotateY(2deg);
    filter: blur(1px);
  }
  70% {
    opacity: 0.8;
    transform: translateX(-3px) scale(1.01) rotateY(-1deg);
    filter: blur(0px);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1) rotateY(0deg);
    filter: blur(0px);
  }
}

@keyframes slideInFromLeftMCP {
  0% {
    opacity: 0;
    transform: translateX(-40px) scale(0.9) rotateY(-5deg);
    filter: blur(3px);
  }
  30% {
    opacity: 0.3;
    transform: translateX(-15px) scale(0.95) rotateY(-2deg);
    filter: blur(1px);
  }
  70% {
    opacity: 0.8;
    transform: translateX(3px) scale(1.01) rotateY(1deg);
    filter: blur(0px);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1) rotateY(0deg);
    filter: blur(0px);
  }
}

.slider-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: transparent;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  opacity: 0;
}

.slider-btn:active::before {
  width: 100px;
  height: 100px;
  opacity: 0;
  transition: all 0.2s ease-out;
}


.playlist-list::-webkit-scrollbar,
.artist-list::-webkit-scrollbar,
.album-list::-webkit-scrollbar,
.song-list::-webkit-scrollbar {
  display: none;
}

.playlist-dots {
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  padding: 6px 12px;
  background: transparent;
  backdrop-filter: none;
  border-radius: 0;
  margin-top: 0;
  opacity: 1;
  visibility: visible;
  transition: all 0.3s ease;
}

.playlist-dots .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  display: inline-block;
  cursor: pointer;
  border: none;
}

.playlist-dots .dot:hover {
  background: rgba(0, 0, 0, 0.5);
  transform: scale(1.3);
}

.playlist-dots .dot.active {
  background: rgba(0, 0, 0, 0.7);
  width: 6px;
  border-radius: 50%;
  box-shadow: none;
}

.chaoshi-card {
  cursor: pointer;
  transition: all 0.2s ease;
  width: calc(20% - 19.2px);
  min-width: 200px;
  flex-shrink: 0;
}

.chaoshi-card:hover .chaoshi-image-container {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.25);
}

.chaoshi-rank {
  position: absolute;
  top: 8px;
  left: 8px;
  width: 20px;
  height: 20px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  z-index: 2;
}

.chaoshi-image-container {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  transition: all 0.2s ease;
}

.chaoshi-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.chaoshi-artist-avatar {
  border-radius: 50%;
}

.artist-list .chaoshi-image-container {
  border-radius: 50%;
  width: 100%;
  height: 0;
  padding-bottom: 100%; 
  position: relative;
  overflow: hidden;
  margin-bottom: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.artist-list .chaoshi-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.artist-list .chaoshi-info {
  padding: 8px 0 0 0;
  background: none;
  text-align: center;
}

.artist-list .chaoshi-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.2;
  margin-bottom: 0;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.artist-list .chaoshi-subtitle {
  display: none;
}

.chaoshi-card:hover .chaoshi-image {
  transform: scale(1.05);
}

.chaoshi-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chaoshi-card:hover .chaoshi-overlay {
  opacity: 1;
}

.chaoshi-play-btn {
  width: 68px;
  height: 68px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.6);
}

.chaoshi-play-btn:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
  box-shadow: 0 0 25px rgba(255, 255, 255, 0.8);
}

.chaoshi-play-icon-svg {
  width: 24px;
  height: 24px;
  color: #333;
  transition: all 0.3s ease;
}

.chaoshi-play-btn:hover .chaoshi-play-icon-svg {
  color: #333;
  transform: scale(1.05);
}

.song-section .chaoshi-card .chaoshi-info {
  padding: 4px 0 16px 4px !important;
  background: none;
  text-align: left !important;
  width: 100% !important;
  box-sizing: border-box;
  display: block !important;
  float: left !important;
  clear: none !important;
}

.song-section .chaoshi-card .chaoshi-info .chaoshi-title {
  font-size: 16px;
  font-weight: 400;
  color: #333;
  line-height: 1.1;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 36px;
  text-align: left !important;
  padding-left: 0 !important;
  margin-left: 0 !important;
  text-indent: 0 !important;
  float: none;
}

.song-section .chaoshi-card .chaoshi-info .chaoshi-subtitle {
  font-size: 14px;
  color: #999;
  line-height: 1.1;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-top: 0;
  margin-bottom: 8px;
  padding-top: 0;
  text-align: left !important;
  padding-left: 0 !important;
  margin-left: 0 !important;
  text-indent: 0 !important;
  float: none;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  text-align: left;
}

.section-title-wrap {
  justify-content: flex-start;
  margin-bottom: 0;
  margin-top: 32px;
}

[data-theme="black"] .chaoshi-title {
  color: #fff;
}

[data-theme="black"] .chaoshi-subtitle {
  color: #999;
}

[data-theme="black"] .section-title {
  color: #fff;
}

[data-theme="black"] .chaoshi-image-container {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}

@media (max-width: 1200px) {
  .playlist-list,
  .artist-list,
  .album-list,
  .song-list {
    gap: 20px;
  }
  
  .chaoshi-card {
    width: calc(25% - 15px);
    min-width: 180px;
  }
  
  .playlist-section, .artist-section, .album-section, .song-section {
    margin-bottom: 60px;
  }
}

@media (max-width: 768px) {
  .playlist-list,
  .artist-list,
  .album-list,
  .song-list {
    gap: 16px;
  }
  
  .chaoshi-card {
    width: calc(50% - 8px);
    min-width: 160px;
  }
  
  .chaoshi-image-container {
    height: 160px;
  }
  
  .chaoshi-info {
    padding: 4px 0 12px 0;
  }
  
  .playlist-section, .artist-section, .album-section, .song-section {
    margin-bottom: 50px;
  }
  
  .playlist-slider-container {
    padding: 28px 0;
  }
  
  .artist-slider-container,
  .album-slider-container,
  .song-slider-container {
    padding: 28px 0;
  }
}

@media (max-width: 480px) {
  .playlist-list,
  .artist-list,
  .album-list,
  .song-list {
    gap: 12px;
  }
  
  .chaoshi-card {
    width: calc(50% - 6px);
    min-width: 140px;
  }
  
  .chaoshi-image-container {
    height: 140px;
  }
  
  .chaoshi-info {
    padding: 4px 0 10px 0;
  }
  
  .playlist-section, .artist-section, .album-section, .song-section {
    margin-bottom: 40px;
  }
  
  .playlist-slider-container {
    padding: 24px 0;
  }
  
  .artist-slider-container,
  .album-slider-container,
  .song-slider-container {
    padding: 24px 0;
  }
}


.song-info {
  text-align: center;
}

.playlist-section, .artist-section, .album-section, .song-section {
  opacity: 1;
  transform: none;
}

::-webkit-scrollbar-thumb {
  background: rgba(129, 140, 248, 0.5);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(129, 140, 248, 0.8);
}

.song-highlights {
  padding: 40px 0;
  position: relative;
  overflow: hidden;
  margin-bottom: 70px;
}

.highlights-container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  min-height: 480px;
  padding: 20px;
}

.highlight-item {
  position: absolute;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.2, 0.8, 0.2, 1);
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.1);
}

.highlight-item.active {
  width: 180px;
  height: 180px;
  z-index: 10;
  box-shadow: 0 20px 40px rgba(129, 140, 248, 0.4);
  border-color: rgba(129, 140, 248, 0.5);
}

.highlight-item:nth-child(1) { top: 50%; left: 50%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(2) { top: 20%; left: 30%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(3) { top: 20%; left: 70%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(4) { top: 80%; left: 30%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(5) { top: 80%; left: 70%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(6) { top: 35%; left: 15%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(7) { top: 35%; left: 85%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(8) { top: 65%; left: 15%; transform: translate(-50%, -50%); }
.highlight-item:nth-child(9) { top: 65%; left: 85%; transform: translate(-50%, -50%); }

.highlight-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.5s ease;
  filter: brightness(0.9);
}

.highlight-item:hover .highlight-img {
  filter: brightness(1.1);
  transform: scale(1.05);
}

.highlight-name {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px 0;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 14px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.highlight-item:hover .highlight-name {
  opacity: 1;
}

.toplist-section {
  position: relative;
  padding: 0;
  margin-bottom: 70px;
  transition: all 0.2s ease;
  width: 100%;
  overflow-x: auto;
}

.section-more {
  color: #999;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s ease;
  margin-left: auto;
}

.section-more:hover {
  color: #409eff;
}

.section-title-wrap {
  position: relative;
}

.toplist-title-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  text-align: center;
}

.toplist-title-wrap .section-title {
  margin: 0;
  text-align: center;
}

.toplist-title-wrap .section-more {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.toplist-title-wrap .section-line {
  display: none;
}

.toplist-container {
  display: flex;
  gap: 16px;
  margin-top: 40px;
  flex-wrap: nowrap;
  overflow-x: visible;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 0 0 20px 0;
  width: 100%;
  justify-content: space-between;
}

.toplist-container::-webkit-scrollbar {
  display: none;
}

  .toplist-card {
    flex: 1 0 220px;
    max-width: calc(20% - 12.8px);
    min-width: 220px;
    width: 220px;
    height: 480px;
    border-radius: 12px;
    padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  color: white;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.toplist-1 {
  background: linear-gradient(135deg, #e91e63 0%, #ad1457 100%);
}

.toplist-2 {
  background: linear-gradient(135deg, #e91e63 0%, #c2185b 100%);
}

.toplist-3 {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
}

.toplist-4 {
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
}

.toplist-5 {
  background: linear-gradient(135deg, #00bcd4 0%, #0097a7 100%);
}

.toplist-header {
  margin-bottom: 16px;
  text-align: center;
}

.toplist-title-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.toplist-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: white;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.5px;
}

.toplist-play-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 60px;
}

.toplist-line {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 2px;
  background: rgba(255, 255, 255, 0.6);
  transition: opacity 0.3s ease;
}

.toplist-play-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0.8);
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.6);
  opacity: 0;
}

.toplist-card:hover .toplist-line {
  opacity: 0;
}

.toplist-card:hover .toplist-play-btn {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
}

.toplist-play-btn:hover {
  background: rgba(255, 255, 255, 1);
  transform: translate(-50%, -50%) scale(1.1);
  box-shadow: 0 0 25px rgba(255, 255, 255, 0.8);
}

.toplist-play-icon-svg {
  width: 20px;
  height: 20px;
  color: #333;
  transition: all 0.3s ease;
}

.toplist-play-btn:hover .toplist-play-icon-svg {
  color: #333;
  transform: scale(1.05);
}

.play-btn-large {
  width: 68px;
  height: 68px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.6);
}

.play-btn-large:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 25px rgba(255, 255, 255, 0.8);
}

.play-icon-svg {
  width: 24px;
  height: 24px;
  color: #333;
  transition: all 0.3s ease;
}

.play-btn-large:hover .play-icon-svg {
  color: #333;
  transform: scale(1.05);
}


.toplist-songs {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.toplist-songs::-webkit-scrollbar {
  display: none !important;
  width: 0 !important;
  height: 0 !important;
}

.toplist-songs::-webkit-scrollbar-thumb {
  display: none !important;
  background: transparent !important;
}

.toplist-songs::-webkit-scrollbar-track {
  display: none !important;
  background: transparent !important;
}

.toplist-song-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 8px 0;
  transition: all 0.2s ease;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.toplist-song-item:last-child {
  border-bottom: none;
}

.toplist-song-item:hover {
  background: rgba(255, 255, 255, 0.05);
  transform: translateX(2px);
}

.song-rank {
  font-size: 16px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.95);
  min-width: 20px;
  text-align: left;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  margin-top: 2px;
}

.song-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
  text-align: left;
}

.song-title {
  font-size: 14px;
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  line-height: 1.3;
}

.song-artist {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 400;
  line-height: 1.2;
}

[data-theme="black"] .toplist-card {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
}

@media (min-width: 1200px) {
  .toplist-container {
    justify-content: space-between;
    gap: 16px;
    overflow-x: visible;
  }
  
  .toplist-card {
    flex: 1 0 220px;
    max-width: calc(20% - 12.8px);
    width: calc(20% - 12.8px);
  }
}

@media (max-width: 1199px) and (min-width: 1000px) {
  .toplist-container {
    gap: 12px;
    justify-content: flex-start;
    overflow-x: auto;
  }
  
  .toplist-card {
    flex: 0 0 200px;
    width: 200px;
    height: 440px;
    padding: 18px;
  }
  
  .toplist-name {
    font-size: 16px;
  }
  
  .toplist-play-btn {
    width: 50px;
    height: 50px;
  }
  
  .toplist-play-icon-svg {
    width: 16px;
    height: 16px;
  }
  
  .song-title {
    font-size: 12px;
  }
  
  .song-artist {
    font-size: 10px;
  }
}

@media (max-width: 768px) {
  .toplist-container {
    gap: 12px;
    padding: 0;
  }
  
  .toplist-card {
    flex: 0 0 220px;
    width: 220px;
    height: 480px;
    padding: 16px;
  }
  
  .toplist-name {
    font-size: 18px;
  }
  
  .toplist-play-btn {
    width: 50px;
    height: 50px;
  }
  
  .toplist-play-icon-svg {
    width: 16px;
    height: 16px;
  }
  
  .song-title {
    font-size: 13px;
  }
  
  .song-artist {
    font-size: 11px;
  }
  
  .song-rank {
    font-size: 14px;
    min-width: 20px;
  }
}

@media (max-width: 480px) {
  .toplist-card {
    flex: 0 0 200px;
    width: 200px;
    height: 420px;
    padding: 12px;
  }
  
  .toplist-name {
    font-size: 16px;
  }
  
  .toplist-play-btn {
    width: 45px;
    height: 45px;
  }
  
  .toplist-play-icon-svg {
    width: 14px;
    height: 14px;
  }
  
  .song-title {
    font-size: 12px;
  }
  
  .song-artist {
    font-size: 10px;
  }
  
  .song-rank {
    font-size: 12px;
    min-width: 18px;
  }
  
  .toplist-songs {
    gap: 2px;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #000000;
  font-size: 20px;
  font-weight: 700;
}

.loading-container .loading-text {
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

[data-theme="black"] .loading-container {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}

[data-theme="black"] .loading-container .loading-text {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}

.album-section .chaoshi-image {
  transition: transform 0.3s ease;
}

.album-section .chaoshi-card:hover .chaoshi-image {
  transform: none;
}

.album-section .chaoshi-card.playing .chaoshi-image {
  animation: albumRotate 3s linear infinite;
}

@keyframes albumRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.clickable-item {
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-block;
}

.chaoshi-title.clickable-item,
.song-title.clickable-item {
  position: relative;
}

.chaoshi-title.clickable-item:hover,
.song-title.clickable-item:hover {
  color: #007bff;
  transform: translateX(2px);
}

.chaoshi-title.clickable-item:hover::after,
.song-title.clickable-item:hover::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #007bff;
}

.chaoshi-subtitle.clickable-item:hover,
.song-artist.clickable-item:hover {
  color: #6c5ce7;
  transform: translateX(2px);
  background: linear-gradient(90deg, #6c5ce7, #a29bfe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.clickable-item:active {
  transform: scale(0.98);
}

.global-loading-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #f3f4f6;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary), var(--primary-light));
  border-radius: 3px;
  transition: width 0.3s ease;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: progressShimmer 1.5s infinite;
}

.progress-text {
  text-align: center;
}

.loading-main-text {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

@keyframes progressShimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(200%); }
}

[data-theme="black"] .global-loading-progress {
  background: rgba(0, 0, 0, 0.95);
  border-bottom-color: #374151;
}

[data-theme="black"] .progress-bar {
  background: #374151;
}

[data-theme="black"] .loading-main-text {
  color: #ffffff;
}

.home-page {
  padding-top: 0;
  transition: padding-top 0.3s ease;
}

.home-page:has(.global-loading-progress) {
  padding-top: 80px;
}
</style>
