// 播放列表轮播组件
<template>
  <UniversalCarousel 
    :items="playlists" 
    :page-size="5"
    @page-change="handlePageChange"
  >
    <template #default="{ items }">
      <div class="playlist-list">
        <PlaylistCard v-for="item in items" :key="item.id" :playlist="item" />
      </div>
    </template>
  </UniversalCarousel>
</template>

<script setup>
import PlaylistCard from './PlaylistCard.vue'
import UniversalCarousel from './UniversalCarousel.vue'
import playlists from '@/assets/mock/playlists.js'

// 页面变化回调
function handlePageChange(pageInfo) {
  // CONSOLE LOG REMOVED: console.log('播放列表轮播页面变化:', pageInfo)
}
</script>

<style scoped>
.playlist-list {
  display: flex;
  gap: 32px;
  overflow: hidden;
  width: 100%;
}
</style>
