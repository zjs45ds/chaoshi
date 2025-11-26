// 开放平台页面
<template>
  <div class="op-page" ref="opPageRef" :style="backgroundStyle">
    <div class="op-banner">
      <div class="op-logo-text">
        <img class="op-logo-img" src="@/assets/音乐培训.png" alt="logo" />
        <div class="op-logo-words">
          <span class="op-logo-main">潮石音乐</span>
          <span class="op-logo-en">CHAOSHIYINLE</span>
        </div>
      </div>
      <div class="op-title">潮石音乐开放平台</div>
      <div class="op-slogan">
        <span>闪耀</span>
        <br />
        <span>从这里开始</span>
      </div>
      <div class="op-promo">音乐 · 科技 · 无限可能</div>
    </div>

    <div class="op-content">
      <section class="op-intro">
        <h2>平台简介</h2>
        <div class="op-feature-card" style="margin-top: 30px;">
          <p>潮石音乐开放平台致力于为开发者、企业、内容方等合作伙伴提供丰富的音乐数据和能力接口，助力打造多元化的音乐应用与生态。</p>
        </div>
      </section>
      
      <section class="op-features">
        <h2>平台优势</h2>
        <div class="op-feature-list">
          <div class="op-feature-card">
            <h3>丰富的音乐资源</h3>
            <p>覆盖海量歌曲、歌手、专辑、歌单等数据，内容持续更新。</p>
          </div>
          <div class="op-feature-card">
            <h3>高效的API服务</h3>
            <p>标准化接口设计，快速集成，稳定高效，满足多样化开发需求。</p>
          </div>
          <div class="op-feature-card">
            <h3>合作共赢</h3>
            <p>支持多种合作模式，开放更多能力，携手共建音乐生态。</p>
          </div>
        </div>
      </section>

      <section class="op-features">
        <h2>平台功能</h2>
        <div class="op-feature-list">
          <div class="op-feature-card">
            <div class="feature-icon">🎵</div>
            <h3>音乐数据API</h3>
            <p>获取海量歌曲、歌手、专辑等音乐元数据，支持精准搜索和高级筛选。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">🔍</div>
            <h3>智能推荐</h3>
            <p>基于用户行为和喜好的个性化音乐推荐服务，提升用户体验。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">📱</div>
            <h3>移动SDK</h3>
            <p>提供iOS和Android平台SDK，快速集成音乐播放和控制功能。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">🎧</div>
            <h3>音频处理</h3>
            <p>提供音频分析、转码、均衡器等高级音频处理能力。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">📊</div>
            <h3>数据统计</h3>
            <p>详细的播放量、用户行为等数据分析和统计报表功能。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">🔐</div>
            <h3>版权保护</h3>
            <p>数字水印、加密传输等多重版权保护机制，保障内容安全。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">🎤</div>
            <h3>音乐直播API</h3>
            <p>提供音乐直播推流、播放和互动功能，支持实时音乐活动。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">📝</div>
            <h3>歌词同步显示</h3>
            <p>精准的歌词同步技术，支持逐字显示和翻译功能。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">📋</div>
            <h3>歌单管理工具</h3>
            <p>强大的歌单创建、编辑和推荐工具，支持个性化定制。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">🌐</div>
            <h3>多语言支持</h3>
            <p>支持20+种语言的界面和内容，覆盖全球主要地区。</p>
          </div>
          <div class="op-feature-card">
            <div class="feature-icon">🎮</div>
            <h3>互动音乐游戏</h3>
            <p>提供节奏游戏、音乐竞猜等互动模块，增强用户粘性。</p>
          </div>
        </div>
      </section>

      <section class="op-api-entry">
        <h2>潮石音乐 · 开发者激励计划</h2>
        <p>加入激励计划，参与创新项目，赢取丰厚奖励与专属资源支持，助力你的音乐梦想启航！</p>
        <div class="incentive-cards">
          <div class="incentive-card">
            <div class="incentive-icon">🏆</div>
            <h3>创新项目奖</h3>
            <p>年度最佳音乐应用</p>
          </div>
          <div class="incentive-card">
            <div class="incentive-icon">💡</div>
            <h3>创意设计奖</h3>
            <p>优秀UI/UX设计</p>
          </div>
          <div class="incentive-card">
            <div class="incentive-icon">🚀</div>
            <h3>技术突破奖</h3>
            <p>技术创新突破，奖金30</p>
          </div>
        </div>
      </section>

      <section class="op-stats">
        <h2>平台数据概览</h2>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">1000+</div>
            <div class="stat-label">合作伙伴</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">50M+</div>
            <div class="stat-label">API调用量</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">99.9%</div>
            <div class="stat-label">服务可用性</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">24/7</div>
            <div class="stat-label">技术支持</div>
          </div>
        </div>
      </section>

      <section class="op-integration">
        <h2>快速集成指南</h2>
        <div class="integration-steps">
          <div class="step">
            <div class="step-number"><img src="@/assets/指南.png" alt="步骤图标" style="width: 30px; height: 30px;" /></div>
            <h3>注册开发者账号</h3>
            <p>填写基本信息，完成身份验证</p>
          </div>
          <div class="step">
            <div class="step-number"><img src="@/assets/指南.png" alt="步骤图标" style="width: 30px; height: 30px;" /></div>
            <h3>选择API套餐</h3>
            <p>根据需求选择合适的服务套餐</p>
          </div>
          <div class="step">
            <div class="step-number"><img src="@/assets/指南.png" alt="步骤图标" style="width: 30px; height: 30px;" /></div>
            <h3>获取API密钥</h3>
            <p>系统自动生成专属API密钥</p>
          </div>
          <div class="step">
            <div class="step-number"><img src="@/assets/指南.png" alt="步骤图标" style="width: 30px; height: 30px;" /></div>
            <h3>开始集成开发</h3>
            <p>使用SDK和文档开始开发</p>
          </div>
        </div>
      </section>

      <section class="op-news">
        <h2>平台动态</h2>
        <div class="news-list">
          <div class="news-item">
            <div class="news-date">2023-10-15</div>
            <h3>潮石音乐API v2.0正式发布</h3>
            <p>全新升级的API接口，提供更丰富的音乐数据和更稳定的服务体验。</p>
            <a href="#" class="news-link">查看详情 →</a>
          </div>
          <div class="news-item">
            <div class="news-date">2023-09-28</div>
            <h3>开发者激励计划第三期启动</h3>
            <p>参与创新项目，赢取丰厚奖励与专属资源支持，助力你的音乐梦想启航！</p>
            <a href="#" class="news-link">立即参与 →</a>
          </div>
          <div class="news-item">
            <div class="news-date">2023-09-01</div>
            <h3>新增独家音乐内容合作</h3>
            <p>与多家知名唱片公司达成合作，新增100万+独家音乐内容。</p>
            <a href="#" class="news-link">了解更多 →</a>
          </div>
        </div>
      </section>

      <section class="op-faq">
        <h2>常见问题</h2>
        <div class="faq-list">
          <div class="faq-item">
            <h3>如何申请API密钥？</h3>
            <p>注册开发者账号后，在控制台中创建应用即可获取API密钥。</p>
          </div>
          <div class="faq-item">
            <h3>API调用有限制吗？</h3>
            <p>不同套餐有不同的调用限额，具体请参考开发者文档中的计费说明。</p>
          </div>
          <div class="faq-item">
            <h3>如何获取最新的API更新信息？</h3>
            <p>您可以订阅我们的开发者通讯，或关注开放平台的"平台动态"板块。</p>
          </div>
          <div class="faq-item">
            <h3>遇到技术问题如何寻求帮助？</h3>
            <p>您可以通过技术支持邮箱(support@chaoshiyinle.com)或开发者社区提问。</p>
          </div>
        </div>
      </section>

      <section class="op-contact">
        <h2>联系我们</h2>
        <div class="contact-info">
          <div class="contact-item">
            <div class="contact-icon">📧</div>
            <div class="contact-details">
              <h3>商务合作</h3>
              <p>business@chaoshiyinle.com</p>
          </div>
          </div>
          <div class="contact-item">
            <div class="contact-icon">🔧</div>
            <div class="contact-details">
              <h3>技术支持</h3>
              <p>support@chaoshiyinle.com</p>
            </div>
          </div>
          <div class="contact-item">
            <div class="contact-icon">📱</div>
            <div class="contact-details">
              <h3>客服热线</h3>
              <p>400-888-8888</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive, computed } from 'vue'

const opPageRef = ref(null)
const backgroundState = reactive({
  url: '',
  isLoaded: false
})

// 从localStorage获取背景图
const getBackgroundFromStorage = () => {
  const savedBg = localStorage.getItem('userBannerBg')
  return savedBg || '/src/assets/跃.jpg'
}

// 设置背景图
const setBackground = (url) => {
  if (!url) {
    backgroundState.url = '/src/assets/跃.jpg'
  } else {
    backgroundState.url = url
  }
  // 标记背景已加载
  backgroundState.isLoaded = true
}

// 背景变化处理函数
const handleBackgroundChange = (event) => {
  console.log('OpenPlatform received background-changed event:', event.detail)
  if (event.detail && event.detail.url) {
    console.log('Updating background to:', event.detail.url)
    setBackground(event.detail.url)
  } else {
    // 如果没有传递url，使用默认背景
    console.log('Using default background')
    setBackground('/src/assets/跃.jpg')
  }
}

// 计算属性，用于模板绑定
const backgroundStyle = computed(() => ({
  backgroundImage: `url(${backgroundState.url})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
  backgroundAttachment: 'fixed',
  transition: 'background-image 0.5s ease-in-out'
}))

// storage事件处理函数
const handleStorageChange = (event) => {
  if (event.key === 'userBannerBg') {
    console.log('OpenPlatform received storage event:', event.newValue)
    setBackground(event.newValue)
  }
}

onMounted(() => {
  // 初始化背景
  const initialBg = getBackgroundFromStorage()
  setBackground(initialBg)

  // 监听背景变化事件（同窗口）
  window.addEventListener('background-changed', handleBackgroundChange)
  // 监听storage事件（跨窗口）
  window.addEventListener('storage', handleStorageChange)
  
  // 为body添加开放平台样式类
  document.body.classList.add('open-platform-page')
})

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('background-changed', handleBackgroundChange)
  window.removeEventListener('storage', handleStorageChange)
  
  // 移除开放平台样式类
  document.body.classList.remove('open-platform-page')
})
</script>

<style scoped>
.op-page {
  min-height: 100vh;
  width: 100vw;
  color: var(--text-primary);
  font-family: 'Microsoft YaHei', sans-serif;
  position: relative;
  margin: 0;
  padding: 0;
  overflow-x: hidden; /* 防止水平滚动条 */
  overflow-y: auto; /* 允许垂直滚动 */
}

/* 确保内容区域在背景图上方 */
.op-content {
  position: relative;
  z-index: 2; /* 提高层级确保内容在背景之上 */
}

/* 半透明覆盖层，提升文字可读性 */
.op-page::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.3));
  pointer-events: none;
  z-index: 1;
}

.op-banner {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-top: 0;
  padding: clamp(80px, 20vh, 150px) 20px;
  min-height: 60vh;
  box-sizing: border-box;
  z-index: 2; /* 确保在背景之上 */
}

.background-info {
  position: absolute;
  top: 20px;
  left: 30px;
  background: var(--background-card);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  color: var(--text-primary);
  backdrop-filter: blur(10px);
  z-index: 10;
  border: 1px solid var(--border);
}

.op-logo-text {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.op-logo-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.op-logo-words {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.op-logo-main {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
}

.op-logo-en {
  font-size: 14px;
  color: var(--text-secondary);
  letter-spacing: 2px;
}

.op-title {
  font-size: 3.5rem;
  font-weight: bold;
  margin-bottom: 15px;
  background: linear-gradient(45deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: transform 0.3s ease;
}

.op-title:hover {
  transform: translateY(-2px);
}

.op-slogan {
  font-size: 1.8rem;
  margin-bottom: 15px;
  text-shadow: 0 0 10px var(--text-primary), 0 0 20px var(--text-primary), 0 0 30px var(--primary);
  color: var(--text-primary);
}

.op-promo {
  font-size: 1.2rem;
  color: var(--text-secondary);
}

.op-content {
  padding: 40px 20px 60px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 30vh;
  position: relative;
  z-index: 2; /* 确保在背景之上 */
}

.op-content section {
  margin-bottom: 40px;
}

.op-content h2 {
  font-size: 2.5rem;
  text-align: center;
  margin-bottom: 30px;
  color: var(--text-primary);
}

.op-content p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: var(--text-secondary);
  text-align: center;
  max-width: 800px;
  margin: 0 auto;
}

.op-feature-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.op-feature-card {
  background: var(--background-card);
  padding: 30px;
  border-radius: 15px;
  text-align: center;
  border: 1px solid var(--border);
  transition: transform 0.3s ease;
  backdrop-filter: blur(10px);
}

.op-feature-card:hover {
  transform: translateY(-10px);
  background: var(--background-light);
  border-color: var(--primary);
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
}

.op-feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.op-feature-card p {
  font-size: 1rem;
  color: var(--text-secondary);
  text-align: center;
}

/* 激励计划卡片 */
.incentive-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.incentive-card {
  background: var(--background-card);
  padding: 25px;
  border-radius: 15px;
  text-align: center;
  border: 1px solid var(--border);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.incentive-card:hover {
  transform: translateY(-5px);
  background: var(--background-light);
  border-color: var(--primary);
}

.incentive-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.incentive-card h3 {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: var(--text-primary);
}

.incentive-card p {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

/* 统计数据 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.stat-item {
  text-align: center;
  padding: 30px 20px;
  background: var(--background-card);
  border-radius: 15px;
  border: 1px solid var(--border);
  backdrop-filter: blur(10px);
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.stat-label {
  font-size: 1rem;
  color: var(--text-secondary);
}

/* 集成步骤 */
.integration-steps {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.step {
  text-align: center;
  padding: 30px 20px;
  background: var(--background-card);
  border-radius: 15px;
  border: 1px solid var(--border);
  position: relative;
  backdrop-filter: blur(10px);
}

.step-number {
  width: 50px;
  height: 50px;
  background: linear-gradient(45deg, var(--primary), var(--secondary));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 auto 20px;
}

.step h3 {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.step p {
  font-size: 1rem;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* 平台动态 */
.op-news {
  margin-bottom: 60px;
}

.news-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
  margin-top: 30px;
}

.news-item {
  padding: 25px;
  background: var(--background-card);
  border-radius: 15px;
  border: 1px solid var(--border);
  transition: transform 0.3s ease;
  backdrop-filter: blur(10px);
}

.news-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.news-date {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 10px;
}

.news-item h3 {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.news-item p {
  font-size: 1rem;
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.news-link {
  display: inline-block;
  color: var(--primary);
  font-weight: 600;
  text-decoration: none;
  transition: color 0.3s ease;
}

.news-link:hover {
  color: var(--primary-light);
}

/* 常见问题 */
.op-faq {
  margin-bottom: 60px;
}

.faq-list {
  max-width: 800px;
  margin: 30px auto 0;
}

.faq-item {
  margin-bottom: 15px;
  padding: 20px;
  background: var(--background-card);
  border-radius: 15px;
  border: 1px solid var(--border);
  backdrop-filter: blur(10px);
}

.faq-item h3 {
  font-size: 1.1rem;
  margin-bottom: 10px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
}

.faq-item h3::before {
  content: 'Q: ';
  font-weight: bold;
  color: var(--primary);
  margin-right: 8px;
}

.faq-item p {
  font-size: 1rem;
  color: var(--text-secondary);
  margin-left: 24px;
}

.faq-item p::before {
  content: 'A: ';
  font-weight: bold;
  color: var(--secondary);
  margin-right: 8px;
}

/* 联系信息 */
.contact-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 25px;
  background: var(--background-card);
  border-radius: 15px;
  border: 1px solid var(--border);
  backdrop-filter: blur(10px);
}

.contact-icon {
  font-size: 2.5rem;
}

.contact-details h3 {
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.contact-details p {
  font-size: 1rem;
  color: var(--text-secondary);
}

/* 页脚样式 */

@media (max-width: 768px) {
  .op-title {
    font-size: 2.5rem;
  }
  
  .op-slogan {
    font-size: 1.5rem;
  }
  
  .op-content {
    padding: 40px 20px;
  }
  
  .op-feature-list {
    grid-template-columns: 1fr;
  }
  
  .background-info {
    top: 15px;
    left: 20px;
    font-size: 0.8rem;
    padding: 6px 12px;
  }
  
  .incentive-cards,
  .stats-grid,
  .integration-steps,
  .contact-info {
    grid-template-columns: 1fr;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    text-align: center;
  }
}
/* 添加全局样式重置 */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  html, body {
    margin: 0;
    padding: 0;
    height: 100%;
    overflow-x: hidden;
  }
  </style>

