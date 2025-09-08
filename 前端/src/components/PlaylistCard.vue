<template>
  <div class="playlist-card">
    <img :src="playlist.cover" :alt="playlist.title" class="cover" />
    <div class="title">{{ playlist.title }}</div>
    <div class="playcount">播放量：{{ playlist.playCount }}</div>
  </div>
</template>

<script setup>
const props = defineProps({
  playlist: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.playlist-card {
  width: 200px;
  background: var(--background-card);
  border-radius: var(--border-radius);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  overflow: hidden;
  text-align: left;
  transition: box-shadow var(--transition-normal), transform var(--transition-normal);
  cursor: pointer;
}

.playlist-card:hover {
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.2);
  transform: translateY(-4px) scale(1.03);
}

.cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}

.title {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin: 12px 0 4px 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.playcount {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin-left: 12px;
  margin-bottom: 12px;
}
</style>
