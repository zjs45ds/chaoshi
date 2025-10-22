// 404 页面
<template>
  <div class="not-found-page">
    <div class="not-found-container">
      <div class="error-illustration">
        <div class="error-code">404</div>
        <div class="error-icon">🎵</div>
      </div>
      
      <div class="error-content">
        <h1 class="error-title">页面未找到</h1>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被移除
        </p>
        
        <div class="error-actions">
          <button class="btn-primary" @click="goHome">
            <i class="icon">🏠</i>
            返回首页
          </button>
          <button class="btn-secondary" @click="goBack">
            <i class="icon">⬅️</i>
            返回上页
          </button>
        </div>
      </div>
      
      <div class="suggestions">
        <h3>您可能想要访问：</h3>
        <div class="suggestion-links">
          <router-link to="/" class="suggestion-item">
            <i class="icon">🎵</i>
            <span>音乐首页</span>
          </router-link>
          <router-link to="/artist" class="suggestion-item">
            <i class="icon">👤</i>
            <span>歌手列表</span>
          </router-link>
          <router-link to="/album" class="suggestion-item">
            <i class="icon">💿</i>
            <span>专辑推荐</span>
          </router-link>
          <router-link to="/toplist" class="suggestion-item">
            <i class="icon">🏆</i>
            <span>音乐排行</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const goHome = () => {
  if (route.path !== '/') {
    router.push('/')
  }
}

const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else if (route.path !== '/') {
    router.push('/')
  }
}
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--background) 0%, var(--background-light) 100%);
  padding: 20px;
}

.not-found-container {
  max-width: 600px;
  text-align: center;
  background: var(--background-card);
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border);
}

.error-illustration {
  position: relative;
  margin-bottom: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: 900;
  color: var(--primary);
  line-height: 1;
  margin-bottom: 20px;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.error-icon {
  font-size: 60px;
  opacity: 0.8;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.error-content {
  margin-bottom: 40px;
}

.error-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.error-description {
  font-size: 18px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 32px;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary, .btn-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  min-width: 140px;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(236, 72, 153, 0.3);
}

.btn-secondary {
  background: transparent;
  color: var(--text-primary);
  border: 2px solid var(--border);
}

.btn-secondary:hover {
  background: var(--border);
  transform: translateY(-2px);
}

.suggestions {
  border-top: 1px solid var(--border);
  padding-top: 32px;
}

.suggestions h3 {
  font-size: 20px;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.suggestion-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
}

.suggestion-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 16px;
  background: var(--background-light);
  border-radius: 12px;
  text-decoration: none;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.suggestion-item:hover {
  color: var(--primary);
  border-color: var(--primary);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(236, 72, 153, 0.2);
}

.suggestion-item .icon {
  font-size: 24px;
}

.suggestion-item span {
  font-size: 14px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .not-found-container {
    padding: 40px 20px;
  }
  
  .error-code {
    font-size: 80px;
  }
  
  .error-title {
    font-size: 28px;
  }
  
  .error-description {
    font-size: 16px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .btn-primary, .btn-secondary {
    width: 100%;
    max-width: 200px;
  }
  
  .suggestion-links {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .suggestion-links {
    grid-template-columns: 1fr;
  }
}
</style>