// 通用轮播组件 
<template>
  <div class="universal-carousel">
    <!-- 左侧切换按钮 -->
    <button 
      v-if="showNavigation && totalPages > 1"
      class="carousel-btn carousel-btn-left" 
      @click="prevPage"
    >
      <i class="carousel-icon-left"></i>
    </button>
    
    <!-- 轮播内容 -->
    <div class="carousel-container">
      <div class="carousel-content">
        <slot :items="currentPageItems" :currentPage="currentPage" />
      </div>
    </div>
    
    <!-- 右侧切换按钮 -->
    <button 
      v-if="showNavigation && totalPages > 1"
      class="carousel-btn carousel-btn-right" 
      @click="nextPage"
    >
      <i class="carousel-icon-right"></i>
    </button>
    
    <!-- 分页指示器 -->
    <div v-if="showDots && totalPages > 1" class="carousel-dots">
      <span
        v-for="(dot, idx) in totalPages"
        :key="idx"
        :class="['dot', { active: idx === currentPage }]"
        @click="goToPage(idx)"
      ></span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useResponsive } from '@/utils/responsive.js'

const props = defineProps({
  items: { type: Array, default: () => [] },
  pageSize: { type: Number, default: 0 }, // 0表示使用响应式大小
  showNavigation: { type: Boolean, default: true },
  showDots: { type: Boolean, default: true }
})

const emit = defineEmits(['pageChange'])

const { cardsPerRow } = useResponsive()
const currentPage = ref(0)
const animating = ref(false)

// 计算页面大小
const currentPageSize = computed(() => 
  props.pageSize || cardsPerRow.value
)

// 计算总页数
const totalPages = computed(() => 
  Math.ceil((props.items?.length || 0) / currentPageSize.value)
)

// 当前页数据
const currentPageItems = computed(() => {
  const start = currentPage.value * currentPageSize.value
  return (props.items || []).slice(start, start + currentPageSize.value)
})

// 分页方法
function prevPage() {
  if (animating.value || currentPage.value <= 0) return
  goToPage(currentPage.value - 1)
}

function nextPage() {
  if (animating.value || currentPage.value >= totalPages.value - 1) return
  goToPage(currentPage.value + 1)
}

function goToPage(pageIndex) {
  if (animating.value || pageIndex === currentPage.value) return
  if (pageIndex < 0 || pageIndex >= totalPages.value) return
  
  animating.value = true
  currentPage.value = pageIndex
  
  emit('pageChange', { currentPage: currentPage.value, totalPages: totalPages.value })
  
  setTimeout(() => {
    animating.value = false
  }, 300)
}

defineExpose({ prevPage, nextPage, goToPage })
</script>

<style scoped>
.universal-carousel {
  position: relative;
  width: 100%;
  padding: 36px 0;
}

.carousel-container {
  width: 100%;
  overflow: visible;
}

.carousel-content {
  display: flex;
  gap: 24px;
  flex-wrap: nowrap;
  overflow-x: auto;
  scrollbar-width: none;
  -webkit-overflow-scrolling: touch;
}

.carousel-content::-webkit-scrollbar {
  display: none;
}

.carousel-btn {
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
  opacity: 0.8;
}

.carousel-btn:hover {
  transform: translateY(-50%) scale(1.1);
  opacity: 1;
}

.carousel-btn-left {
  left: clamp(-150px, -10vw, -80px);
}

.carousel-btn-right {
  right: clamp(-150px, -10vw, -80px);
}

.carousel-icon-left,
.carousel-icon-right {
  width: 50px;
  height: 50px;
  display: inline-block;
}

.carousel-icon-left {
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="%23666" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.carousel-icon-right {
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="%23666" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18l6-6-6-6"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.carousel-dots {
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  cursor: pointer;
}

.dot:hover {
  background: rgba(0, 0, 0, 0.5);
  transform: scale(1.3);
}

.dot.active {
  background: rgba(0, 0, 0, 0.7);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .carousel-btn {
    width: 60px;
    height: 60px;
  }
  
  .carousel-icon-left,
  .carousel-icon-right {
    width: 35px;
    height: 35px;
  }
  
  .carousel-content {
    gap: 16px;
  }
}
</style>