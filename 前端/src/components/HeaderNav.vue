<template>
  <header class="header-nav">
    <div class="header-content">
      <div class="logo-area">
        <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
      </div>
      <nav class="main-menu">
        <a href="#" class="menu-item" :class="{ active: isHome }" @click.prevent="goHome">音乐馆</a>
        <a href="#" class="menu-item" :class="{ active: isMyMusic }" @click.prevent="goMyMusic">我的音乐</a>
        <a href="#" class="menu-item" :class="{ active: isOpenPlatform }" @click.prevent="goOpenPlatform">开放平台</a>
        <a href="#" class="menu-item" :class="{ active: is0717 }" @click.prevent="go0717">0717</a>
        <div class="independent-planet-icon" @click="goToGame">
          <img src="@/assets/星球.svg" alt="独立星球图标" class="independent-planet-svg" />
        </div>
      </nav>
      <div class="search-user-area">
        <div class="search-area">
          <div class="planet-logo">
            <img src="@/assets/星球.svg" alt="星球" class="planet-svg" />
          </div>
          <div class="search-input-container">
            <input 
              type="text" 
              class="search-input" 
              placeholder="搜索音乐、MV、歌单、用户" 
              v-model="searchQuery"
              @keyup.enter="handleSearch"
              @input="handleSearchInput"
              @focus="handleSearchFocus"
            />
            <svg class="search-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
              <path d="M351.1808 59.2896A435.2 435.2 0 0 1 805.376 715.264 460.8 460.8 0 0 1 351.1808 59.3408z" fill="#20C997"></path>
              <path d="M754.3808 722.2272a358.4 358.4 0 1 0-267.8272 120.2176 51.2 51.2 0 0 1 0 102.4 460.8 460.8 0 1 1 365.1584-179.712l118.8864 121.2416c23.7568 24.2176 23.552 63.0272-0.4096 87.04l-0.4096 0.4096a61.184 61.184 0 0 1-86.9888-0.4608l-148.0192-150.9376a61.7984 61.7984 0 0 1 0.4096-86.9888l0.4096-0.4096c5.632-5.5808 11.9808-9.8304 18.7904-12.8z m-467.968-364.5952h409.6a51.2 51.2 0 1 1 0 102.4h-409.6a51.2 51.2 0 1 1 0-102.4z m0 204.8h256a51.2 51.2 0 0 1 0 102.4h-256a51.2 51.2 0 1 1 0-102.4z" fill="#2C6DD2"></path>
            </svg>
          </div>
          <div v-if="showSearchResults" class="search-results" @click.stop>
            <!-- 搜索中状态 -->
            <div v-if="isSearching" class="search-loading">
              <div class="loading-spinner"></div>
              <span>搜索中...</span>
            </div>
            
            <!-- 有搜索结果 -->
            <div v-else-if="searchResults.length > 0">
              <div class="search-section-title">搜索结果</div>
              <div v-for="result in searchResults" :key="result.id" class="search-result-item" @click="selectSearchResult(result)">
                <div class="result-icon">{{ getResultIcon(result.type) }}</div>
                <div class="result-info">
                  <div class="result-title">{{ result.name || result.title }}</div>
                  <div class="result-subtitle">{{ getResultSubtitle(result) }}</div>
                </div>
                <div class="result-type">{{ getResultTypeText(result.type) }}</div>
              </div>
            </div>
            
            <!-- 无搜索结果但有搜索关键词 -->
            <div v-else-if="searchQuery.trim() !== '' && !isSearching" class="no-results">
              <div class="no-results-icon">🔍</div>
              <div class="no-results-text">未找到相关结果</div>
              <div class="no-results-tip">请尝试其他关键词</div>
            </div>
            
            <!-- 空搜索时显示历史和热门 -->
            <div v-else>
              <!-- 搜索历史 -->
              <div v-if="searchHistory.length > 0" class="search-section">
                <div class="search-section-header">
                  <span class="search-section-title">搜索历史</span>
                  <button class="clear-history-btn" @click="handleClearHistory">清空</button>
                </div>
                <div class="history-list">
                  <div 
                    v-for="(item, index) in searchHistory.slice(0, 8)" 
                    :key="index" 
                    class="history-item"
                    @click="selectHistoryItem(item)"
                  >
                    <span class="history-icon">🕐</span>
                    <span class="history-text">{{ item }}</span>
                  </div>
                </div>
              </div>
              
              <!-- 热门搜索 -->
              <div v-if="hotSearches.length > 0" class="search-section">
                <div class="search-section-title">热门搜索</div>
                <div class="hot-search-list">
                  <div 
                    v-for="(item, index) in hotSearches.slice(0, 10)" 
                    :key="index" 
                    class="hot-search-item"
                    :class="{ 'hot-top': index < 3 }"
                    @click="selectHotSearch(item)"
                  >
                    <span class="hot-rank">{{ index + 1 }}</span>
                    <span class="hot-text">{{ item.keyword || item }}</span>
                    <span v-if="item.type" class="hot-type">{{ getHotSearchTypeText(item.type) }}</span>
                    <span v-if="item.hot" class="hot-badge">🔥</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="user-actions">
          <div class="user-avatar" v-if="isLogin" style="position:relative;">
            <img :src="avatarImg" alt="用户头像" style="cursor:pointer;" @click="toggleDropdown" @mouseenter="showTooltip = true" @mouseleave="showTooltip = false" />
            <div v-if="showDropdown" class="profile-dropdown" @click.stop>
              <div class="dropdown-arrow"></div>
              <div class="dropdown-header">
                <img :src="avatarImg" class="dropdown-avatar" />
                <div class="dropdown-info">
                  <div class="dropdown-nickname">{{ nickname }}</div>
                  
                </div>
              </div>
              <div class="dropdown-divider"></div>
              <div class="dropdown-item" @click="goProfile">
                <span>个人资料</span>
                <i class="el-icon-user"></i>
              </div>
              <div class="dropdown-item" @click="showColorPicker = true">
                <span>主题切换</span>
                <i class="el-icon-moon"></i>
              </div>
              
              <div class="dropdown-item" @click="showBackgroundSelector">
                <span>更换背景</span>
                <i class="el-icon-picture">🎨</i>
              </div>

              <div class="dropdown-divider"></div>
              <div class="dropdown-item logout-item" @click="logout">退出登录</div>
            </div>
          </div>
          <button class="login-nav-btn" v-else @click="goLogin">登录</button>
        </div>
      </div>
    </div>
  </header>
  <el-dialog v-model="dialogVisible" width="400px" :show-close="true" center>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="登录" name="login">
        <el-form :model="loginForm" label-width="60px" class="login-form">
          <el-form-item label="账号">
            <el-input v-model="loginForm.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width:100%" @click="handleLogin">登录</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="注册" name="register">
        <el-form :model="registerForm" label-width="60px" class="register-form">
          <el-form-item label="账号">
            <el-input v-model="registerForm.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="确认">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="success" style="width:100%" @click="handleRegister">注册</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
  
  <!-- 颜色选择器对话框 -->
  <el-dialog v-model="showColorPicker" title="选择主题颜色" width="400px" center>
    <div class="color-picker-container">
      <div class="preset-colors">
        <h4>预设主题</h4>
        <div class="color-grid">
          <div 
            v-for="(color, name) in presetColors" 
            :key="name"
            class="color-item"
            :class="{ active: currentTheme === name }"
            :style="{ backgroundColor: color.primary }"
            @click="selectPresetTheme(name)"
          >
            <span class="color-name">{{ color.name }}</span>
          </div>
        </div>
      </div>
      
      <div class="custom-color">
        <h4>自定义颜色</h4>
        <div class="custom-color-input">
          <input 
            type="color" 
            v-model="customColor" 
            @change="applyCustomColor"
            class="color-input"
          />
          <button @click="applyCustomColor" class="apply-btn">应用</button>
        </div>
      </div>
    </div>
  </el-dialog>

  <!-- 背景选择器对话框 -->
  <el-dialog v-model="showBackgroundModal" title="选择背景墙" width="800px" center>
    <div class="background-picker-container">
      <div class="preset-backgrounds">
        <h4>预设背景</h4>
        <div class="background-grid">
          <div 
            v-for="(bg, index) in presetBackgrounds" 
            :key="index"
            class="background-option"
            :class="{ active: currentBackground === bg.url }"
            @click="selectBackground(bg.url)"
          >
            <img :src="bg.url" :alt="bg.name">
            <div class="bg-overlay">
              <span class="bg-name">{{ bg.name }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="custom-upload">
        <h4>自定义背景</h4>
        <div class="upload-area">
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
          <p class="upload-tip">支持 JPG、PNG、GIF、WebP 格式，文件大小不超过10MB，建议尺寸 1920x1080</p>
        </div>
      </div>
      
      <div class="recent-backgrounds" v-if="recentBackgrounds.length > 0">
        <h4>最近使用</h4>
        <div class="background-grid">
          <div 
            v-for="(bg, index) in recentBackgrounds" 
            :key="index"
            class="background-option"
            :class="{ active: currentBackground === bg }"
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
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { validateImageFile, compressImage } from '@/utils/imageUtils.js'
import { ElMessage } from 'element-plus'
import { searchAll, searchSuggest, getHotSearch, getSearchHistory, saveSearchHistory, clearSearchHistory, fuzzySearch, smartSuggest, getHotArtists, getHotSongs } from '@/api/search.js'

const router = useRouter()
const route = useRoute()
const dialogVisible = ref(false)
const activeTab = ref('login')
const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', confirmPassword: '' })

const isLogin = ref(false)
const defaultAvatar = 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100'
const avatarImg = ref(localStorage.getItem('userAvatar') || defaultAvatar)
const showDropdown = ref(false)
const showTooltip = ref(false)
const nickname = ref(localStorage.getItem('userNickname') || '苏黎世的从前')
const currentTheme = ref(localStorage.getItem('theme') || 'pink')
const showColorPicker = ref(false)
const customColor = ref(localStorage.getItem('customColor') || '#ec4899')
const showBackgroundModal = ref(false)
const currentBackground = ref(localStorage.getItem('userBannerBg') || '')
const recentBackgrounds = ref(JSON.parse(localStorage.getItem('recentBackgrounds') || '[]'))
const backgroundInput = ref(null)

// 搜索相关
const searchQuery = ref('')
const showSearchResults = ref(false)
const searchResults = ref([])
const searchHistory = ref([])
const hotSearches = ref([])
const isSearching = ref(false)
const searchTimer = ref(null)

const presetColors = {
  pink: { name: '粉色', primary: '#ec4899' },
  blue: { name: '蓝色', primary: '#3b82f6' },
  green: { name: '绿色', primary: '#10b981' },
  purple: { name: '紫色', primary: '#8b5cf6' },
  orange: { name: '橙色', primary: '#f97316' },
  red: { name: '红色', primary: '#ef4444' },
  black: { name: '黑色', primary: '#000000' },
  white: { name: '白色', primary: '#ffffff' }
}

const presetBackgrounds = [
  { name: '星空', url: 'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=1920&h=1080&fit=crop&q=80' },
  { name: '海滩', url: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1920&h=1080&fit=crop&q=80' },
  { name: '城市', url: 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=1920&h=1080&fit=crop&q=80' },
  { name: '森林', url: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1920&h=1080&fit=crop&q=80' },
  { name: '雪山', url: 'https://images.unsplash.com/photo-1464822759844-d150baec0134?w=1920&h=1080&fit=crop&q=80' },
  { name: '日落', url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&q=80' }
]

function checkLogin() {
  isLogin.value = localStorage.getItem('isLogin') === '1'
  avatarImg.value = localStorage.getItem('userAvatar') || defaultAvatar
  nickname.value = localStorage.getItem('userNickname') || '苏黎世的从前'
}
onMounted(() => {
  checkLogin()
  loadSearchHistory()
  loadHotSearches()
  window.addEventListener('user-avatar-changed', checkLogin)
  document.addEventListener('click', handleClickOutside)
})
onUnmounted(() => {
  window.removeEventListener('user-avatar-changed', checkLogin)
  document.removeEventListener('click', handleClickOutside)
})
watch(() => route.fullPath, checkLogin)

const isHome = computed(() => {
  // 只在首页和常规内容页高亮，不在/my-music和/0717高亮
  if (route.path === '/my-music' || route.path === '/0717') return false;
  return [
    '/', '/artist', '/album', '/toplist', '/mv', '/song', '/playlist', '/search'
  ].some(p => route.path === p || route.path.startsWith(p + '/'))
})
const isOpenPlatform = computed(() => route.path === '/open-platform')
const isMyMusic = computed(() => route.path === '/my-music')
const is0717 = computed(() => route.path === '/0717')

function handleLogin() {
  // 登录逻辑（可对接API）
  dialogVisible.value = false
}
function handleRegister() {
  // 注册逻辑（可对接API）
  dialogVisible.value = false
}
function goLogin() {
  if (route.path !== '/login') {
    router.push('/login')
  }
}
function goHome() {
  if (route.path !== '/') {
    router.push('/')
  }
}


function goOpenPlatform() {
  if (route.path !== '/open-platform') {
    router.push('/open-platform')
  }
}
function goMyMusic() {
  if (route.path !== '/my-music') {
    router.push('/my-music')
  }
}
function goProfile() {
  if (route.path !== '/profile') {
    router.push('/profile')
  }
  showDropdown.value = false
}
function selectPresetTheme(themeName) {
  currentTheme.value = themeName
  localStorage.setItem('theme', themeName)
  document.documentElement.setAttribute('data-theme', themeName)
  
  // 触发主题变化事件，通知其他组件
  window.dispatchEvent(new CustomEvent('theme-changed', { detail: { theme: themeName } }))
  
  ElMessage.success(`已切换到${presetColors[themeName].name}主题`)
  showColorPicker.value = false
}

function showBackgroundSelector() {
  showBackgroundModal.value = true
}

function selectBackground(url, isProcessed = false) {
  currentBackground.value = url
  
  // 尝试保存到localStorage
  try {
    localStorage.setItem('userBannerBg', url)
    localStorage.removeItem('isCustomBackground')
    
    // 如果是经过处理的背景，标记一下
    if (isProcessed) {
      localStorage.setItem('backgroundProcessed', 'true')
    } else {
      localStorage.removeItem('backgroundProcessed')
    }
    
  } catch (error) {
    if (error.name === 'QuotaExceededError') {
      // 存储失败时给出提示，但不阻止背景应用
      ElMessage.warning('背景过大，无法保存到本地，刷新页面后将恢复默认背景')
    }
  }
  
  showBackgroundModal.value = false
  
  // 添加到最近使用（但不包括空背景和超大背景）
  if (url && !url.startsWith('data:') && !recentBackgrounds.value.includes(url)) {
    recentBackgrounds.value.unshift(url)
    if (recentBackgrounds.value.length > 5) {
      recentBackgrounds.value.pop() // 只保留最近5个
    }
    try {
      localStorage.setItem('recentBackgrounds', JSON.stringify(recentBackgrounds.value))
    } catch (error) {
      // 静默处理存储失败
    }
  }
  
  // 立即应用背景（在发送事件之前）
  applyBackgroundDirectly(url)
  
  // 立即触发全局事件，通知其他组件更新背景
  window.dispatchEvent(new CustomEvent('background-changed', { detail: { url } }))
  
  // 根据是否有背景显示不同的提示
  const message = url ? '背景更换成功！' : '已移除背景！'
  setTimeout(() => {
    ElMessage({
      message: message,
      type: 'success',
      duration: 2000,
      showClose: true
    })
  }, 100) // 延迟显示提示，确保背景应用后再显示
}

// 直接应用背景的函数
function applyBackgroundDirectly(url) {
  if (url) {
    document.body.style.backgroundImage = `url(${url})`
    document.body.style.backgroundSize = 'cover'
    document.body.style.backgroundPosition = 'center'
    document.body.style.backgroundRepeat = 'no-repeat'
    document.body.style.backgroundAttachment = 'fixed'
  } else {
    // 移除背景
    document.body.style.backgroundImage = ''
    document.body.style.backgroundSize = ''
    document.body.style.backgroundPosition = ''
    document.body.style.backgroundRepeat = ''
    document.body.style.backgroundAttachment = ''
  }
}

async function onBackgroundUpload(e) {
  const file = e.target.files[0]
  if (!file) {
    return
  }

  // 验证文件
  const validation = validateImageFile(file)
  if (!validation.isValid) {
    validation.errors.forEach(error => ElMessage.error(error))
    return
  }

  // 显示压缩提示
  if (validation.needsCompression) {
    ElMessage.info('检测到图片较大，正在进行优化处理...')
  }

  try {
    // 压缩图片
    const result = await compressImage(file)
    
    // 检查压缩后的大小
    if (parseFloat(result.compressedSize) > 3) {
      ElMessage.warning('图片较大，建议使用更小的图片以获得更好的性能')
    }

    console.log(`图片优化完成: 原始${result.originalSize}MB -> 压缩后${result.compressedSize}MB (压缩率${result.compressionRatio}%)`)
    
    // 使用压缩后的图片
    selectBackground(result.dataUrl, true) // 传递true表示这是经过处理的背景
    
  } catch (error) {
    console.error('图片处理失败:', error)
    ElMessage.error('图片处理失败，请重试')
  } finally {
    // 清空文件输入框，允许重复上传同一文件
    e.target.value = ''
  }
}

// 搜索相关方法
function handleSearchFocus() {
  // 聚焦时显示搜索下拉框
  showSearchResults.value = true
  if (searchQuery.value.trim() === '') {
    // 空搜索时显示历史和热门搜索
    searchResults.value = []
  }
}

async function handleSearchInput() {
  const keyword = searchQuery.value.trim()
  
  // 清除之前的搜索定时器
  if (searchTimer.value) {
    clearTimeout(searchTimer.value)
  }
  
  if (keyword === '') {
    showSearchResults.value = true // 显示历史和热门搜索
    searchResults.value = []
    return
  }
  
  // 防抖搜索
  searchTimer.value = setTimeout(async () => {
    await performSearch(keyword)
  }, 300)
}

async function performSearch(keyword) {
  if (!keyword) return
  
  try {
    isSearching.value = true
    showSearchResults.value = true
    
    // 调用真实搜索API
    const response = await searchAll(keyword, 0, 20)
    
    if (response && response.code === 200) {
      // 处理搜索结果数据
      const results = []
      const data = response.data || {}
      
      // 合并不同类型的搜索结果
      if (data.songs && data.songs.length > 0) {
        results.push(...data.songs.slice(0, 5).map(song => ({
          ...song,
          type: 'song'
        })))
      }
      
      if (data.artists && data.artists.length > 0) {
        results.push(...data.artists.slice(0, 3).map(artist => ({
          ...artist,
          type: 'artist'
        })))
      }
      
      if (data.albums && data.albums.length > 0) {
        results.push(...data.albums.slice(0, 3).map(album => ({
          ...album,
          type: 'album'
        })))
      }
      
      if (data.playlists && data.playlists.length > 0) {
        results.push(...data.playlists.slice(0, 3).map(playlist => ({
          ...playlist,
          type: 'playlist'
        })))
      }
      
      if (data.mvs && data.mvs.length > 0) {
        results.push(...data.mvs.slice(0, 2).map(mv => ({
          ...mv,
          type: 'mv'
        })))
      }
      
      searchResults.value = results
    } else {
      // API调用失败，使用模拟数据
      searchResults.value = getMockSearchResults(keyword)
    }
  } catch (error) {
    console.error('搜索失败:', error)
    // 搜索失败时使用模拟数据
    searchResults.value = getMockSearchResults(keyword)
  } finally {
    isSearching.value = false
  }
}

function getMockSearchResults(keyword) {
  const mockResults = [
    { id: 1, type: 'song', name: '演员', artistName: '薛之谦' },
    { id: 2, type: 'artist', name: '薛之谦', description: '华语流行歌手' },
    { id: 3, type: 'album', name: '天外来物', artistName: '薛之谦' },
    { id: 4, type: 'playlist', name: '流行热歌', description: '1000万播放' },
    { id: 5, type: 'mv', name: '演员MV', artistName: '薛之谦' }
  ]
  
  return mockResults.filter(result => 
    result.name.toLowerCase().includes(keyword.toLowerCase()) ||
    (result.artistName && result.artistName.toLowerCase().includes(keyword.toLowerCase())) ||
    (result.description && result.description.toLowerCase().includes(keyword.toLowerCase()))
  )
}

async function handleSearch() {
  const keyword = searchQuery.value.trim()
  if (!keyword) return
  
  // 保存搜索历史
  await saveToSearchHistory(keyword)
  
  // 执行搜索并显示结果
  await performSearch(keyword)
  
  ElMessage.success(`搜索"${keyword}"完成`)
}

function selectSearchResult(result) {
  // 保存搜索历史
  saveToSearchHistory(searchQuery.value)
  
  // 根据结果类型跳转到相应页面
  let targetPath = ''
  switch (result.type) {
    case 'song':
      targetPath = `/song/${result.id}`
      break
    case 'artist':
      targetPath = `/artist/${result.id}`
      break
    case 'album':
      targetPath = `/album/${result.id}`
      break
    case 'playlist':
      targetPath = `/playlist/${result.id}`
      break
    case 'mv':
      targetPath = `/mv/${result.id}`
      break
  }
  
  // 检查路由后再导航
  if (targetPath && route.path !== targetPath) {
    router.push(targetPath)
  }
  
  // 清空搜索框和结果
  searchQuery.value = ''
  showSearchResults.value = false
  searchResults.value = []
}

// 搜索历史相关方法
function selectHistoryItem(keyword) {
  searchQuery.value = keyword
  handleSearch()
}

function selectHotSearch(item) {
  const keyword = item.keyword || item
  
  // 如果热门搜索项有类型和ID信息，直接跳转到对应页面
  if (item.type && item.id) {
    // 保存搜索历史
    saveToSearchHistory(keyword)
    
    // 清空搜索框和结果
    searchQuery.value = ''
    showSearchResults.value = false
    searchResults.value = []
    
    // 根据类型跳转到对应页面
    switch (item.type) {
      case 'artist':
        router.push(`/artist/${item.id}`)
        break
      case 'song':
        router.push(`/song/${item.id}`)
        break
      case 'album':
        router.push(`/album/${item.id}`)
        break
      case 'playlist':
        router.push(`/playlist/${item.id}`)
        break
      case 'mv':
        router.push(`/mv/${item.id}`)
        break
      default:
        // 没有明确类型时，进行搜索
        searchQuery.value = keyword
        handleSearch()
    }
  } else {
    // 没有类型信息时，进行搜索
    searchQuery.value = keyword
    handleSearch()
  }
}

async function handleClearHistory() {
  try {
    await clearSearchHistory()
    searchHistory.value = []
    ElMessage.success('搜索历史已清空')
  } catch (error) {
    console.error('清空搜索历史失败:', error)
    // 本地清空
    searchHistory.value = []
    localStorage.removeItem('searchHistory')
    ElMessage.success('搜索历史已清空')
  }
}

async function saveToSearchHistory(keyword) {
  if (!keyword || !keyword.trim()) return
  
  try {
    await saveSearchHistory(keyword)
    // 更新本地搜索历史
    loadSearchHistory()
  } catch (error) {
    console.error('保存搜索历史失败:', error)
    // 本地保存
    const history = JSON.parse(localStorage.getItem('searchHistory') || '[]')
    if (!history.includes(keyword)) {
      history.unshift(keyword)
      if (history.length > 20) {
        history.pop()
      }
      localStorage.setItem('searchHistory', JSON.stringify(history))
      searchHistory.value = history
    }
  }
}

// 加载搜索历史
async function loadSearchHistory() {
  try {
    const response = await getSearchHistory()
    if (response && response.code === 200) {
      searchHistory.value = response.data || []
    } else {
      // 从本地加载
      searchHistory.value = JSON.parse(localStorage.getItem('searchHistory') || '[]')
    }
  } catch (error) {
    console.error('加载搜索历史失败:', error)
    searchHistory.value = JSON.parse(localStorage.getItem('searchHistory') || '[]')
  }
}

// 加载热门搜索
async function loadHotSearches() {
  try {
    const response = await getHotSearch()
    if (response && response.code === 200 && response.data && response.data.length > 0) {
      // 处理后端返回的数据，确保薛之谦和周杰伦排在前面
      let hotData = response.data
      
      // 查找薛之谦和周杰伦的数据
      const xuezhiqian = hotData.find(item => item.name === '薛之谦' || item.keyword === '薛之谦')
      const zhoujielun = hotData.find(item => item.name === '周杰伦' || item.keyword === '周杰伦')
      
      // 移除原有的薛之谦和周杰伦数据
      hotData = hotData.filter(item => 
        item.name !== '薛之谦' && item.keyword !== '薛之谦' &&
        item.name !== '周杰伦' && item.keyword !== '周杰伦'
      )
      
      // 构建最终的热门搜索列表
      const finalHotSearches = []
      
      // 第一位：薛之谦
      if (xuezhiqian) {
        finalHotSearches.push({
          keyword: xuezhiqian.name || xuezhiqian.keyword || '薛之谦',
          hot: true,
          type: 'artist',
          id: xuezhiqian.id || 1
        })
      } else {
        finalHotSearches.push({
          keyword: '薛之谦',
          hot: true,
          type: 'artist',
          id: 1
        })
      }
      
      // 第二位：周杰伦
      if (zhoujielun) {
        finalHotSearches.push({
          keyword: zhoujielun.name || zhoujielun.keyword || '周杰伦',
          hot: true,
          type: 'artist',
          id: zhoujielun.id || 2
        })
      } else {
        finalHotSearches.push({
          keyword: '周杰伦',
          hot: true,
          type: 'artist',
          id: 2
        })
      }
      
      // 添加其他热门搜索数据
      hotData.slice(0, 8).forEach((item, index) => {
        finalHotSearches.push({
          keyword: item.name || item.keyword || item.title,
          hot: index < 1, // 前1个标记为热门
          type: item.type || 'artist',
          id: item.id || (index + 3)
        })
      })
      
      hotSearches.value = finalHotSearches
      console.log('🔥 热门搜索数据加载成功:', finalHotSearches)
    } else {
      // 后端没有数据或请求失败时使用默认数据
      console.log('⚠️ 后端热门搜索数据为空，使用默认数据')
      hotSearches.value = getDefaultHotSearches()
    }
  } catch (error) {
    console.error('❌ 加载热门搜索失败:', error)
    // 网络错误时使用默认数据
    hotSearches.value = getDefaultHotSearches()
  }
}

// 获取默认热门搜索数据
function getDefaultHotSearches() {
  return [
    { keyword: '薛之谦', hot: true, type: 'artist', id: 1 },
    { keyword: '周杰伦', hot: true, type: 'artist', id: 2 },
    { keyword: '林俊杰', hot: true, type: 'artist', id: 3 },
    { keyword: '邓紫棋', type: 'artist', id: 4 },
    { keyword: '张学友', type: 'artist', id: 5 },
    { keyword: '陈奕迅', type: 'artist', id: 6 },
    { keyword: '演员', type: 'song', id: 1 },
    { keyword: '稻香', type: 'song', id: 2 },
    { keyword: '华语流行', type: 'playlist', id: 1 },
    { keyword: '经典老歌', type: 'playlist', id: 2 }
  ]
}

// 辅助方法
function getResultIcon(type) {
  const icons = {
    song: '🎵',
    artist: '👤',
    album: '💿',
    playlist: '📝',
    mv: '🎬'
  }
  return icons[type] || '🎵'
}

function getResultSubtitle(result) {
  switch (result.type) {
    case 'song':
      return result.artistName || result.artist || '未知歌手'
    case 'artist':
      return result.description || '歌手'
    case 'album':
      return result.artistName || result.artist || '未知歌手'
    case 'playlist':
      return result.description || `${result.trackCount || 0}首歌曲`
    case 'mv':
      return result.artistName || result.artist || '未知歌手'
    default:
      return ''
  }
}

function getResultTypeText(type) {
  const typeTexts = {
    song: '歌曲',
    artist: '歌手',
    album: '专辑',
    playlist: '歌单',
    mv: 'MV'
  }
  return typeTexts[type] || ''
}

function getHotSearchTypeText(type) {
  const typeTexts = {
    song: '歌曲',
    artist: '歌手',
    album: '专辑',
    playlist: '歌单',
    mv: 'MV'
  }
  return typeTexts[type] || ''
}

function applyCustomColor() {
  // 生成自定义主题的CSS变量
  const color = customColor.value
  // 提取颜色的色相值（简单计算）
  const hex = color.replace('#', '')
  const r = parseInt(hex.substr(0, 2), 16) / 255
  const g = parseInt(hex.substr(2, 2), 16) / 255
  const b = parseInt(hex.substr(4, 2), 16) / 255
  
  // 计算色相 (简单版本)
  let hue = 0
  const cmax = Math.max(r, g, b)
  const cmin = Math.min(r, g, b)
  const delta = cmax - cmin
  
  if (delta === 0) {
    hue = 0
  } else if (cmax === r) {
    hue = ((g - b) / delta) % 6
  } else if (cmax === g) {
    hue = (b - r) / delta + 2
  } else {
    hue = (r - g) / delta + 4
  }
  
  hue = Math.round(hue * 60)
  if (hue < 0) hue += 360
  
  const style = document.createElement('style')
  style.id = 'custom-theme'
  style.textContent = `
    [data-theme="custom"] {
      --primary: ${color};
      --primary-light: ${adjustBrightness(color, 20)};
      --primary-dark: ${adjustBrightness(color, -20)};
      --secondary: ${adjustHue(color, 30)};
      --accent: ${adjustHue(color, 60)};
      --background: ${adjustBrightness(color, 95)};
      --background-light: ${adjustBrightness(color, 98)};
      --background-card: ${adjustBrightness(color, 95, 0.95)};
      --text-primary: ${adjustBrightness(color, -70)};
      --text-secondary: ${color};
      --text-tertiary: ${adjustBrightness(color, 20)};
      --border: ${adjustBrightness(color, 0, 0.3)};
      --custom-hue: ${hue}deg;
      --success: #10b981;
      --warning: #f59e0b;
      --error: #ef4444;
    }
  `
  
  // 移除旧的自定义主题样式
  const oldStyle = document.getElementById('custom-theme')
  if (oldStyle) oldStyle.remove()
  
  // 添加新的自定义主题样式
  document.head.appendChild(style)
  
  // 应用自定义主题
  currentTheme.value = 'custom'
  localStorage.setItem('theme', 'custom')
  localStorage.setItem('customColor', color)
  document.documentElement.setAttribute('data-theme', 'custom')
  
  // 触发主题变化事件，通知其他组件
  window.dispatchEvent(new CustomEvent('theme-changed', { detail: { theme: 'custom' } }))
  
  ElMessage.success('已应用自定义颜色主题')
  showColorPicker.value = false
}

// 辅助函数：调整颜色亮度
function adjustBrightness(color, percent, alpha = 1) {
  const hex = color.replace('#', '')
  const r = parseInt(hex.substr(0, 2), 16)
  const g = parseInt(hex.substr(2, 2), 16)
  const b = parseInt(hex.substr(4, 2), 16)
  
  const factor = 1 + percent / 100
  const newR = Math.min(255, Math.max(0, Math.round(r * factor)))
  const newG = Math.min(255, Math.max(0, Math.round(g * factor)))
  const newB = Math.min(255, Math.max(0, Math.round(b * factor)))
  
  return `rgba(${newR}, ${newG}, ${newB}, ${alpha})`
}

// 辅助函数：调整色相
function adjustHue(color, degrees) {
  const hex = color.replace('#', '')
  const r = parseInt(hex.substr(0, 2), 16)
  const g = parseInt(hex.substr(2, 2), 16)
  const b = parseInt(hex.substr(4, 2), 16)
  
  // 简单的色相调整
  const factor = degrees / 360
  const newR = Math.min(255, Math.max(0, Math.round(r + factor * 255)))
  const newG = Math.min(255, Math.max(0, Math.round(g + factor * 255)))
  const newB = Math.min(255, Math.max(0, Math.round(b + factor * 255)))
  
  return `#${newR.toString(16).padStart(2, '0')}${newG.toString(16).padStart(2, '0')}${newB.toString(16).padStart(2, '0')}`
}

function go0717() {
  if (route.path !== '/0717') {
    router.push('/0717')
  }
}

function goToGame() {
  if (route.path !== '/game') {
    router.push('/game')
  }
}

function toggleDropdown(event) {
  showDropdown.value = !showDropdown.value
  event && event.stopPropagation()
  showTooltip.value = false
}
function handleClickOutside(e) {
  const avatar = document.querySelector('.user-avatar')
  const dropdown = document.querySelector('.profile-dropdown')
  const searchResults = document.querySelector('.search-results')
  const searchInput = document.querySelector('.search-input')
  
  // 关闭用户下拉菜单
  if (
    showDropdown.value &&
    avatar && !avatar.contains(e.target) &&
    (!dropdown || !dropdown.contains(e.target))
  ) {
    showDropdown.value = false
  }
  
  // 关闭搜索结果
  if (
    showSearchResults.value &&
    searchInput && !searchInput.contains(e.target) &&
    (!searchResults || !searchResults.contains(e.target))
  ) {
    showSearchResults.value = false
  }
}
function logout() {
  localStorage.removeItem('isLogin')
  checkLogin()
  showDropdown.value = false
  
  // 只有在不在首页时才跳转到首页，避免冗余导航警告
  if (route.path !== '/') {
    router.push('/')
  }
}
</script>

<style scoped>
.header-nav {
  position: relative;
  width: 100%;
  z-index: 1000;
  background: #fff !important; /* 强制默认白色背景 */
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #e5e7eb;
  transition: background-color var(--transition-normal);
}

/* 仅黑色主题下导航栏使用黑色背景 */
[data-theme="black"] .header-nav {
  background: #000 !important;
  border-bottom: 1px solid #333 !important;
}

/* 确保非黑色主题下导航栏保持白色 - 增加优先级 */
/* 白色主题专用样式 */
[data-theme="white"] .header-nav {
  background: #fff !important;
  border-bottom: 1px solid #e5e7eb !important;
}

/* 其他非黑色主题样式 */
:root:not([data-theme="black"]):not([data-theme="white"]) .header-nav {
  background: #fff !important;
  border-bottom: 1px solid #e5e7eb !important;
}
/* 移除导航栏整体悬停效果 */
/* .header-nav:hover {
  background: rgba(10, 25, 47, 0.9);
} */
.header-content {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  height: 72px;
  justify-content: space-between;
  padding: 0 20px;
  box-sizing: border-box;
}

.main-menu {
  display: flex;
  flex-wrap: nowrap;
  white-space: nowrap;
}

.search-user-area {
  display: flex;
  align-items: center;
  white-space: nowrap;
}
.logo-area {
  display: flex;
  align-items: center;
  margin-right: var(--space-xl);
  padding: 8px 12px;
  border-radius: 8px;
}
.logo-img {
  height: 40px;
  width: auto;
  display: block;
  transition: transform var(--transition-normal);
}

/* 移除所有主题下的logo滤镜设置，确保logo正常显示 */
/* 各主题下的logo样式可以根据实际需求重新配置 */


.logo-img:hover {
  transform: scale(1.05);
}
.main-menu {
  display: flex;
  align-items: center;
  gap: var(--space-xl);
}
.menu-item {
  position: relative;
  color: #333;
  font-weight: 700;
  font-size: 17px;
  text-decoration: none;
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--border-radius);
  transition: background 0.2s, color 0.2s;
  letter-spacing: 0.5px;
}

.menu-item:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #333;
  border-radius: 4px;
  transform: translateY(-1px);
}
.menu-item.active {
  color: #333;
  background: rgba(0, 0, 0, 0.08);
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.menu-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: var(--space-md);
  width: calc(100% - var(--space-xl));
  height: 2px;
  background: #333;
  border-radius: 1px;
}
.search-user-area {
  display: flex;
  align-items: center;
  gap: var(--space-xl);
}
.search-input {
  width: 240px;
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid #e5e7eb;
  border-radius: var(--border-radius-xl);
  padding: 10px 16px;
  color: #2d3748;
  font-size: 15px;
  font-weight: 500;
  transition: all var(--transition-normal);
}
.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  background: #ffffff;
}
.search-input::placeholder {
  color: #718096;
  font-weight: 400;
}
.user-actions {
  display: flex;
  align-items: center;
}
.login-nav-btn {
  background: var(--background-card);
  color: var(--text-primary);
  border: 1px solid var(--border);
  border-radius: var(--border-radius-xl);
  padding: 10px 20px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all var(--transition-normal);
}

/* 黑色主题下的登录按钮 */
[data-theme="black"] .login-nav-btn {
  background: #000;
  color: #fff;
  border: 1px solid #fff;
}

.login-nav-btn:hover {
  background: var(--background-light);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 黑色主题下的登录按钮悬停效果 */
[data-theme="black"] .login-nav-btn:hover {
  background: #333;
}
.user-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid transparent;
  transition: all var(--transition-normal);
}
.user-avatar img:hover {
  border-color: var(--primary-light);
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(129, 140, 248, 0.3);
}
.profile-dropdown {
  background: var(--background-light);
  border: 1px solid var(--border);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--space-md);
  min-width: 240px;
}
.dropdown-header {
  padding-bottom: var(--space-md);
  margin-bottom: var(--space-md);
  border-bottom: 1px solid var(--border);
}
.dropdown-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
}
.dropdown-nickname {
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}
.dropdown-item {
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--border-radius);
  transition: background-color var(--transition-fast);
}
.dropdown-item:hover {
  background: rgba(129, 140, 248, 0.1);
}
.dropdown-item.logout-item {
  color: var(--error);
}
.menu-item {
  font-size: 18px;
  color: var(--text-primary);
  text-decoration: none;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s, color 0.2s;
}
.menu-item.active {
  background: var(--primary);
  color: var(--text-primary);
}
.search-user-area {
  display: flex;
  align-items: center;
  gap: 16px;
}
.search-input {
  width: 220px;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: 18px;
  padding: 0 16px;
  font-size: 16px;
  outline: none;
  transition: border 0.2s;
  background: var(--background-light);
  color: var(--text-primary);
}
.search-input:focus {
  border: 1.5px solid var(--success);
}
.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1.5px solid #e5e5e5;
}
.login-nav-btn {
  margin-left: 8px;
  padding: 0 18px;
  height: 36px;
  border: 1.5px solid var(--primary);
  background: var(--background-card);
  border-bottom: 1px solid var(--border);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  color: var(--primary-dark);
  border-radius: 18px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, border 0.2s;
}
.login-nav-btn:hover {
  background: inherit;
  color: inherit;
  border-color: inherit;
}
.profile-dropdown {
  position: fixed;
  top: 64px;
  right: 40px;
  background: var(--background-card);
  border: 1px solid #eee;
  box-shadow: 0 4px 24px rgba(0,0,0,0.13);
  border-radius: 12px;
  min-width: 260px;
  z-index: 1000;
  padding: 0 0 8px 0;
  animation: fadeIn 0.18s;
  max-height: none;
  overflow: visible;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.dropdown-arrow {
  position: absolute;
  top: -10px;
  right: 24px;
  width: 18px;
  height: 10px;
  overflow: hidden;
}
.dropdown-arrow::after {
  content: '';
  display: block;
  width: 18px;
  height: 18px;
  background: var(--background-card);
  border-left: 1px solid #eee;
  border-top: 1px solid #eee;
  transform: rotate(45deg);
  position: absolute;
  top: 6px;
  left: 0;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.06);
}
.dropdown-header {
  display: flex;
  align-items: center;
  padding: 18px 18px 8px 18px;
}
.dropdown-avatar {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 14px;
  border: 2px solid #f2f2f2;
}
.dropdown-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.dropdown-nickname {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.dropdown-divider {
  height: 1px;
  background: var(--background-light);
  margin: 0 0 8px 0;
}
.dropdown-item {
  padding: 10px 24px;
  font-size: 15px;
  color: var(--text-primary);
  cursor: pointer;
  transition: background 0.18s;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dropdown-item i {
  font-size: 16px;
  color: var(--primary);
  margin-left: 8px;
}

/* 颜色选择器样式 */
.color-picker-container {
  padding: 20px;
}

.preset-colors h4,
.custom-color h4 {
  margin-bottom: 15px;
  color: var(--text-primary);
  font-weight: 600;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.color-item {
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
  position: relative;
}

.color-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.color-item.active {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.color-name {
  color: white;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.custom-color-input {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-input {
  width: 60px;
  height: 40px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.apply-btn {
  padding: 8px 16px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}

.apply-btn:hover {
  background: var(--primary-dark);
}

/* 背景选择器样式 */
.background-picker-container {
  padding: 20px;
}

.preset-backgrounds h4,
.custom-upload h4,
.recent-backgrounds h4 {
  margin-bottom: 15px;
  color: var(--text-primary);
  font-weight: 600;
}

.background-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.background-option {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 3px solid transparent;
}

.background-option:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.background-option.active {
  border-color: #31c27c;
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
  color: white;
  padding: 8px;
  text-align: center;
}

.bg-name {
  font-size: 12px;
  font-weight: 500;
}

.upload-area {
  text-align: center;
  padding: 20px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  background: #f9f9f9;
}

.upload-btn {
  background: #31c27c;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto 12px;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  background: #2fb374;
  transform: translateY(-2px);
}

.upload-icon {
  font-size: 16px;
}

.upload-tip {
  margin: 0;
  font-size: 12px;
  color: #666;
}

.upload-note {
  margin: 8px 0 0 0;
  font-size: 11px;
  color: #ff6b35;
  font-style: italic;
}
.dropdown-item:hover {
  background: var(--background-light);
}
.logout-item {
  color: #ef4444;
  font-weight: 600;
  border-top: 1px solid var(--border);
  margin-top: 6px;
}
.menu-item.active {
  color: var(--text-primary);
  font-weight: 500;
}
.menu-item {
  transition: background 0.18s, color 0.18s, border 0.18s;
  color: var(--text-primary);
}

/* 搜索相关样式 */
.search-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 0;
}

/* 独立星球图标样式 */
.independent-planet-icon {
  margin-left: 12px;
  display: flex;
  align-items: center;
}

.independent-planet-svg {
  width: 60px;
  height: 60px;
  transition: transform 20s linear infinite;
}

.independent-planet-icon:hover .independent-planet-svg {
  transform: rotate(360deg);
}

/* 原有导航菜单中的星球图标样式已移除 */

/* 保持原有搜索区域星球图标样式，但隐藏它 */
.planet-logo {
  display: none;
}

.planet-svg {
  width: 0;
  height: 0;
  transition: transform 20s linear infinite;
}

.planet-svg:hover {
  transform: rotate(360deg);
}

.search-input-container {
  position: relative;
  display: flex;
  align-items: center;
  min-width: 200px;
  width: 100%;
  max-width: 360px;
}

.search-input {
  width: 100%;
  padding: 8px 12px 8px 16px;
  padding-right: 40px;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
  box-sizing: border-box;
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-height: 400px;
  overflow-y: auto;
  margin-top: 8px;
  padding: 8px 0;
}

/* 搜索加载状态 */
.search-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #666;
  font-size: 14px;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #e5e7eb;
  border-top: 2px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 搜索结果项 */
.search-result-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin: 0 8px;
  border-radius: 8px;
}

.search-result-item:hover {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  transform: translateX(4px);
}

.result-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.result-info {
  flex: 1;
}

.result-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 2px;
}

.result-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.result-type {
  font-size: 11px;
  color: #9ca3af;
  background: #f3f4f6;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

/* 搜索分区标题 */
.search-section {
  margin-bottom: 16px;
}

.search-section-title {
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
  padding: 8px 16px;
  margin-bottom: 4px;
  display: block;
}

.search-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  margin-bottom: 4px;
}

.clear-history-btn {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 12px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.clear-history-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

/* 搜索历史 */
.history-list {
  padding: 0 8px;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 6px;
  margin-bottom: 2px;
}

.history-item:hover {
  background: #f8fafc;
  transform: translateX(2px);
}

.history-icon {
  margin-right: 10px;
  font-size: 14px;
  opacity: 0.6;
}

.history-text {
  font-size: 13px;
  color: #4b5563;
}

/* 热门搜索 */
.hot-search-list {
  padding: 0 8px;
}

.hot-search-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 6px;
  margin-bottom: 2px;
}

.hot-search-item:hover {
  background: linear-gradient(135deg, #fef7f0 0%, #fed7aa 100%);
  transform: translateX(2px);
}

.hot-search-item.hot-top {
  background: linear-gradient(135deg, #fef2f2 0%, #fecaca 100%);
}

.hot-search-item.hot-top:hover {
  background: linear-gradient(135deg, #fee2e2 0%, #fca5a5 100%);
}

.hot-rank {
  font-size: 12px;
  font-weight: bold;
  color: #ef4444;
  min-width: 20px;
  margin-right: 8px;
}

.hot-search-item.hot-top .hot-rank {
  color: #dc2626;
}

.hot-text {
  font-size: 13px;
  color: #374151;
  flex: 1;
}

.hot-type {
  font-size: 10px;
  color: #9ca3af;
  background: #f3f4f6;
  padding: 1px 4px;
  border-radius: 3px;
  margin-left: 8px;
  font-weight: 500;
}

.hot-badge {
  font-size: 12px;
  margin-left: 4px;
}

.no-results {
  padding: 24px 16px;
  text-align: center;
  color: #666;
}

.no-results-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.no-results-text {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.no-results-tip {
  font-size: 12px;
  color: #999;
}
/* 黑色主题下的导航栏菜单样式 */
[data-theme="black"] .menu-item {
  color: #fff;
}

/* 黑色主题下的激活状态导航栏样式 */
[data-theme="black"] .menu-item.active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

/* 黑色主题下的激活状态导航栏下划线 */
[data-theme="black"] .menu-item.active::after {
  background: #ffffff;
}

/* 黑色主题下的搜索输入框样式 */
[data-theme="black"] .search-input {
  background: #333;
  border-color: #555;
  color: #fff;
}

/* 确保导航栏在各种屏幕尺寸下保持固定样式 */
@media screen and (max-width: 1200px) {
  .header-nav {
    min-width: 1000px;
  }
}

@media screen and (max-width: 992px) {
  .header-nav {
    min-width: 900px;
  }
}

@media screen and (max-width: 768px) {
  .header-nav {
    min-width: 700px;
  }
}

@media screen and (max-width: 576px) {
  .header-nav {
    min-width: 600px;
  }
}

/* 黑色主题下的搜索输入框聚焦样式 */
[data-theme="black"] .search-input:focus {
  border-color: #888;
}

/* 黑色主题下的登录按钮样式 */
[data-theme="black"] .login-nav-btn {
  background: transparent;
  border-color: #fff;
  color: #fff;
}

/* 黑色主题下的登录按钮悬停样式 */
[data-theme="black"] .login-nav-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 黑色主题下的用户头像边框 */
[data-theme="black"] .user-avatar img {
  border-color: #fff;
}

/* 黑色主题下的搜索结果面板 */
[data-theme="black"] .search-results {
  background: rgba(30, 30, 30, 0.95);
  border: 1px solid #444;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.4);
}

/* 黑色主题下的搜索结果项 */
[data-theme="black"] .search-result-item {
  border-bottom: 1px solid #444;
}

[data-theme="black"] .search-result-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 黑色主题下的搜索结果文本 */
[data-theme="black"] .result-title {
  color: #fff;
}

[data-theme="black"] .result-subtitle {
  color: #aaa;
}

[data-theme="black"] .no-results-text {
  color: #fff;
}

[data-theme="black"] .no-results-tip {
  color: #aaa;
}

/* 黑色主题下的无结果提示 */
[data-theme="black"] .no-results {
  color: #aaa;
}

/* 黑色主题下的logo图片样式 - 确保logo显示为白色 */
[data-theme="black"] .logo-img {
  filter: invert(100%) !important;
  -webkit-filter: invert(100%) !important;
  -moz-filter: invert(100%) !important;
  -ms-filter: invert(100%) !important;
  -o-filter: invert(100%) !important;
  opacity: 1 !important;
  display: block !important;
  width: auto !important;
  height: 40px !important;
}

</style>