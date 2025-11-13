import './assets/main.css'
import './styles/play-icon.css'
import { errorHandler } from '@/utils/errorHandler.js'
import { setContentSecurityPolicy } from '@/utils/security.js'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'

// 初始化安全策略
if (process.env.NODE_ENV === 'production') {
  setContentSecurityPolicy()
}

const app = createApp(App)

// 配置Element Plus的z-index
app.use(ElementPlus, {
  zIndex: 3000 // 设置基础z-index为3000，确保消息组件显示在登录页面之上
})

app.use(router)

// 设置Vue错误处理
app.config.errorHandler = (error, instance, info) => {
  errorHandler.handle(error, {
    source: 'vue-error',
    component: instance?.$options?.name || 'Unknown',
    info
  })
}

// 设置警告处理器
app.config.warnHandler = (msg, instance, trace) => {
  // 生产环境下忽略警告
}

app.mount('#app')

