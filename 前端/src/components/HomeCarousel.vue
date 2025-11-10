<template>
  <div class="carousel-container">
    <el-carousel
      :interval="3000"
      type="card"
      height="240px"
      indicator-position="none"
      arrow="always"
      class="custom-carousel"
    >
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <div class="carousel-item-wrapper">
          <img
            class="carousel-img"
            :src="item.src"
            :alt="`轮播图${index + 1}`"
          >
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElCarousel, ElCarouselItem } from 'element-plus'
import { getBanners } from '@/api/home.js'

const carouselItems = ref([])

// 从后端获取轮播图数据
const fetchBanners = async () => {
  try {
    const response = await getBanners()
    // 检查响应是否是对象并且包含data字段
    const banners = response && response.data ? response.data : []
    // 确保banners是数组
    if (Array.isArray(banners)) {
      carouselItems.value = banners.map(banner => ({ src: banner.imgUrl }))
    } else {
      console.warn('轮播图数据不是数组格式:', banners)
      carouselItems.value = []
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
    carouselItems.value = []
  }
}

onMounted(() => {
  fetchBanners()
})
</script>

<style scoped>
.carousel-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.custom-carousel {
  --el-carousel-card-width: 60% !important;
}

.carousel-item-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

/* 确保卡片式轮播的3D效果和自定义样式 */
:deep(.el-carousel__stage) {
  perspective: 2000px;
}

:deep(.el-carousel__item) {
  transition: transform 0.5s ease;
}

/* 精确控制前后幻灯片的样式 */
:deep(.el-carousel__item.is-prev) {
  transform: translateX(-150%) translateZ(-800px);
}

:deep(.el-carousel__item.is-next) {
  transform: translateX(50%) translateZ(-800px);
}

/* 修复可能的层级问题 */
:deep(.el-carousel__stage-item) {
  position: absolute;
}
</style>