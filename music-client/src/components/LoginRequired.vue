// 登录required组件
<template>
  <div class="modern-login-container">
    <div class="content-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="left-brand-section">
        <div class="brand-display">
          <h1 class="brand-title-large">潮石音乐</h1>
          <p class="brand-subtitle-large">发现你的音乐世界</p>
        </div>
      </div>

      <!-- 中间登录卡片 -->
      <div class="center-login-section">
        <div class="login-card">
          <div class="card-header">
            <h2 class="login-title">欢迎回来</h2>
            <p class="login-subtitle">登录后即可享受完整的音乐体验</p>
          </div>

          <div class="login-form">
            <div class="social-login">
              <button class="social-btn primary-btn" @click="handleLogin">
                <svg class="btn-icon" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/>
                  <path d="M12 6c-3.31 0-6 2.69-6 6s2.69 6 6 6 6-2.69 6-6-2.69-6-6-6zm0 10c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4z"/>
                </svg>
                立即登录
              </button>
            </div>

            <div class="divider">
              <span>或者</span>
            </div>

            <div class="quick-actions">
              <button class="quick-btn" @click="goHome">
                <svg class="btn-icon" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
                </svg>
                返回首页
              </button>
            </div>
          </div>

          <div class="card-footer">
            <p class="footer-text">
              继续使用即表示您同意我们的
              <a href="#" class="footer-link">服务条款</a>
              和
              <a href="#" class="footer-link">隐私政策</a>
            </p>
          </div>
        </div>
      </div>

      <!-- 右侧功能区域 -->
      <div class="right-feature-section">
        <div class="feature-item">
          <div class="feature-icon">🎵</div>
          <div class="feature-text">
            <h3>海量曲库</h3>
            <p>千万首高品质音乐任你选择</p>
          </div>
        </div>

        <div class="feature-item">
          <div class="feature-icon">📱</div>
          <div class="feature-text">
            <h3>多端同步</h3>
            <p>手机、电脑、平板无缝切换</p>
          </div>
        </div>

        <div class="feature-item">
          <div class="feature-icon">🎧</div>
          <div class="feature-text">
            <h3>个性推荐</h3>
            <p>智能推荐，发现更多好音乐</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="site-footer">
      <div class="footer-links">
        <a href="#" class="footer-nav-link">关于潮石音乐</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">About Chaoshi Music</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">服务条款</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">用户服务协议</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">隐私政策</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">权利声明</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">广告服务</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">潮石招聘</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">客服中心</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">网站导航</a>
        <span class="separator">|</span>
        <a href="#" class="footer-nav-link">举报中心</a>
      </div>
      <div class="copyright-info">
        <p class="copyright-text">Copyright © 2025 Chaoshi Music. All Rights Reserved. 潮石音乐 版权所有</p>
        <p class="license-info">
          营业执照 | 
          网络文化经营许可证：粤网文[2023]2882-203号（示例编号，建议根据实际申请更新）| 
          网络视听许可证：待申请或根据实际情况填写 | 
          客服电话：400-400-400 | 
          违法与不良信息举报邮箱：<a href="mailto:2360303458@qq.com" class="email-link">2360303458@qq.com</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginRequired',
  methods: {
    // 跳转到登录页面
    handleLogin() {
      this.$router.push('/login')
    },
    
    // 返回首页
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.modern-login-container {
  width: 100% !important;
  height: 100vh !important;
  max-height: 100vh !important;
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  z-index: 1000;
  overflow: hidden !important;
  margin: 0 !important;
  padding: 2rem !important;
  box-sizing: border-box !important;
}

/* 内容包装器 */
.content-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* 左侧品牌区域 */
.left-brand-section {
  flex: 0 0 380px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;
  padding: 3rem 2rem;
}

/* 中间登录区域 */
.center-login-section {
  flex: 0 0 440px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 3rem 2rem;
}

/* 右侧功能区域 */
.right-feature-section {
  flex: 0 0 380px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  padding: 3rem 2rem;
}

/* 左侧品牌展示样式 */
.left-brand-section .brand-display {
  text-align: right;
}

.left-brand-section .brand-logo-large {
  margin-bottom: 2rem;
  text-align: right;
}

.left-brand-section .logo-icon-large {
  width: 70px;
  height: 70px;
  color: var(--primary);
  filter: drop-shadow(0 6px 12px rgba(0, 0, 0, 0.12));
  opacity: 0.9;
}

.left-brand-section .brand-title-large {
  font-size: 3.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 0.75rem 0;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
  text-align: right;
}

.left-brand-section .brand-subtitle-large {
  font-size: 1.25rem;
  color: var(--text-secondary);
  margin: 0;
  font-weight: 400;
  opacity: 0.85;
  letter-spacing: 0.3px;
  text-align: right;
}

/* 功能项目样式 */
.feature-item {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  padding: 0.875rem;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
  margin-bottom: 1.5rem;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.12);
}

.feature-icon {
  font-size: 1.25rem;
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  border-radius: 8px;
  flex-shrink: 0;
}

.feature-text h3 {
  font-size: 1rem;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 0.25rem 0;
}

.feature-text p {
  font-size: 0.825rem;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.4;
  opacity: 0.9;
}

.right-feature-section .feature-item {
  text-align: left;
}


.login-card {
  width: 100%;
  max-width: 340px;
  background: transparent;
  border-radius: 0;
  border: none;
  box-shadow: none;
  backdrop-filter: none;
  overflow: visible;
}

.card-header {
  padding: 1.25rem 1.25rem 0.75rem;
  text-align: center;
}

.login-title {
  font-size: 2.2rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 0.4rem 0;
}

.login-subtitle {
  font-size: 0.875rem;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

.login-form {
  padding: 0 1.25rem 1rem;
}

.social-login {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 42px;
}

.btn-icon {
  width: 18px;
  height: 18px;
}

.primary-btn {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.primary-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.divider {
  position: relative;
  text-align: center;
  margin: 1.5rem 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: rgba(0, 0, 0, 0.1);
}

.divider span {
  background: rgba(255, 255, 255, 0.95);
  color: #999;
  font-size: 0.8rem;
  padding: 0 0.875rem;
  position: relative;
}

.quick-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 0.5rem;
}

.quick-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.6);
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-btn:hover {
  background: rgba(255, 255, 255, 0.8);
  color: var(--primary);
  border-color: var(--primary);
}

.card-footer {
  padding: 0.875rem 1.25rem 1.25rem;
  border-top: none;
}

.footer-text {
  font-size: 0.75rem;
  color: #999;
  text-align: center;
  margin: 0;
  line-height: 1.4;
}

.footer-link {
  color: var(--primary);
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: var(--primary-dark);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .modern-login-container {
    height: calc(100vh - 100px) !important;
    max-height: calc(100vh - 100px) !important;
    bottom: 100px !important;
    overflow: hidden;
    padding: 1rem !important;
  }
  
  .content-wrapper {
    flex-direction: column;
    max-width: none;
  }
  
  .center-login-section {
    flex: 0 0 auto;
    order: 1;
    padding: 1.5rem;
  }
  
  .left-brand-section, .right-feature-section {
    flex: 0 1 auto;
    order: 2;
    padding: 1rem 1.5rem;
    max-width: none;
    align-items: center;
    text-align: center;
  }
  
  .right-feature-section {
    order: 3;
  }
  
  .left-brand-section .brand-display {
    text-align: center;
  }
  
  .left-brand-section .brand-logo-large {
    text-align: center;
    margin-bottom: 1.5rem;
  }
  
  .left-brand-section .brand-title-large,
  .left-brand-section .brand-subtitle-large {
    text-align: center;
  }
  
  .right-feature-section .feature-item {
    text-align: center;
    margin-bottom: 1rem;
  }
}

@media (max-width: 768px) {
  .modern-login-container {
    height: calc(100vh - 80px) !important;
    max-height: calc(100vh - 80px) !important;
    bottom: 80px !important;
    overflow: hidden;
    padding: 1rem !important;
  }
  
  .content-wrapper {
    flex-direction: column;
    max-width: none;
  }
  
  .center-login-section {
    flex: 0 0 auto;
    order: 1;
    padding: 1rem;
  }
  
  .left-brand-section, .right-feature-section {
    flex: 0 1 auto;
    order: 2;
    padding: 1rem;
    max-width: none;
    align-items: center;
    text-align: center;
  }
  
  .right-feature-section {
    order: 3;
  }
  
  .left-brand-section .brand-display {
    text-align: center;
  }
  
  .left-brand-section .logo-icon-large {
    width: 70px;
    height: 70px;
  }
  
  .left-brand-section .brand-title-large {
    font-size: 2.25rem;
    text-align: center;
  }
  
  .left-brand-section .brand-subtitle-large {
    font-size: 1.1rem;
    text-align: center;
  }
  
  .right-feature-section .feature-item {
    text-align: center;
    margin-bottom: 1rem;
    padding: 1rem;
  }
  
  .login-card {
    margin: 0;
    max-width: 100%;
  }
  
  .card-header {
    padding: 1rem 1rem 0.6rem;
  }
  
  .login-form {
    padding: 0 1rem 0.75rem;
  }
  
  .card-footer {
    padding: 0.75rem 1rem 1rem;
  }
}

/* 底部版权信息样式 */
.site-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100vw;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding: 1.5rem 2rem 1rem;
  text-align: center;
  z-index: 1001;
  box-sizing: border-box;
  overflow: hidden;
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.footer-nav-link {
  color: #666;
  text-decoration: none;
  font-size: 0.75rem;
  transition: color 0.3s ease;
  white-space: nowrap;
}

.footer-nav-link:hover {
  color: var(--primary);
}

.separator {
  color: #999;
  font-size: 0.75rem;
  margin: 0 0.25rem;
}

.copyright-text {
  color: #888;
  font-size: 0.7rem;
  margin: 0.5rem 0;
  line-height: 1.4;
}

.license-info {
  color: #777;
  font-size: 0.65rem;
  line-height: 1.3;
  margin: 0;
}

.email-link {
  color: var(--primary);
  text-decoration: none;
  transition: color 0.3s ease;
}

.email-link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

@media (max-width: 1024px) {
  .site-footer {
    height: 100px;
    padding: 1rem 1.5rem 0.5rem;
  }
  
  .footer-links {
    gap: 0.3rem;
  }
  
  .footer-nav-link {
    font-size: 0.7rem;
  }
  
  .separator {
    margin: 0 0.15rem;
  }
  
  .copyright-text {
    font-size: 0.65rem;
  }
  
  .license-info {
    font-size: 0.6rem;
  }
}

@media (max-width: 768px) {
  .site-footer {
    height: 80px;
    padding: 1rem 1rem 0.5rem;
  }
  
  .footer-links {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .footer-nav-link {
    font-size: 0.75rem;
  }
  
  .separator {
    display: none;
  }
  
  .copyright-text {
    font-size: 0.7rem;
  }
  
  .license-info {
    font-size: 0.65rem;
  }
}

@media (max-width: 480px) {
  .site-footer {
    padding: 0.8rem 0.8rem 0.5rem;
  }
  
  .footer-nav-link {
    font-size: 0.7rem;
  }
  
  .copyright-text {
    font-size: 0.65rem;
  }
  
  .license-info {
    font-size: 0.6rem;
    line-height: 1.3;
  }
}
</style>