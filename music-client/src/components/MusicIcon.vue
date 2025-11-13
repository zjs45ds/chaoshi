// 音乐图标组件
<template>
  <span class="music-icon" :class="iconClass">
    <template v-if="iconConfig.type === 'emoji'">{{ iconConfig.content }}</template>
    <template v-else-if="iconConfig.type === 'svg'" v-html="iconConfig.content"></template>
    <template v-else-if="iconConfig.type === 'image'">
      <img :src="iconConfig.content" :alt="props.name" class="icon-image" />
    </template>
    <template v-else-if="iconConfig.type === 'class'">
      <i :class="iconConfig.content"></i>
    </template>
    <template v-else>{{ iconConfig.content }}</template>
  </span>
</template>

<script setup>
import { computed } from 'vue'

// ==================== Props ====================

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  size: {
    type: String,
    default: 'medium' // small, medium, large
  }
})

// ==================== 图标配置系统 ====================

/**
 * 图标配置 - 使用本地SVG图标文件
 * 支持多种图标类型：image（图片路径）、emoji、SVG字符串等
 */
const iconConfigs = {
  // 播放控制图标
  play: { type: 'image', content: '/src/assets/star.svg' },
  pause: { type: 'image', content: '/src/assets/pause.svg' },
  loading: { type: 'image', content: '/src/assets/loading.svg' },
  skipBack: { type: 'image', content: '/src/assets/left.svg' },
  skipForward: { type: 'image', content: '/src/assets/right.svg' },
  
  // 播放模式图标
  repeat: { type: 'image', content: '/src/assets/循环播放.svg' },
  repeatOne: { type: 'image', content: '/src/assets/顺序播放.svg' },
  shuffle: { type: 'image', content: '/src/assets/随机播放.svg' },
  
  // 音量控制图标
  volumeHigh: { type: 'image', content: '/src/assets/sound.svg' },
  volumeLow: { type: 'image', content: '/src/assets/sound.svg' },
  volumeMute: { type: 'emoji', content: '🔇' },
  
  // 功能图标
  list: { type: 'image', content: '/src/assets/播放列表.svg' },
  more: { type: 'emoji', content: '⋯' },
  close: { type: 'emoji', content: '✕' },
  arrowDown: { type: 'emoji', content: '↓' },
  
  // 播放列表图标
  heart: { type: 'emoji', content: '♡' },
  heartFilled: { type: 'emoji', content: '♥' },
  remove: { type: 'emoji', content: '➖' },
  trash: { type: 'emoji', content: '🗑' },
  time: { type: 'emoji', content: '⏱' },
  musicEmpty: { type: 'emoji', content: '🎵' }
}

// ==================== 计算属性 ====================

/**
 * 获取图标配置
 */
const iconConfig = computed(() => {
  return iconConfigs[props.name] || { type: 'emoji', content: '?' }
})

/**
 * 获取图标样式类
 */
const iconClass = computed(() => {
  const classes = [`icon-${props.name}`, `icon-size-${props.size}`]
  
  if (iconConfig.value.type === 'class') {
    classes.push(iconConfig.value.content)
  }
  
  return classes
})
</script>

<style scoped>
.music-icon {
  display: inline-block;
  font-style: normal;
  font-weight: normal;
  font-variant: normal;
  text-transform: none;
  line-height: 1;
  vertical-align: middle;
  opacity: 1 !important; /* 确保图标完全不透明 */
}

/* 图标图片样式 */
.icon-image {
  width: 1em;
  height: 1em;
  display: inline-block;
  vertical-align: middle;
  object-fit: contain;
  opacity: 1 !important; /* 确保图片完全不透明 */
}

/* 图标尺寸 */
.icon-size-small {
  font-size: 16px;
}

.icon-size-medium {
  font-size: 20px;
}

.icon-size-large {
  font-size: 24px;
}

.icon-size-xlarge {
  font-size: 28px;
}

/* 特定图标的调整 */
.icon-loading {
  animation: iconSpin 1s linear infinite;
}

@keyframes iconSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 颜色适配 */
.music-icon {
  color: inherit;
  opacity: 1 !important; /* 强制不透明 */
}
</style>