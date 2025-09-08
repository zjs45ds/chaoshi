<template>
  <div class="login-container" :class="backgroundType">
    <!-- 动态粒子背景 -->
    <div v-if="backgroundType === 'particles'" class="particles-background">
      <div class="particle" v-for="n in 50" :key="n" :style="getParticleStyle(n)"></div>
    </div>
    
    <!-- 动态渐变背景 -->
    <div v-if="backgroundType === 'gradient'" class="gradient-background"></div>
    
    <!-- 自定义背景 -->
    <div v-if="backgroundType === 'custom' && customBackground" 
         class="custom-background" 
         :style="{ backgroundImage: `url(${customBackground})` }"></div>
    
    <!-- 背景控制面板 -->
    <div class="background-controls">
      <button @click="toggleBackgroundType" class="bg-toggle-btn" title="切换背景">
        🎨
      </button>
      <input 
        ref="fileInput" 
        type="file" 
        accept="image/*" 
        @change="handleCustomBackground" 
        style="display: none"
      >
      <button @click="selectCustomBackground" class="bg-upload-btn" title="上传背景">
        📁
      </button>
    </div>
    
    <!-- 处理提示 -->
    <div v-if="isProcessing" class="processing-overlay">
      <div class="processing-content">
        <div class="spinner"></div>
        <p>正在进行优化处理...</p>
      </div>
    </div>
    
    <!-- SVG滤镜库 -->
    <svg style="display: none">
      <filter id="glass-distortion" x="0%" y="0%" width="100%" height="100%" filterUnits="objectBoundingBox">
        <feTurbulence type="fractalNoise" baseFrequency="0.001 0.005" numOctaves="1" seed="17" result="turbulence" />
        <feComponentTransfer in="turbulence" result="mapped">
          <feFuncR type="gamma" amplitude="1" exponent="10" offset="0.5" />
          <feFuncG type="gamma" amplitude="0" exponent="1" offset="0" />
          <feFuncB type="gamma" amplitude="0" exponent="1" offset="0.5" />
        </feComponentTransfer>
        <feGaussianBlur in="turbulence" stdDeviation="3" result="softMap" />
        <feSpecularLighting in="softMap" surfaceScale="5" specularConstant="1" specularExponent="100" lighting-color="white" result="specLight">
          <fePointLight x="-200" y="-200" z="300" />
        </feSpecularLighting>
        <feComposite in="specLight" operator="arithmetic" k1="0" k2="1" k3="1" k4="0" result="litImage" />
        <feDisplacementMap in="SourceGraphic" in2="softMap" scale="200" xChannelSelector="R" yChannelSelector="G" />
      </filter>
    </svg>

    <!-- 登录/注册卡片 -->
    <div
      class="glass-component login-card"
      ref="tiltCard"
      @mousemove="handleMouseMove"
      @mouseleave="handleMouseLeave"
    >
      <div class="glass-effect"></div>
      <div class="glass-tint"></div>
      <div class="glass-shine"></div>
      <div class="glass-content">
        <!-- 登录表单 -->
        <div v-if="!isRegisterMode" class="form-container">
          <h2 class="login-title">欢迎登录</h2>
          <form class="login-form" @submit.prevent="handleLogin">
            <div class="form-group">
              <input 
                type="text" 
                placeholder="用户名" 
                class="glass-input"
                :class="{ 'error': loginErrors.username }"
                v-model="loginForm.username"
                @blur="validateLoginField('username')"
                @input="clearLoginError('username')"
                required
              >
              <div v-if="loginErrors.username" class="error-message">{{ loginErrors.username }}</div>
            </div>
            <div class="form-group">
              <input 
                type="password" 
                placeholder="密码" 
                class="glass-input"
                :class="{ 'error': loginErrors.password }"
                v-model="loginForm.password"
                @blur="validateLoginField('password')"
                @input="clearLoginError('password')"
                required
              >
              <div v-if="loginErrors.password" class="error-message">{{ loginErrors.password }}</div>
            </div>
            <div class="form-group remember-group">
              <label class="remember-label">
                <input type="checkbox" v-model="loginForm.remember" class="glass-checkbox">
                <span>记住我</span>
              </label>
            </div>
            <button type="submit" class="glass-button">登录</button>
          </form>
          <div class="form-footer">
            <p class="switch-form" @click="toggleMode">没有账号？立即注册</p>
            <p class="forgot-password">忘记密码？</p>
          </div>
        </div>

        <!-- 注册表单 -->
        <div v-else class="form-container">
          <h2 class="login-title">创建账号</h2>
          <form class="login-form" @submit.prevent="handleRegister">
            <div class="form-group">
              <input 
                type="text" 
                placeholder="用户名" 
                class="glass-input"
                :class="{ 'error': registerErrors.username }"
                v-model="registerForm.username"
                @blur="validateRegisterField('username')"
                @input="clearRegisterError('username')"
                required
              >
              <div v-if="registerErrors.username" class="error-message">{{ registerErrors.username }}</div>
            </div>
            <div class="form-group">
              <input 
                type="email" 
                placeholder="邮箱" 
                class="glass-input"
                :class="{ 'error': registerErrors.email }"
                v-model="registerForm.email"
                @blur="validateRegisterField('email')"
                @input="clearRegisterError('email')"
                required
              >
              <div v-if="registerErrors.email" class="error-message">{{ registerErrors.email }}</div>
            </div>
            <div class="form-group">
              <input 
                type="password" 
                placeholder="密码" 
                class="glass-input"
                :class="{ 'error': registerErrors.password }"
                v-model="registerForm.password"
                @blur="validateRegisterField('password')"
                @input="clearRegisterError('password')"
                required
              >
              <div v-if="registerErrors.password" class="error-message">{{ registerErrors.password }}</div>
            </div>
            <div class="form-group">
              <input 
                type="password" 
                placeholder="确认密码" 
                class="glass-input"
                :class="{ 'error': registerErrors.confirmPassword }"
                v-model="registerForm.confirmPassword"
                @blur="validateRegisterField('confirmPassword')"
                @input="clearRegisterError('confirmPassword')"
                required
              >
              <div v-if="registerErrors.confirmPassword" class="error-message">{{ registerErrors.confirmPassword }}</div>
            </div>
            <div class="form-group">
              <input 
                type="text" 
                placeholder="手机号" 
                class="glass-input"
                :class="{ 'error': registerErrors.phone }"
                v-model="registerForm.phone"
                @blur="validateRegisterField('phone')"
                @input="clearRegisterError('phone')"
                required
              >
              <div v-if="registerErrors.phone" class="error-message">{{ registerErrors.phone }}</div>
            </div>
            <button type="submit" class="glass-button">注册</button>
          </form>
          <div class="form-footer">
            <p class="switch-form" @click="toggleMode">已有账号？立即登录</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { validateImageFile, compressImage } from '@/utils/imageUtils.js'
import { login, register } from '@/api/user.js'

export default {
  name: 'LiquidGlass',
  setup() {
    const router = useRouter()
    const isRegisterMode = ref(false)
    const backgroundType = ref('gradient') // 默认动态渐变背景
    const customBackground = ref('')
    const isProcessing = ref(false)
    const fileInput = ref(null)
    const loginForm = ref({ 
      username: '', 
      password: '',
      remember: false
    })
    const registerForm = ref({ 
      username: '', 
      email: '', 
      password: '', 
      confirmPassword: '', 
      phone: '' 
    })
    const loginErrors = ref({
      username: '',
      password: ''
    })
    const registerErrors = ref({
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      phone: ''
    })

    const toggleMode = () => {
      isRegisterMode.value = !isRegisterMode.value
      // 清空表单和错误提示
      loginForm.value = { username: '', password: '', remember: false }
      registerForm.value = { username: '', email: '', password: '', confirmPassword: '', phone: '' }
      loginErrors.value = { username: '', password: '' }
      registerErrors.value = { username: '', email: '', password: '', confirmPassword: '', phone: '' }
    }

    const validateLoginField = (field) => {
      const form = loginForm.value
      const errors = loginErrors.value
      
      switch (field) {
        case 'username':
          if (!form.username.trim()) {
            errors.username = '请输入用户名'
          } else {
            errors.username = ''
          }
          break
        case 'password':
          if (!form.password.trim()) {
            errors.password = '请输入密码'
          } else {
            errors.password = ''
          }
          break
      }
    }

    const validateRegisterField = (field) => {
      const form = registerForm.value
      const errors = registerErrors.value
      
      switch (field) {
        case 'username':
          if (!form.username.trim()) {
            errors.username = '请输入用户名'
          } else if (form.username.trim().length < 3) {
            errors.username = '用户名至少需要3个字符'
          } else {
            errors.username = ''
          }
          break
        case 'email':
          const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
          if (!form.email.trim()) {
            errors.email = '请输入邮箱'
          } else if (!emailRegex.test(form.email)) {
            errors.email = '请输入有效的邮箱地址'
          } else {
            errors.email = ''
          }
          break
        case 'password':
          if (!form.password.trim()) {
            errors.password = '请输入密码'
          } else if (form.password.length < 6) {
            errors.password = '密码至少需要6个字符'
          } else {
            errors.password = ''
          }
          break
        case 'confirmPassword':
          if (!form.confirmPassword.trim()) {
            errors.confirmPassword = '请确认密码'
          } else if (form.confirmPassword !== form.password) {
            errors.confirmPassword = '两次密码不一致'
          } else {
            errors.confirmPassword = ''
          }
          break
        case 'phone':
          const phoneRegex = /^1[3-9]\d{9}$/
          if (!form.phone.trim()) {
            errors.phone = '请输入手机号'
          } else if (!phoneRegex.test(form.phone)) {
            errors.phone = '请输入有效的手机号'
          } else {
            errors.phone = ''
          }
          break
      }
    }

    const clearLoginError = (field) => {
      loginErrors.value[field] = ''
    }

    const clearRegisterError = (field) => {
      registerErrors.value[field] = ''
    }

    const validateLoginForm = () => {
      validateLoginField('username')
      validateLoginField('password')
      return !loginErrors.value.username && !loginErrors.value.password
    }

    const validateRegisterForm = () => {
      validateRegisterField('username')
      validateRegisterField('email')
      validateRegisterField('password')
      validateRegisterField('confirmPassword')
      validateRegisterField('phone')
      return !Object.values(registerErrors.value).some(error => error)
    }

    const handleLogin = async () => {
      if (!validateLoginForm()) {
        return
      }
      
      // 显示加载状态
      const loadingMessage = ElMessage({
        message: '🔍 正在登录...',
        type: 'info',
        duration: 0, // 不自动消失
        showClose: false
      })
      
      try {
        // 调用后端API进行登录
        const response = await login({
          username: loginForm.value.username,
          password: loginForm.value.password
        })
        
        // 关闭加载提示
        loadingMessage.close()
        
        if (!response) {
          throw new Error('登录响应为空')
        }
        
        // 从response.data中获取用户数据（后端返回的数据结构）
        const userData = response.data
        if (!userData || !userData.token) {
          throw new Error('登录响应中缺少token')
        }
        
        // 登录成功 - 显示后端返回的成功消息（如果有的话）
        const successMessage = response.message || `🎉 欢迎回来，${userData.nickname || userData.username}！登录成功`
        
        ElMessage({
          message: successMessage,
          type: 'success',
          duration: 3000,
          showClose: true,
          dangerouslyUseHTMLString: false
        })
        
        // 存储用户信息和token
        localStorage.setItem('isLogin', '1')
        localStorage.setItem('token', userData.token)
        localStorage.setItem('userId', userData.id)
        localStorage.setItem('username', userData.username)
        localStorage.setItem('nickname', userData.nickname || userData.username)
        if (userData.avatar) {
          localStorage.setItem('avatar', userData.avatar)
        }
        
        // 清空表单
        loginForm.value = { username: '', password: '', remember: false }
        
        // 跳转到主页（延迟一下让用户看到成功提示）
        setTimeout(() => {
          router.push('/my-music')
        }, 1500)
        
      } catch (error) {
        // 关闭加载提示
        loadingMessage.close()
        
        // 获取错误消息（优先使用后端返回的友好提示）
        let errorMessage = '❌ 登录失败'
        let shouldShowError = true
        
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.message === 'Network Error' || error.code === 'ECONNABORTED' || error.code === 'ECONNREFUSED') {
          // 网络错误已在httpUtils.js中处理，这里不再重复显示
          shouldShowError = false
        } else if (error.response) {
          // 根据状态码提供具体错误信息
          const status = error.response.status
          if (status === 401) {
            errorMessage = '❌ 用户名或密码错误，请重新输入'
          } else if (status === 404) {
            errorMessage = '❌ API接口不存在，请检查后端配置'
          } else if (status === 500) {
            errorMessage = '❌ 服务器内部错误，请稍后重试'
          } else if (status >= 500) {
            errorMessage = '❌ 服务器暂时不可用，请稍后重试'
          } else {
            errorMessage = `❌ 请求失败 (${status})，请稍后重试`
          }
        } else if (error.message) {
          errorMessage = '❌ ' + error.message
        }
        
        // 只在需要时显示错误消息
        if (shouldShowError) {
          ElMessage({
            message: errorMessage,
            type: 'error',
            duration: 5000,
            showClose: true
          })
        }
      }
    }

    const handleRegister = async () => {
      if (!validateRegisterForm()) {
        return
      }
      
      // 显示加载状态
      const loadingMessage = ElMessage({
        message: '📝 正在注册...',
        type: 'info',
        duration: 0,
        showClose: false
      })
      
      try {
        // 调用后端API进行注册
        const response = await register({
          username: registerForm.value.username,
          email: registerForm.value.email,
          password: registerForm.value.password,
          phone: registerForm.value.phone
        })
        
        // 关闭加载提示
        loadingMessage.close()
        
        // 注册成功 - 显示后端返回的成功消息（如果有的话）
        const successMessage = response.message || '🎉 注册成功！请使用您的账户登录'
        
        ElMessage({
          message: successMessage,
          type: 'success',
          duration: 4000,
          showClose: true,
          dangerouslyUseHTMLString: false
        })
        
        // 注册成功后切换到登录模式
        isRegisterMode.value = false
        
        // 将注册的用户名自动填入登录表单
        loginForm.value.username = registerForm.value.username
        loginForm.value.password = '' // 不填入密码，让用户自己输入
        
        // 清空注册表单
        registerForm.value = { username: '', email: '', password: '', confirmPassword: '', phone: '' }
        
      } catch (error) {
        // 关闭加载提示
        loadingMessage.close()
        
        // 获取错误消息（优先使用后端返回的友好提示）
        let errorMessage = '❌ 注册失败'
        let shouldShowError = true
        
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.message === 'Network Error' || error.code === 'ECONNABORTED' || error.code === 'ECONNREFUSED') {
          // 网络错误已在httpUtils.js中处理
          shouldShowError = false
        } else if (error.response) {
          const status = error.response.status
          if (status === 400) {
            errorMessage = '❌ 输入信息有误，请检查后重试'
          } else if (status === 409) {
            errorMessage = '❌ 用户名或邮箱已存在，请更换后重试'
          } else if (status === 500) {
            errorMessage = '❌ 服务器内部错误，请稍后重试'
          } else if (status >= 500) {
            errorMessage = '❌ 服务器暂时不可用，请稍后重试'
          } else {
            errorMessage = `❌ 注册失败 (${status})，请稍后重试`
          }
        } else if (error.message) {
          errorMessage = '❌ ' + error.message
        }
        
        if (shouldShowError) {
          ElMessage({
            message: errorMessage,
            type: 'error',
            duration: 5000,
            showClose: true
          })
        }
      }
    }

    // 后台请求相关功能

    // 背景相关功能
    const backgroundTypes = ['gradient', 'particles', 'custom']
    let currentTypeIndex = 0

    const toggleBackgroundType = () => {
      currentTypeIndex = (currentTypeIndex + 1) % backgroundTypes.length
      backgroundType.value = backgroundTypes[currentTypeIndex]
      
      // 如果切换到自定义背景但没有自定义图片，则跳过
      if (backgroundType.value === 'custom' && !customBackground.value) {
        toggleBackgroundType()
        return
      }
    }

    const selectCustomBackground = () => {
      fileInput.value?.click()
    }

    // 图片压缩处理函数
    const compressImageLocal = compressImage

    const handleCustomBackground = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      // 验证文件
      const validation = validateImageFile(file)
      if (!validation.isValid) {
        validation.errors.forEach(error => ElMessage.error(error))
        return
      }
      
      try {
        // 显示处理提示
        if (validation.needsCompression) {
          isProcessing.value = true
        }
        
        // 压缩图片
        const result = await compressImageLocal(file)
        
        // 检查压缩后大小
        if (parseFloat(result.compressedSize) > 3) {
          ElMessage.warning('图片较大，建议使用更小的图片以获得更好的性能')
        }
        
        // 尝试保存到localStorage
        try {
          const backgroundData = {
            data: result.dataUrl,
            backgroundProcessed: true,
            timestamp: Date.now()
          }
          localStorage.setItem('customLoginBackground', JSON.stringify(backgroundData))
          customBackground.value = result.dataUrl
          backgroundType.value = 'custom'
          ElMessage.success('自定义背景设置成功')
        } catch (storageError) {
          // localStorage存储失败
          customBackground.value = result.dataUrl
          backgroundType.value = 'custom'
          ElMessage.warning('背景过大，无法保存到本地，刷新页面后将恢复默认背景')
        }
      } catch (error) {
        ElMessage.error('图片处理失败，请重试')
      } finally {
        isProcessing.value = false
        // 清空input
        if (fileInput.value) {
          fileInput.value.value = ''
        }
      }
    }

    // 粒子动画相关
    const getParticleStyle = (index) => {
      const size = Math.random() * 4 + 2
      const duration = Math.random() * 20 + 10
      const delay = Math.random() * 5
      
      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${Math.random() * 100}%`,
        top: `${Math.random() * 100}%`,
        animationDuration: `${duration}s`,
        animationDelay: `${delay}s`
      }
    }

    // 初始化时加载保存的自定义背景
    onMounted(() => {
      try {
        const savedBackground = localStorage.getItem('customLoginBackground')
        if (savedBackground) {
          const backgroundData = JSON.parse(savedBackground)
          if (backgroundData.backgroundProcessed && backgroundData.data) {
            customBackground.value = backgroundData.data
            // 如果有保存的背景，默认使用自定义背景
            backgroundType.value = 'custom'
          }
        }
      } catch (error) {
        // 无法加载自定义背景
      }
    })

    return {
      isRegisterMode,
      loginForm,
      registerForm,
      loginErrors,
      registerErrors,
      backgroundType,
      customBackground,
      isProcessing,
      fileInput,
      toggleMode,
      handleLogin,
      handleRegister,
      validateLoginField,
      validateRegisterField,
      clearLoginError,
      clearRegisterError,
      toggleBackgroundType,
      selectCustomBackground,
      handleCustomBackground,
      getParticleStyle
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  width: 100vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
}

/* 动态渐变背景 */
.gradient-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(-45deg, 
    #667eea 0%, 
    #764ba2 25%, 
    #f093fb 50%, 
    #f5576c 75%, 
    #4facfe 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  z-index: -1;
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 粒子背景 */
.particles-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  z-index: -1;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: float linear infinite;
}

@keyframes float {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 自定义背景 */
.custom-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: -1;
}

/* 背景控制面板 */
.background-controls {
  position: fixed;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
  z-index: 1001;
}

.bg-toggle-btn,
.bg-upload-btn {
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  color: white;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.bg-toggle-btn:hover,
.bg-upload-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1);
}

/* 处理提示覆盖层 */
.processing-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1002;
}

.processing-content {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 30px;
  border-radius: 15px;
  text-align: center;
  color: white;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.login-card {
  width: 400px;
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 24px 0 rgba(0,0,0,0.10), 0 1.5px 6px 0 rgba(0,0,0,0.08);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.6);
  cursor: pointer;
  background: transparent;
}

.glass-effect {
  position: absolute;
  inset: 0;
  z-index: 0;
  backdrop-filter: blur(5px);
  filter: url(#glass-distortion);
  isolation: isolate;
  border-radius: 24px;
}

.glass-tint {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: rgba(0, 0, 0, 0.15);
  border-radius: 24px;
}

.glass-shine {
  position: absolute;
  inset: 0;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.13);
  border-radius: 24px;
  box-shadow:
    inset 1px 1px 8px 0 rgba(255, 255, 255, 0.18),
    inset -1px -1px 8px 0 rgba(255, 255, 255, 0.08);
  pointer-events: none;
}

.glass-content {
  position: relative;
  z-index: 3;
  padding: 2rem;
  color: white;
}

.form-container {
  transition: all 0.3s ease;
}

.login-title {
  text-align: center;
  color: #fff;
  margin-bottom: 2rem;
  font-size: 2rem;
  font-weight: 600;
  text-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

.form-group {
  margin-bottom: 1.5rem;
  
  &.remember-group {
    margin-bottom: 1rem;
  }
}

.glass-input {
  width: 100%;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 1rem;
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
  box-sizing: border-box;

  &::placeholder {
    color: rgba(255, 255, 255, 0.7);
  }

  &:focus {
    outline: none;
    background: rgba(255, 255, 255, 0.2);
    box-shadow: 0 0 15px rgba(255, 255, 255, 0.1);
  }

  &.error {
    border: 1px solid rgba(255, 107, 107, 0.8);
    box-shadow: 0 0 10px rgba(255, 107, 107, 0.3);
  }
}

.error-message {
  color: rgba(255, 107, 107, 0.9);
  font-size: 0.8rem;
  margin-top: 5px;
  margin-left: 5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  animation: fadeInError 0.3s ease-in-out;
}

@keyframes fadeInError {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.glass-button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
  position: relative;
  overflow: hidden;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  }

  &:active {
    transform: translateY(0);
  }
}

.remember-group {
  margin-bottom: 1rem;
}

.remember-label {
  display: flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  cursor: pointer;

  span {
    margin-left: 8px;
  }
}

.glass-checkbox {
  appearance: none;
  width: 16px;
  height: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.1);
  cursor: pointer;
  position: relative;

  &:checked {
    background: rgba(255, 255, 255, 0.3);
    border-color: rgba(255, 255, 255, 0.6);

    &::after {
      content: '✓';
      position: absolute;
      top: -2px;
      left: 1px;
      color: white;
      font-size: 12px;
      font-weight: bold;
    }
  }
}

.form-footer {
  margin-top: 1.5rem;
  text-align: center;
}

.switch-form {
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  margin: 0.5rem 0;
  font-size: 0.9rem;
  transition: color 0.3s ease;

  &:hover {
    color: white;
  }
}

.forgot-password {
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  margin: 0;
  font-size: 0.9rem;
  transition: color 0.3s ease;

  &:hover {
    color: rgba(255, 255, 255, 0.8);
  }
}

.form-container {
  transition: all 0.3s ease;
}

// 添加点击波纹效果
.click-gradient {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.4) 0%, rgba(180,180,255,0.2) 40%, rgba(100,100,255,0.1) 70%, rgba(50,50,255,0) 100%);
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  pointer-events: none;
  z-index: 4;
}

.glass-component.clicked .click-gradient {
  animation: gradient-ripple 0.6s ease-out;
}

@keyframes gradient-ripple {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(3);
    opacity: 0;
  }
}

.glass-component {
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform;
}

.remember-label {
  display: flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  cursor: pointer;
  
  .glass-checkbox {
    margin-right: 8px;
    accent-color: rgba(255, 255, 255, 0.6);
  }
}

.glass-button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
  position: relative;
  overflow: hidden;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  }

  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  margin-top: 1.5rem;
  text-align: center;
  
  .switch-form {
    color: rgba(255, 255, 255, 0.8);
    cursor: pointer;
    margin-bottom: 0.5rem;
    transition: color 0.3s ease;
    
    &:hover {
      color: #fff;
    }
  }
  
  .forgot-password {
    color: rgba(255, 255, 255, 0.6);
    font-size: 0.9rem;
    cursor: pointer;
    transition: color 0.3s ease;
    
    &:hover {
      color: rgba(255, 255, 255, 0.8);
    }
  }
}

// 添加点击波纹效果
.click-gradient {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.4) 0%, rgba(180,180,255,0.2) 40%, rgba(100,100,255,0.1) 70%, rgba(50,50,255,0) 100%);
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  pointer-events: none;
  z-index: 4;
}

.glass-component.clicked .click-gradient {
  animation: gradient-ripple 0.6s ease-out;
}

@keyframes gradient-ripple {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(3);
    opacity: 0;
  }
}

.glass-component {
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform;
}

// 响应式设计
@media (max-width: 480px) {
  .login-card {
    width: 90vw;
    max-width: 350px;
  }
  
  .glass-content {
    padding: 1.5rem;
  }
  
  .login-title {
    font-size: 1.5rem;
  }
}
</style>