// 头部导航组件
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
            <button 
              v-if="searchQuery" 
              class="clear-search-btn" 
              @click="clearSearch"
              title="清除搜索内容"
            >
              ✕
            </button>
            <button class="search-icon-button" @click="handleSearch" title="搜索">
              <svg class="search-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
                <path d="M351.1808 59.2896A435.2 435.2 0 0 1 805.376 715.264 460.8 460.8 0 0 1 351.1808 59.3408z" fill="#20C997"></path>
                <path d="M754.3808 722.2272a358.4 358.4 0 1 0-267.8272 120.2176 51.2 51.2 0 0 1 0 102.4 460.8 460.8 0 1 1 365.1584-179.712l118.8864 121.2416c23.7568 24.2176 23.552 63.0272-0.4096 87.04l-0.4096 0.4096a61.184 61.184 0 0 1-86.9888-0.4608l-148.0192-150.9376a61.7984 61.7984 0 0 1 0.4096-86.9888l0.4096-0.4096c5.632-5.5808 11.9808-9.8304 18.7904-12.8z m-467.968-364.5952h409.6a51.2 51.2 0 1 1 0 102.4h-409.6a51.2 51.2 0 1 1 0-102.4z m0 204.8h256a51.2 51.2 0 0 1 0 102.4h-256a51.2 51.2 0 1 1 0-102.4z" fill="#2C6DD2"></path>
              </svg>
            </button>
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
            
            
            <div v-else>
              <!-- 搜索历史 -->
              <div v-if="searchHistory.length > 0" class="search-section">
                <div class="search-section-header">
                  <span class="search-section-title">搜索历史</span>
                  <button class="clear-history-btn" @click="handleClearHistory">清空</button>
                </div>
                <div class="history-list">
                  <div 
                    v-for="(item, index) in searchHistory.slice(0, 10)" 
                    :key="index" 
                    class="history-item"
                  >
                    <span class="history-text" @click="selectHistoryItem(item)">{{ item }}</span>
                    <button 
                      class="history-delete-btn" 
                      @click.stop="deleteHistoryItem(item)"
                      title="删除此搜索历史"
                    >
                      ✕
                    </button>
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
  <el-dialog v-model="showColorPicker" title="选择主题颜色" width="500px" center>
    <div class="color-picker-container">
      <div class="preset-colors">
        <div class="color-grid">
          <div 
            v-for="(color, name) in presetColors" 
            :key="name"
            class="color-item"
            :class="{ active: currentTheme === name }"
            :style="{ 
              background: `linear-gradient(135deg, ${color.primary} 0%, ${adjustBrightness(color.primary, 20)} 100%)`,
              border: `2px solid ${color.primary}`
            }"
            @click="selectPresetTheme(name)"
          >
            <div class="color-preview" :style="{ backgroundColor: color.background }">
              <div class="color-text" :style="{ color: color.textPrimary }">Aa</div>
            </div>
            <span class="color-name">{{ color.name }}</span>
          </div>
        </div>
      </div>
      
      <div class="theme-tip">
        <p>✨ 选择您喜欢的主题颜色，系统会自动应用相应的配色方案</p>
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
import { searchAll, searchSuggest, getSearchHistory, saveSearchHistory, deleteSearchHistoryItem, clearSearchHistory, fuzzySearch, smartSuggest } from '@/api/search.js'
import { getCurrentUserInfo, getUsername, getUserAvatar, initUserInfo, getCurrentUserId } from '@/utils/userStore.js'

const router = useRouter()
const route = useRoute()
const dialogVisible = ref(false)
const activeTab = ref('login')
const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', confirmPassword: '' })

const isLogin = ref(false)
const defaultAvatar = 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100'
const showDropdown = ref(false)
const showTooltip = ref(false)

// 使用统一的用户信息管理，确保从数据库获取最新数据
const userInfo = getCurrentUserInfo()
const avatarImg = computed(() => userInfo.avatar || defaultAvatar)
const nickname = computed(() => userInfo.username || '用户')
const currentTheme = ref(localStorage.getItem('theme') || 'pink')
const showColorPicker = ref(false)
const showBackgroundModal = ref(false)
const currentBackground = ref(localStorage.getItem('userBannerBg') || '')
const recentBackgrounds = ref(JSON.parse(localStorage.getItem('recentBackgrounds') || '[]'))
const backgroundInput = ref(null)

// 搜索相关
const searchQuery = ref('')
const showSearchResults = ref(false)
const searchResults = ref([])
const searchHistory = ref([])
const isSearching = ref(false)
const searchTimer = ref(null)

const presetColors = {
  pink: { 
    name: '粉色', 
    primary: '#ec4899',
    background: '#fdf2f8',
    backgroundCard: 'rgba(253, 242, 248, 0.95)',
    textPrimary: '#831843',
    textSecondary: '#be185d'
  },
  lightPink: { 
    name: '浅粉色', 
    primary: '#f7b9c8',
    background: '#fef9fa',
    backgroundCard: 'rgba(252, 231, 237, 0.95)',
    textPrimary: '#4a1e2b',
    textSecondary: '#7d4a5a'
  },
  blue: { 
    name: '蓝色', 
    primary: '#3b82f6',
    background: '#eff6ff',
    backgroundCard: 'rgba(239, 246, 255, 0.95)',
    textPrimary: '#1e3a8a',
    textSecondary: '#2563eb'
  },
  green: { 
    name: '绿色', 
    primary: '#10b981',
    background: '#f0fdf4',
    backgroundCard: 'rgba(240, 253, 244, 0.95)',
    textPrimary: '#064e3b',
    textSecondary: '#059669'
  },
  purple: { 
    name: '紫色', 
    primary: '#8b5cf6',
    background: '#faf5ff',
    backgroundCard: 'rgba(250, 245, 255, 0.95)',
    textPrimary: '#4c1d95',
    textSecondary: '#7c3aed'
  },
  orange: { 
    name: '橙色', 
    primary: '#f97316',
    background: '#fff7ed',
    backgroundCard: 'rgba(255, 247, 237, 0.95)',
    textPrimary: '#7c2d12',
    textSecondary: '#ea580c'
  },
  red: { 
    name: '红色', 
    primary: '#ef4444',
    background: '#fef2f2',
    backgroundCard: 'rgba(254, 242, 242, 0.95)',
    textPrimary: '#7f1d1d',
    textSecondary: '#dc2626'
  },
  black: { 
    name: '黑色', 
    primary: '#ffffff',
    background: '#000000',
    backgroundCard: 'rgba(0, 0, 0, 0.95)',
    textPrimary: '#ffffff',
    textSecondary: '#e5e5e5'
  },
  white: { 
    name: '白色', 
    primary: '#696969',
    background: '#ffffff',
    backgroundCard: 'rgba(255, 255, 255, 0.95)',
    textPrimary: '#000000',
    textSecondary: '#333333'
  }
}

const presetBackgrounds = [
  { name: '星空', url: 'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=1920&h=1080&fit=crop&q=80' },
  { name: '海滩', url: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1920&h=1080&fit=crop&q=80' },
  { name: '城市', url: 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=1920&h=1080&fit=crop&q=80' },
  { name: '森林', url: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1920&h=1080&fit=crop&q=80' },
  { name: '雪山', url: 'https://images.unsplash.com/photo-1464822759844-d150baec0134?w=1920&h=1080&fit=crop&q=80' },
  { name: '日落', url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&q=80' }
]

async function checkLogin() {
  isLogin.value = localStorage.getItem('isLogin') === '1'
  
  // 如果已登录，从数据库获取最新用户信息
  if (isLogin.value) {
    try {
      await initUserInfo()
      console.log('✅ HeaderNav: 用户信息已从数据库更新')
    } catch (error) {
      console.warn('⚠️ HeaderNav: 无法从数据库获取用户信息，使用本地缓存')
    }
  }
}

// 用户信息更新处理函数
function handleUserInfoUpdate() {
  console.log('🔔 HeaderNav: 接收到用户信息更新事件')
  // userInfo是响应式的，会自动更新，这里不需要手动操作
  // 重新加载搜索历史，因为用户登录状态可能已改变
  loadSearchHistory()
}
// 初始化主题
function initTheme() {
  const savedTheme = localStorage.getItem('theme') || 'pink'
  if (presetColors[savedTheme]) {
    selectPresetTheme(savedTheme, false) // 初始化时不显示消息
  } else {
    // 如果保存的主题不存在，使用默认粉色主题
    selectPresetTheme('pink', false) // 初始化时不显示消息
  }
}

// 滚动事件处理函数
function handleScroll() {
  // 如果搜索历史框正在显示，则在滚动时自动隐藏
  if (showSearchResults.value) {
    showSearchResults.value = false
  }
}

onMounted(() => {
  checkLogin()
  loadSearchHistory()
  initTheme() // 初始化主题
  // 监听用户信息更新事件
  window.addEventListener('user-info-updated', handleUserInfoUpdate)
  document.addEventListener('click', handleClickOutside)
  // 添加滚动事件监听器
  window.addEventListener('scroll', handleScroll)
})
onUnmounted(() => {
  window.removeEventListener('user-info-updated', handleUserInfoUpdate)
  document.removeEventListener('click', handleClickOutside)
  // 移除滚动事件监听器
  window.removeEventListener('scroll', handleScroll)
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
  // 在新窗口中打开开放平台页面
  const url = router.resolve({ path: '/open-platform', query: { popup: 'true' } }).href
  window.open(url, '_blank', 'noopener,noreferrer')
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
function selectPresetTheme(themeName, showMessage = true) {
  currentTheme.value = themeName
  localStorage.setItem('theme', themeName)
  
  // 获取主题信息
  const theme = presetColors[themeName]
  const root = document.documentElement
  
  // 重要：只设置data-theme属性，让App.vue中定义的CSS变量通过data-theme选择器生效
  // 这样可以确保所有主题变量的一致性
  root.setAttribute('data-theme', themeName)
  
  // 清除可能存在的内联CSS变量，避免覆盖App.vue中的主题定义
  root.style.removeProperty('--primary')
  root.style.removeProperty('--background')
  root.style.removeProperty('--background-card')
  root.style.removeProperty('--text-primary')
  root.style.removeProperty('--text-secondary')
  root.style.removeProperty('--primary-light')
  root.style.removeProperty('--primary-dark')
  root.style.removeProperty('--background-light')
  root.style.removeProperty('--border')
  
  // 清除body上可能存在的内联背景色，让CSS变量生效
  document.body.style.removeProperty('background-color')
  
  // 触发主题变化事件，通知其他组件
  window.dispatchEvent(new CustomEvent('theme-changed', { detail: { theme: themeName, colors: theme } }))
  
  // 只有在用户主动切换时才显示消息
  if (showMessage) {
    ElMessage.success(`已切换到${theme.name}主题`)
    showColorPicker.value = false
  }
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
  // 聚焦时，只有当搜索历史不为空时才显示搜索下拉框
  if (searchHistory.value.length > 0) {
    showSearchResults.value = true
  }
  if (searchQuery.value.trim() === '') {
    // 空搜索时显示历史
    searchResults.value = []
  }
}

async function handleSearchInput() {
  const keyword = searchQuery.value.trim()
  
  // 清除之前的搜索定时器
  if (searchTimer.value) {
    clearTimeout(searchTimer.value)
    searchTimer.value = null
  }
  
  if (keyword === '') {
    // 空搜索时，只有当搜索历史不为空时才显示搜索下拉框
    showSearchResults.value = searchHistory.value.length > 0
    searchResults.value = []
    return
  }
  
  // 使用防抖技术进行实时搜索
  showSearchResults.value = true
  isSearching.value = true
  
  // 设置新的搜索定时器，延迟300ms执行搜索建议
  searchTimer.value = setTimeout(async () => {
    try {
      // 调用搜索建议API获取实时搜索数据
      const response = await smartSuggest(keyword)
      
      if (response && response.code === 200) {
        searchResults.value = response.data || []
      } else {
        console.warn('搜索建议API返回异常状态:', response?.code, response?.message)
        searchResults.value = []
      }
    } catch (error) {
      console.error('获取搜索建议失败:', error)
      searchResults.value = []
    } finally {
      isSearching.value = false
    }
  }, 300)
}

async function performSearch(keyword) {
  if (!keyword) return
  
  try {
    isSearching.value = true
    showSearchResults.value = true
    
    const userId = getCurrentUserId()
    
    // 调用真实搜索API
    const response = await searchAll(keyword, 0, 20, userId)
    
    // 处理搜索结果数据
    const results = []
    
    if (response && response.code === 200) {
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
    } else {
      console.warn('搜索API返回异常状态:', response?.code, response?.message)
    }
    
    searchResults.value = results
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
  } finally {
    isSearching.value = false
  }
}



async function handleSearch() {
  const keyword = searchQuery.value.trim()
  if (!keyword) return
  
  try {
    // 保存搜索历史
    await saveToSearchHistory(keyword)
    
    // 执行搜索并显示结果
    await performSearch(keyword)
    
    ElMessage.success(`搜索"${keyword}"完成`)
  } catch (error) {
    console.error('搜索过程中发生错误:', error)
    ElMessage.error('搜索失败，请重试')
  }
}

async function selectSearchResult(result) {
  // 如果是搜索建议，需要先将建议内容设置为搜索关键词，然后执行搜索
  if (result.type === 'suggestion') {
    searchQuery.value = result.name
    await handleSearch()
    return
  }
  
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


function clearSearch() {
  searchQuery.value = ''
  searchResults.value = []
  showSearchResults.value = false
}

async function deleteHistoryItem(keyword) {
  try {
    const userId = getCurrentUserId()
    console.log('准备删除搜索历史:', { keyword, userId })
    
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    const response = await deleteSearchHistoryItem(keyword, userId)
    console.log('删除搜索历史API响应:', response)
    
    if (response && response.code === 200) {
      // API调用成功后，重新从数据库加载搜索历史
      await loadSearchHistory()
      ElMessage.success('搜索历史项已删除')
    } else {
      ElMessage.error(response?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除搜索历史项失败:', error)
    ElMessage.error('删除失败，请重试')
  }
}

async function handleClearHistory() {
  try {
    const userId = getCurrentUserId()
    console.log('准备清空搜索历史:', { userId })
    
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    const response = await clearSearchHistory(userId)
    console.log('清空搜索历史API响应:', response)
    
    if (response && response.code === 200) {
      // API调用成功后，重新从数据库加载搜索历史（应该是空的）
      await loadSearchHistory()
      ElMessage.success('搜索历史已清空')
    } else {
      ElMessage.error(response?.message || '清空失败')
    }
  } catch (error) {
    console.error('清空搜索历史失败:', error)
    ElMessage.error('清空失败，请重试')
  }
}

async function saveToSearchHistory(keyword) {
  if (!keyword || !keyword.trim()) return
  
  try {
    const userId = getCurrentUserId()
    console.log('保存搜索历史 - 用户ID:', userId)
    
    if (!userId) {
      console.log('用户未登录，跳过搜索历史保存')
      return
    }
    
    console.log('保存搜索历史到数据库:', { keyword, userId })
    const response = await saveSearchHistory(keyword, userId)
    console.log('保存搜索历史API响应:', response)
    
    if (response && response.code === 200) {
      console.log('搜索历史保存成功，重新加载搜索历史')
      // 保存成功后重新从数据库加载搜索历史
      await loadSearchHistory()
    } else {
      console.warn('保存搜索历史API返回异常状态:', response?.code, response?.message)
      // 即使API返回异常，仍尝试重新加载搜索历史
      await loadSearchHistory()
    }
  } catch (error) {
    console.error('保存搜索历史失败:', error)
    console.error('错误详情:', error.message)
    console.error('错误堆栈:', error.stack)
    
    // 保存失败时仍尝试重新加载搜索历史，确保显示最新数据
    try {
      await loadSearchHistory()
    } catch (reloadError) {
      console.error('重新加载搜索历史也失败:', reloadError)
    }
  }
}

// 加载搜索历史
async function loadSearchHistory() {
  try {
    const userId = getCurrentUserId()
    console.log('加载搜索历史 - 用户ID:', userId)
    
    if (!userId) {
      // 未登录用户，清空搜索历史
      searchHistory.value = []
      console.log('用户未登录，清空搜索历史')
      return
    }
    
    console.log('从数据库加载搜索历史，userId:', userId)
    const response = await getSearchHistory(userId)
    console.log('搜索历史API响应完整数据:', response)
    
    if (response && response.code === 200) {
      console.log('API调用成功，原始数据:', response.data)
      
      // 增强数据处理逻辑，适配多种可能的数据格式
      if (Array.isArray(response.data)) {
        // 处理数组类型数据
        searchHistory.value = response.data.map(item => {
          // 处理不同格式的item
          if (typeof item === 'string') {
            return item
          } else if (typeof item === 'object' && item) {
            return item.keyword || item.content || item.name || JSON.stringify(item)
          }
          return ''
        }).filter(keyword => keyword && keyword.trim()) // 过滤空值和空格
        
        console.log('处理后的搜索历史:', searchHistory.value)
      } else {
        // 如果不是数组，尝试转换为数组或使用空数组
        console.warn('搜索历史数据不是数组，类型为:', typeof response.data)
        searchHistory.value = []
      }
    } else {
      console.warn('搜索历史API响应异常:', response?.code, response?.message)
      searchHistory.value = []
    }
  } catch (error) {
    console.error('加载搜索历史失败:', error)
    console.error('错误详情:', error.message)
    console.error('错误堆栈:', error.stack)
    searchHistory.value = []
  }
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
  // 在新窗口中打开0717页面（无导航栏版本）
  const url = router.resolve({ path: '/0717', query: { popup: 'true' } }).href
  window.open(url, '_blank', 'noopener,noreferrer')
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
  background: var(--background-card);
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
  background: var(--background-light);
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

.color-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.color-item {
  height: 80px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
  padding: 8px;
  overflow: hidden;
}

.color-item:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.color-item.active {
  border-color: #31c27c;
  box-shadow: 0 0 0 3px rgba(49, 194, 124, 0.3);
  transform: scale(1.02);
}

.color-preview {
  width: 100%;
  height: 40px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.color-text {
  font-weight: bold;
  font-size: 14px;
}

.color-name {
  color: white;
  font-weight: 600;
  font-size: 12px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.7);
  text-align: center;
}

.theme-tip {
  text-align: center;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 8px;
  margin-top: 16px;
}

.theme-tip p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.5;
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

/* 搜索图标按钮样式 */
.search-icon-button {
  position: absolute;
  right: 0;
  top: 0;
  height: 100%;
  width: 38px;
  background: none;
  border: none;
  border-radius: 0 20px 20px 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
  padding: 0;
}

.search-icon-button:hover {
  background: rgba(0, 0, 0, 0.05);
}

.search-icon-button:focus {
  outline: none;
  background: rgba(0, 0, 0, 0.08);
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

.clear-search-btn {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 16px;
  color: #9ca3af;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}

.clear-search-btn:hover {
  background: #f3f4f6;
  color: #6b7280;
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
  padding: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-item {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 12px;
  transition: all 0.2s ease;
  border-radius: 16px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  white-space: nowrap;
  max-width: 140px;
  position: relative;
}

.history-item:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.history-text {
  font-size: 12px;
  color: #475569;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  flex: 1;
  margin-right: 4px;
}

.history-delete-btn {
  background: none;
  border: none;
  color: #9ca3af;
  font-size: 12px;
  cursor: pointer;
  padding: 2px 4px;
  border-radius: 50%;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.history-delete-btn:hover {
  background: #ef4444;
  color: white;
  transform: scale(1.1);
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
  background: var(--background-card);
  border-color: #fff;
  color: #fff;
}

/* 黑色主题下的登录按钮悬停样式 */
[data-theme="black"] .login-nav-btn:hover {
  background: var(--background-hover);
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