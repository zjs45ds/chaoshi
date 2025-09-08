<template>
  <div class="user-page">
    <h2>用户中心</h2>
    <div class="user-list">
      <div v-for="user in users" :key="user.id" class="user-item">
        <img :src="user.avatar" :alt="user.name" class="user-avatar" />
        <div class="user-info">
          <div class="user-name">{{ user.name }}</div>
          <div class="user-desc">{{ user.desc }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import users from '@/assets/mock/users.js'
</script>

<style scoped>
.user-page {
  width: 1280px;
  margin: 0 auto;
  min-height: 600px;
  background: var(--background-card);
}
.user-list {
  margin-top: 32px;
  display: flex;
  gap: 32px;
}
.user-item {
  width: 220px;
  background: var(--background-light);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
}
.user-item:hover {
  box-shadow: 0 8px 24px rgba(34,197,94,0.12);
  transform: translateY(-4px) scale(1.03);
}
.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 12px;
}
.user-info {
  text-align: center;
}
.user-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}
.user-desc {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-top: 4px;
}
</style>