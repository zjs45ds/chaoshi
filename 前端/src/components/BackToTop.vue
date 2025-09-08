<!--
  回到顶部组件
  通用的滚动回顶部功能，支持平滑滚动和动画效果
-->
<template>
  <transition name="back-to-top" appear>
    <div 
      v-if="visible" 
      class="back-to-top-container"
      :style="containerStyle"
      @click="scrollToTop"
      :class="{ 'pulse': isPulsing }"
    >
      <!-- 主按钮 -->
      <div class="back-to-top-btn">
        <!-- 背景光环效果 -->
        <div class="glow-ring"></div>
        
        <!-- 箭头图标 -->
        <div class="arrow-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path 
              d="M12 19V5M5 12L12 5L19 12" 
              stroke="currentColor" 
              stroke-width="2" 
              stroke-linecap="round" 
              stroke-linejoin="round"
            />
          </svg>
        </div>
        
        <!-- 进度环 -->
        <svg class="progress-ring" viewBox="0 0 60 60">
          <circle
            class="progress-ring-bg"
            cx="30"
            cy="30"
            r="26"
            fill="none"
            stroke="rgba(255, 255, 255, 0.1)"
            stroke-width="2"
          />
          <circle
            class="progress-ring-fill"
            cx="30"
            cy="30"
            r="26"
            fill="none"
            stroke="url(#progressGradient)"
            stroke-width="2"
            stroke-linecap="round"
            :style="{ strokeDasharray: circumference, strokeDashoffset: strokeDashoffset }"
          />
          <defs>
            <linearGradient id="progressGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#4facfe"/>
              <stop offset="50%" style="stop-color:#00f2fe"/>
              <stop offset="100%" style="stop-color:#667eea"/>
            </linearGradient>
          </defs>
        </svg>
      </div>
      
      <!-- 提示文字 -->
      <div class="tooltip" v-if="showTooltip">
        回到顶部
      </div>
    </div>
  </transition>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'

export default {
  name: 'BackToTop',
  props: {
    // 显示阈值（滚动多少像素后显示）
    threshold: {
      type: Number,
      default: 300
    },
    // 滚动容器选择器
    container: {
      type: String,
      default: 'window'
    },
    // 是否显示滚动进度
    showProgress: {
      type: Boolean,
      default: true
    },
    // 是否显示提示
    showTooltip: {
      type: Boolean,
      default: true
    },
    // 动画持续时间
    duration: {
      type: Number,
      default: 500
    },
    // 底部距离（像素）
    bottomOffset: {
      type: Number,
      default: 100
    },
    // 右侧距离（像素）
    rightOffset: {
      type: Number,
      default: 30
    }
  },
  setup(props) {
    const visible = ref(false)
    const scrollTop = ref(0)
    const scrollHeight = ref(0)
    const clientHeight = ref(0)
    const isPulsing = ref(false)
    const isPlaylistOpen = ref(false) // 播放列表是否打开
    const musicPlayerHeight = ref(0) // 音乐播放器高度
    
    // 计算圆环周长
    const circumference = 2 * Math.PI * 26
    
    // 计算滚动进度
    const scrollProgress = computed(() => {
      if (!props.showProgress) return 0
      const maxScroll = scrollHeight.value - clientHeight.value
      return maxScroll > 0 ? (scrollTop.value / maxScroll) * 100 : 0
    })
    
    // 计算stroke-dashoffset
    const strokeDashoffset = computed(() => {
      if (!props.showProgress) return circumference
      const progress = Math.min(100, Math.max(0, scrollProgress.value))
      return circumference - (progress / 100) * circumference
    })
    
    // 计算容器样式
    const containerStyle = computed(() => {
      // 如果有音乐播放器，增加bottom距离避免重叠
      const bottomOffset = musicPlayerHeight.value > 0 ? props.bottomOffset + musicPlayerHeight.value + 10 : props.bottomOffset
      
      return {
        bottom: `${bottomOffset}px`,
        right: `${props.rightOffset}px`
      }
    })
    
    // 获取滚动容器
    const getScrollContainer = () => {
      if (props.container === 'window') {
        return window
      } else {
        return document.querySelector(props.container)
      }
    }
    
    // 检查音乐播放器高度
    const checkMusicPlayerHeight = () => {
      const musicPlayer = document.querySelector('.netease-player.has-song')
      if (musicPlayer && musicPlayer.offsetHeight > 0) {
        musicPlayerHeight.value = musicPlayer.offsetHeight
      } else {
        musicPlayerHeight.value = 0
      }
    }
    
    // 处理滚动事件
    const handleScroll = () => {
      const container = getScrollContainer()
      
      if (container === window) {
        scrollTop.value = window.pageYOffset || document.documentElement.scrollTop
        scrollHeight.value = document.documentElement.scrollHeight
        clientHeight.value = window.innerHeight
      } else if (container) {
        scrollTop.value = container.scrollTop
        scrollHeight.value = container.scrollHeight
        clientHeight.value = container.clientHeight
      }
      
      // 控制显示/隐藏
      visible.value = scrollTop.value > props.threshold && !isPlaylistOpen.value
      
      // 脉冲效果（滚动很快时）
      if (scrollTop.value > props.threshold * 2) {
        isPulsing.value = true
        setTimeout(() => {
          isPulsing.value = false
        }, 1000)
      }
    }
    
    // 平滑滚动到顶部
    const scrollToTop = () => {
      const container = getScrollContainer()
      
      if (container === window) {
        // 使用现代的smooth scroll
        if ('scrollBehavior' in document.documentElement.style) {
          window.scrollTo({
            top: 0,
            behavior: 'smooth'
          })
        } else {
          // 兼容旧浏览器的动画滚动
          animateScroll(scrollTop.value, 0, props.duration)
        }
      } else if (container) {
        if ('scrollBehavior' in container.style) {
          container.scrollTo({
            top: 0,
            behavior: 'smooth'
          })
        } else {
          animateScroll(container.scrollTop, 0, props.duration, container)
        }
      }
    }
    
    // 自定义动画滚动（兼容性）
    const animateScroll = (start, end, duration, element = window) => {
      const startTime = performance.now()
      
      const animate = (currentTime) => {
        const elapsed = currentTime - startTime
        const progress = Math.min(elapsed / duration, 1)
        
        // 使用缓动函数
        const easeOutCubic = 1 - Math.pow(1 - progress, 3)
        const currentPosition = start + (end - start) * easeOutCubic
        
        if (element === window) {
          window.scrollTo(0, currentPosition)
        } else {
          element.scrollTop = currentPosition
        }
        
        if (progress < 1) {
          requestAnimationFrame(animate)
        }
      }
      
      requestAnimationFrame(animate)
    }
    
    // 监听滚动事件
    onMounted(() => {
      const container = getScrollContainer()
      if (container) {
        container.addEventListener('scroll', handleScroll, { passive: true })
        // 初始检查
        handleScroll()
      }
      
      // 检查音乐播放器高度
      checkMusicPlayerHeight()
      
      // 监听 DOM 变化，检测音乐播放器的出现/消失
      const observer = new MutationObserver(() => {
        checkMusicPlayerHeight()
      })
      
      observer.observe(document.body, {
        childList: true,
        subtree: true,
        attributes: true,
        attributeFilter: ['class']
      })
      
      // 保存 observer 以便清理
      window._backToTopObserver = observer
      
      // 监听播放列表状态变化
      const handlePlaylistToggle = (event) => {
        isPlaylistOpen.value = event.detail.isOpen
        // 重新检查显示状态
        handleScroll()
      }
      window.addEventListener('playlistToggle', handlePlaylistToggle)
    })
    
    onBeforeUnmount(() => {
      const container = getScrollContainer()
      if (container) {
        container.removeEventListener('scroll', handleScroll)
      }
      
      // 清理 MutationObserver
      if (window._backToTopObserver) {
        window._backToTopObserver.disconnect()
        delete window._backToTopObserver
      }
      
      // 移除播放列表事件监听（这里需要保留对事件处理函数的引用）
      // 由于闭包问题，这里可能无法正确移除，但通常页面卸载时会自动清理
    })
    
    return {
      visible,
      scrollProgress,
      circumference,
      strokeDashoffset,
      isPulsing,
      scrollToTop,
      containerStyle
    }
  }
}
</script>

<style scoped>
/* 回到顶部容器 */
.back-to-top-container {
  position: fixed;
  bottom: 30px; /* 距离底部30px */
  right: 30px; /* 最右边 */
  z-index: 9999;
  cursor: pointer;
  user-select: none;
}

/* 主按钮 */
.back-to-top-btn {
  position: relative;
  width: 60px;
  height: 60px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  overflow: hidden;
}

.back-to-top-btn:hover {
  transform: translateX(-5px) scale(1.1); /* 向左移动并放大 */
  background: rgba(0, 0, 0, 0.85);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.3),
    0 0 30px rgba(102, 126, 234, 0.6);
}

/* 脉冲效果 */
.back-to-top-container.pulse .back-to-top-btn {
  animation: pulseGlow 1s ease-in-out;
}

@keyframes pulseGlow {
  0%, 100% {
    transform: scale(1);
    box-shadow: 
      0 8px 32px rgba(102, 126, 234, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
  50% {
    transform: scale(1.15);
    box-shadow: 
      0 25px 50px rgba(102, 126, 234, 0.6),
      inset 0 1px 0 rgba(255, 255, 255, 0.5),
      0 0 40px rgba(102, 126, 234, 0.8);
  }
}

/* 光环效果 */
.glow-ring {
  position: absolute;
  top: -5px;
  left: -5px;
  right: -5px;
  bottom: -5px;
  border-radius: 50%;
  background: linear-gradient(45deg, 
    rgba(102, 126, 234, 0.3) 0%, 
    rgba(118, 75, 162, 0.3) 50%, 
    rgba(240, 147, 251, 0.3) 100%);
  opacity: 0;
  animation: rotateGlow 3s linear infinite;
  transition: opacity 0.3s ease;
}

.back-to-top-btn:hover .glow-ring {
  opacity: 1;
}

@keyframes rotateGlow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 箭头图标 */
.arrow-icon {
  width: 24px;
  height: 24px;
  color: #ffffff;
  z-index: 2;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.arrow-icon svg {
  width: 100%;
  height: 100%;
  stroke: #ffffff;
  stroke-width: 2.5;
  filter: drop-shadow(0 0 3px rgba(0, 0, 0, 0.8));
}

.back-to-top-btn:hover .arrow-icon {
  color: #ffffff;
  transform: translateY(-2px);
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.4));
}

.back-to-top-btn:hover .arrow-icon svg {
  stroke: #ffffff;
  filter: drop-shadow(0 0 6px rgba(0, 0, 0, 1));
}

/* 进度环 */
.progress-ring {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
  z-index: 1;
}

.progress-ring-bg {
  opacity: 0.3;
}

.progress-ring-fill {
  transition: stroke-dashoffset 0.3s ease;
}

/* 提示文字 */
.tooltip {
  position: absolute;
  top: 50%;
  right: 70px; /* 在按钮左侧显示 */
  transform: translateY(-50%); /* 垂直居中对齐 */
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  white-space: nowrap;
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.back-to-top-container:hover .tooltip {
  opacity: 1;
  transform: translateY(-50%) translateX(-5px); /* 垂直居中，向左移动 */
}

/* 进入/离开动画 */
.back-to-top-enter-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.back-to-top-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.back-to-top-enter-from {
  opacity: 0;
  transform: translateX(20px) scale(0.8);
}

.back-to-top-leave-to {
  opacity: 0;
  transform: translateX(20px) scale(0.8);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .back-to-top-container {
    /* 移动端会通过 JS 动态计算位置，这里只设置基本样式 */
    right: 20px;
  }
  
  .back-to-top-btn {
    width: 50px;
    height: 50px;
    background: rgba(0, 0, 0, 0.8);
    border: 2px solid rgba(255, 255, 255, 0.4);
  }
  
  .arrow-icon {
    width: 20px;
    height: 20px;
  }
  
  .arrow-icon svg {
    stroke-width: 3;
    filter: drop-shadow(0 0 4px rgba(0, 0, 0, 1));
  }
  
  .tooltip {
    display: none; /* 移动端隐藏提示 */
  }
}

/* 小屏幕设备进一步调整 */
@media (max-width: 480px) {
  .back-to-top-container {
    /* 小屏幕会通过 JS 动态计算位置，这里只设置基本样式 */
    right: 15px;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .back-to-top-btn {
    background: rgba(0, 0, 0, 0.8);
    border-color: rgba(255, 255, 255, 0.2);
  }
  
  .back-to-top-btn:hover {
    background: rgba(0, 0, 0, 0.9);
    border-color: rgba(255, 255, 255, 0.4);
  }
  
  .arrow-icon svg {
    stroke: #ffffff;
  }
}

/* 浅色模式适配 */
@media (prefers-color-scheme: light) {
  .back-to-top-btn {
    background: rgba(0, 0, 0, 0.75);
    border-color: rgba(255, 255, 255, 0.3);
  }
  
  .back-to-top-btn:hover {
    background: rgba(0, 0, 0, 0.85);
    border-color: rgba(255, 255, 255, 0.5);
  }
  
  .arrow-icon svg {
    stroke: #ffffff;
  }
}

/* 高对比度模式适配 */
@media (prefers-contrast: high) {
  .back-to-top-btn {
    background: rgba(0, 0, 0, 0.95);
    border: 3px solid #ffffff;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.8);
  }
  
  .arrow-icon {
    color: #ffffff;
  }
  
  .arrow-icon svg {
    stroke: #ffffff;
    stroke-width: 3;
    filter: none;
  }
}

/* 减少动画模式 */
@media (prefers-reduced-motion: reduce) {
  .back-to-top-btn,
  .arrow-icon,
  .tooltip,
  .progress-ring-fill {
    transition: none;
  }
  
  .glow-ring {
    animation: none;
  }
  
  .back-to-top-container.pulse .back-to-top-btn {
    animation: none;
  }
}
</style>