// 全屏播放页面 - 唱片风格
<template>
  <div class="record-player">
    <!-- 返回按钮 -->
    <button class="back-button" @click="goHome">
      <svg viewBox="0 0 24 24">
        <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.42-1.41L7.83 13H20v-2z" fill="currentColor"/>
      </svg>
    </button>

    <!-- 主内容区 -->
    <div class="player-container">
      <!-- 唱片区域 -->
      <div class="vinyl-section">
        <!-- 唱臂 -->
        <div class="tonearm" :class="{ 'playing': isPlaying }">
          <div class="tonearm-base"></div>
          <div class="tonearm-arm"></div>
          <div class="tonearm-head"></div>
        </div>

        <!-- 唱片外圈 -->
        <div class="vinyl-outer" :class="{ 'spinning': isPlaying }">
          <!-- 唱片本体 -->
          <div class="vinyl-disc">
            <!-- 专辑封面 -->
            <div class="album-art">
              <img 
                :src="currentDisplaySong.cover || require('@/assets/1音乐.png')" 
                :alt="currentDisplaySong.name"
                @click="togglePlay"
              />
            </div>
            
            <!-- 唱片纹理 -->
            <div class="vinyl-grooves">
              <div v-for="i in 12" :key="i" class="groove"></div>
            </div>
            
            <!-- 中心标签 -->
            <div class="vinyl-label"></div>
          </div>
        </div>
      </div>

      <!-- 歌曲信息 -->
      <div class="song-details">
        <h1 class="song-name">{{ currentDisplaySong.name }}</h1>
        <p class="artist-name">{{ currentDisplaySong.artist }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useMusicPlayerStore } from '@/utils/musicPlayer'

const router = useRouter()
const route = useRoute()
const musicPlayerStore = useMusicPlayerStore()

// 从播放器 store 获取状态
const { currentSong, isPlaying } = storeToRefs(musicPlayerStore)
const { setPlaying } = musicPlayerStore

// 默认歌曲信息
const defaultSong = {
  id: 1,
  name: '喜欢你',
  artist: '邓紫棋',
  album: '在音乐的时光',
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

// 时间格式化函数
function formatDuration(seconds) {
  if (!seconds || isNaN(seconds)) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
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
.record-player {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(180deg, #2a2a2a 0%, #1a1a1a 100%);
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

/* 主容器 */
.player-container {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 3rem;
  padding-top: 6rem;
}

/* 唱片区域 */
.vinyl-section {
  position: relative;
  width: 450px;
  height: 450px;
}

/* 唱臂 */
.tonearm {
  position: absolute;
  top: -50px;
  right: 50px;
  width: 200px;
  height: 200px;
  z-index: 20;
  transform-origin: 90% 10%;
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.tonearm.playing {
  transform: rotate(25deg);
}

.tonearm-base {
  position: absolute;
  top: 0;
  right: 20px;
  width: 30px;
  height: 30px;
  background: radial-gradient(circle, #666 0%, #333 100%);
  border-radius: 50%;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
}

.tonearm-arm {
  position: absolute;
  top: 15px;
  right: 35px;
  width: 150px;
  height: 8px;
  background: linear-gradient(90deg, #888 0%, #555 100%);
  border-radius: 4px;
  transform-origin: right center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

.tonearm-head {
  position: absolute;
  top: 10px;
  right: 180px;
  width: 20px;
  height: 18px;
  background: linear-gradient(135deg, #999 0%, #444 100%);
  border-radius: 50% 50% 50% 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.5);
}

/* 唱片外圈 */
.vinyl-outer {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 420px;
  height: 420px;
  background: radial-gradient(circle, #1a1a1a 0%, #0a0a0a 100%);
  border-radius: 50%;
  box-shadow: 
    0 0 0 8px #2a2a2a,
    0 0 0 12px #1a1a1a,
    0 20px 60px rgba(0, 0, 0, 0.8),
    inset 0 0 30px rgba(0, 0, 0, 0.5);
  transition: transform 0.3s ease;
}

.vinyl-outer.spinning {
  animation: vinylSpin 3s linear infinite;
}

@keyframes vinylSpin {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

/* 唱片本体 */
.vinyl-disc {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

/* 专辑封面 */
.album-art {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200px;
  height: 200px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 
    0 0 0 2px rgba(255, 255, 255, 0.1),
    0 8px 20px rgba(0, 0, 0, 0.6);
  cursor: pointer;
  transition: transform 0.3s ease;
}

.album-art:hover {
  transform: translate(-50%, -50%) scale(1.05);
}

.album-art img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 唱片纹理 */
.vinyl-grooves {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.groove {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(255, 255, 255, 0.03);
  border-radius: 50%;
}

.groove:nth-child(1) { width: 90%; height: 90%; }
.groove:nth-child(2) { width: 85%; height: 85%; }
.groove:nth-child(3) { width: 80%; height: 80%; }
.groove:nth-child(4) { width: 75%; height: 75%; }
.groove:nth-child(5) { width: 70%; height: 70%; }
.groove:nth-child(6) { width: 65%; height: 65%; }
.groove:nth-child(7) { width: 60%; height: 60%; }
.groove:nth-child(8) { width: 55%; height: 55%; }
.groove:nth-child(9) { width: 50%; height: 50%; }
.groove:nth-child(10) { width: 45%; height: 45%; }
.groove:nth-child(11) { width: 40%; height: 40%; }
.groove:nth-child(12) { width: 35%; height: 35%; }

/* 中心标签 */
.vinyl-label {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, #2a2a2a 0%, #1a1a1a 100%);
  border-radius: 50%;
  box-shadow: 
    0 0 0 2px rgba(255, 255, 255, 0.05),
    inset 0 2px 8px rgba(0, 0, 0, 0.8);
}

.vinyl-label::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 15px;
  height: 15px;
  background: #0a0a0a;
  border-radius: 50%;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.9);
}

/* 歌曲信息 */
.song-details {
  text-align: center;
  color: white;
  margin-top: 1rem;
}

.song-name {
  font-size: 2rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  color: #ffffff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.song-details .artist-name {
  font-size: 1.1rem;
  font-weight: 400;
  margin: 0;
  color: rgba(255, 255, 255, 0.6);
}

/* 返回按钮 */
.back-button {
  position: fixed;
  top: 2rem;
  left: 2rem;
  z-index: 30;
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
}

/* 响应式设计 */
@media (max-width: 768px) {
  .player-container {
    padding-top: 4rem;
  }
  
  .vinyl-section {
    width: 350px;
    height: 350px;
  }
  
  .vinyl-outer {
    width: 320px;
    height: 320px;
  }
  
  .album-art {
    width: 150px;
    height: 150px;
  }
  
  .tonearm {
    width: 150px;
    height: 150px;
    right: 30px;
  }
  
  .tonearm-arm {
    width: 110px;
  }
  
  .song-name {
    font-size: 1.8rem;
  }
  
  .song-details .artist-name {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .player-container {
    padding-top: 3rem;
  }
  
  .vinyl-section {
    width: 280px;
    height: 280px;
  }
  
  .vinyl-outer {
    width: 260px;
    height: 260px;
  }
  
  .album-art {
    width: 120px;
    height: 120px;
  }
  
  .tonearm {
    width: 120px;
    height: 120px;
    right: 20px;
  }
  
  .tonearm-arm {
    width: 90px;
  }
  
  .song-name {
    font-size: 1.5rem;
  }
  
  .song-details .artist-name {
    font-size: 0.9rem;
  }
}
</style>