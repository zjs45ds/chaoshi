// 全屏播放页面
<template>
  <div class="modern-player">
    <!-- 动态背景 -->
    <div class="background-layer">
      <!-- 流动的颜色渐变 -->
      <div class="color-flow"></div>
      <!-- 模糊圆形装饰 -->
      <div class="floating-orbs">
        <div v-for="i in 6" :key="i" class="orb" :style="getOrbStyle(i)"></div>
      </div>
      <!-- 细微的网格 -->
      <div class="subtle-grid"></div>
    </div>

    <!-- 主容器 -->
    <div class="player-content">
      <!-- 专辑封面区域 -->
      <div class="album-section">
        <div class="album-container">
          <!-- 播放按钮悬浮层 -->
          <div class="play-overlay" @click="togglePlay" v-if="!isPlaying">
            <div class="play-icon">
              <svg viewBox="0 0 24 24">
                <path d="M8 5v14l11-7z" fill="currentColor"/>
              </svg>
            </div>
          </div>
          
          <!-- 专辑封面 -->
          <div class="album-wrapper" :class="{ 'playing': isPlaying }">
            <img 
              :src="currentDisplaySong.cover || require('@/assets/1音乐.png')" 
              :alt="currentDisplaySong.name" 
              class="album-cover"
              @click="goHome"
            />
            <!-- vinyl唱片效果 -->
            <div class="vinyl-record" v-if="isPlaying">
              <div class="vinyl-center"></div>
              <div class="vinyl-groove" v-for="i in 8" :key="i"></div>
            </div>
          </div>
          
          <!-- 音频可视化 -->
          <div class="visualizer" v-if="isPlaying">
            <div v-for="i in 64" :key="i" class="vis-bar" :style="getVisualizerStyle(i)"></div>
          </div>
        </div>
      </div>
      
      <!-- 歌曲信息区域 -->
      <div class="song-info">
        <div class="info-card">
          <!-- 歌曲标题 -->
          <h1 class="song-title">{{ currentDisplaySong.name }}</h1>
          
          <!-- 艺术家信息 -->
          <p class="artist-name">{{ currentDisplaySong.artist }}</p>
          
          <!-- 专辑信息 -->
          <p class="album-name" v-if="currentDisplaySong.album">{{ currentDisplaySong.album }}</p>
          
          <!-- 进度信息 -->
          <div class="progress-info">
            <span class="duration">{{ formatDuration(currentDisplaySong.duration) }}</span>
            <span class="status" :class="{ active: isPlaying }">{{ isPlaying ? 'Playing' : 'Paused' }}</span>
          </div>
          
          <!-- 操作按钮 -->
          <div class="action-buttons">
            <button class="action-btn" @click="goToLyricPage">
              <svg viewBox="0 0 24 24">
                <path d="M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z"/>
              </svg>
              <span>歌词</span>
            </button>
            
            <button class="action-btn" @click="goHome">
              <svg viewBox="0 0 24 24">
                <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
              </svg>
              <span>主页</span>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 返回按钮 -->
    <button class="back-button" @click="goHome">
      <svg viewBox="0 0 24 24">
        <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.42-1.41L7.83 13H20v-2z"/>
      </svg>
    </button>
    
    <!-- 底部播放器 -->
    <div class="bottom-player">
    <!-- 播放器已移除 -->
    <!-- <MusicPlayerBar /> -->
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// 已移除播放器相关功能 - 本组件为录音播放器，不需要音乐播放功能

const router = useRouter()
const route = useRoute()

// 默认歌曲信息
const defaultSong = {
  id: 1,
  name: 'Beautiful Day',
  artist: 'Unknown Artist',
  album: 'Greatest Hits',
  duration: 210,
  cover: 'https://qpic.y.qq.com/music_cover/N6GhicG06jmQnia2FZRicpvhQXiaLPoEJcRlnjtIlFeBXTuPgsgdFwykWg/600?n=1'
}

// 当前显示的歌曲
const currentDisplaySong = computed(() => {
  return currentSong.value || defaultSong
})

// 动态时间变量
const animationTime = ref(0)
let animationFrame = null

// 生命周期管理
onMounted(() => {
  startAnimation()
})

onUnmounted(() => {
  stopAnimation()
})

// 动画循环
function startAnimation() {
  const animate = () => {
    animationTime.value += 0.01
    animationFrame = requestAnimationFrame(animate)
  }
  animationFrame = requestAnimationFrame(animate)
}

function stopAnimation() {
  if (animationFrame) {
    cancelAnimationFrame(animationFrame)
  }
}

// 圆形装饰样式
function getOrbStyle(index) {
  const angle = (index * 60) + (animationTime.value * 5)
  const radius = 30 + index * 10
  const x = 50 + Math.cos(angle * Math.PI / 180) * radius
  const y = 50 + Math.sin(angle * Math.PI / 180) * radius
  const size = 80 + Math.sin(animationTime.value + index) * 20
  
  return {
    left: Math.max(0, Math.min(100, x)) + '%',
    top: Math.max(0, Math.min(100, y)) + '%',
    width: size + 'px',
    height: size + 'px',
    animationDelay: index * 0.5 + 's'
  }
}

// 可视化样式
function getVisualizerStyle(index) {
  const height = 2 + Math.abs(Math.sin(animationTime.value * 3 + index * 0.2)) * 30
  const hue = (index * 5 + animationTime.value * 30) % 360
  
  return {
    height: height + 'px',
    backgroundColor: `hsl(${hue}, 70%, 60%)`,
    animationDelay: index * 0.02 + 's'
  }
}

// 播放切换函数
function togglePlay() {
  const newPlayingState = !isPlaying.value
  setPlaying(newPlayingState)
}

// 导航函数
function goToLyricPage() {
  if (route.path !== '/fullscreen-player') {
    router.push('/fullscreen-player')
  }
}

function goHome() {
  if (route.path !== '/') {
    router.push('/')
  }
}
</script>

<style scoped>
/* 基础容器 */
.modern-player {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 动态背景 */
.background-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.color-flow {
  position: absolute;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, 
    rgba(102, 126, 234, 0.3) 0%, 
    rgba(118, 75, 162, 0.3) 25%,
    rgba(255, 154, 158, 0.3) 50%,
    rgba(250, 208, 196, 0.3) 75%,
    rgba(102, 126, 234, 0.3) 100%);
  animation: flowMove 20s ease-in-out infinite;
  filter: blur(60px);
}

@keyframes flowMove {
  0%, 100% { transform: translate(-25%, -25%) rotate(0deg); }
  50% { transform: translate(-75%, -75%) rotate(180deg); }
}

.floating-orbs {
  position: absolute;
  width: 100%;
  height: 100%;
}

.orb {
  position: absolute;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 50%, transparent 100%);
  border-radius: 50%;
  filter: blur(2px);
  animation: orbFloat 15s ease-in-out infinite;
}

@keyframes orbFloat {
  0%, 100% { transform: translateY(0px) scale(1); }
  50% { transform: translateY(-20px) scale(1.1); }
}

.subtle-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  opacity: 0.5;
}

/* 主内容 */
.player-content {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 4rem;
  max-width: 1200px;
  padding: 2rem;
}

/* 专辑区域 */
.album-section {
  flex-shrink: 0;
}

.album-container {
  position: relative;
  width: 400px;
  height: 400px;
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  cursor: pointer;
  backdrop-filter: blur(10px);
  z-index: 5;
  transition: all 0.3s ease;
}

.play-overlay:hover {
  background: rgba(0, 0, 0, 0.3);
}

.play-icon {
  width: 80px;
  height: 80px;
  color: white;
  opacity: 0.9;
  transition: transform 0.3s ease;
}

.play-overlay:hover .play-icon {
  transform: scale(1.1);
}

.album-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  transition: transform 0.3s ease;
}

.album-wrapper.playing {
  animation: subtleRotate 20s linear infinite;
}

@keyframes subtleRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.album-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.album-cover:hover {
  transform: scale(1.05);
}

/* Vinyl唱片效果 */
.vinyl-record {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, 
    transparent 35%, 
    rgba(0, 0, 0, 0.1) 35%, 
    rgba(0, 0, 0, 0.1) 40%, 
    transparent 40%);
  animation: vinylRotate 2s linear infinite;
}

@keyframes vinylRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.vinyl-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 50%;
}

.vinyl-groove {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 50%;
}

.vinyl-groove:nth-child(2) { width: 60%; height: 60%; }
.vinyl-groove:nth-child(3) { width: 70%; height: 70%; }
.vinyl-groove:nth-child(4) { width: 80%; height: 80%; }
.vinyl-groove:nth-child(5) { width: 90%; height: 90%; }

/* 音频可视化 */
.visualizer {
  position: absolute;
  bottom: -50px;
  left: 50%;
  transform: translateX(-50%);
  width: 300px;
  height: 40px;
  display: flex;
  align-items: end;
  justify-content: center;
  gap: 2px;
}

.vis-bar {
  width: 3px;
  background: linear-gradient(to top, rgba(255,255,255,0.8), rgba(255,255,255,0.4));
  border-radius: 2px;
  animation: visualizerPulse 0.5s ease-in-out infinite alternate;
}

@keyframes visualizerPulse {
  0% { transform: scaleY(0.3); }
  100% { transform: scaleY(1); }
}

/* 歌曲信息 */
.song-info {
  flex: 1;
  max-width: 500px;
}

.info-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  padding: 3rem;
  text-align: center;
}

.song-title {
  font-size: 3rem;
  font-weight: 700;
  color: white;
  margin: 0 0 1rem 0;
  line-height: 1.2;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.artist-name {
  font-size: 1.5rem;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 0.5rem 0;
  font-weight: 500;
}

.album-name {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 2rem 0;
  font-weight: 400;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 2rem 0;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.duration {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.status {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  transition: color 0.3s ease;
}

.status.active {
  color: #4ade80;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
  font-weight: 500;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.action-btn svg {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

/* 返回按钮 */
.back-button {
  position: fixed;
  top: 2rem;
  left: 2rem;
  z-index: 20;
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.back-button svg {
  width: 24px;
  height: 24px;
  fill: currentColor;
}

/* 底部播放器 */
.bottom-player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 20;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .player-content {
    flex-direction: column;
    gap: 2rem;
    text-align: center;
  }
  
  .album-container {
    width: 300px;
    height: 300px;
  }
  
  .song-title {
    font-size: 2.5rem;
  }
}

@media (max-width: 768px) {
  .player-content {
    padding: 1rem;
  }
  
  .album-container {
    width: 250px;
    height: 250px;
  }
  
  .song-title {
    font-size: 2rem;
  }
  
  .info-card {
    padding: 2rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>