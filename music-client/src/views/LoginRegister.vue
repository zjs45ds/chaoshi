<template>
  <div class="login-container">
    <!-- 背景切换按钮 -->
    <div v-if="backgroundImages.length > 1" class="background-controls">
      <button class="bg-toggle-btn" @click="switchBackground" title="切换背景">
        🎨
      </button>
    </div>
    
    <!-- 预设背景图片 -->
    <div class="image-background" 
         :style="{ backgroundImage: `url(${backgroundImages[currentBackgroundIndex]})` }"
         :key="currentBackgroundIndex">
    </div>
    
    <!-- 备用渐变背景 -->
    <div v-if="imageLoadError" class="fallback-background"></div>
    
    
    <!-- 背景加载指示器 -->
    <div v-if="isBackgroundLoading" class="background-loading">
      <div class="loading-spinner"></div>
      <p>背景加载中...</p>
    </div>

    <!-- 登录/注册卡片 -->
    <div class="glass-component login-card">
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
                autocomplete="username"
                required
              >
              <div v-if="loginErrors.username" class="error-message">{{ loginErrors.username }}</div>
            </div>
            <div class="form-group password-group">
              <input 
                :type="showLoginPassword ? 'text' : 'password'" 
                placeholder="密码" 
                class="glass-input password-input"
                :class="{ 'error': loginErrors.password }"
                v-model="loginForm.password"
                @blur="validateLoginField('password')"
                @input="clearLoginError('password')"
                autocomplete="current-password"
                required
              >
              <button 
                type="button" 
                class="password-toggle" 
                @click="toggleLoginPasswordVisibility"
                title="显示/隐藏密码"
              >
                <img v-if="showLoginPassword" src="@/assets/密码可见.png" alt="密码可见" width="20" height="20">
                <img v-else src="@/assets/密码不可见.png" alt="密码不可见" width="20" height="20">
              </button>
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
                autocomplete="username"
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
                autocomplete="email"
                required
              >
              <div v-if="registerErrors.email" class="error-message">{{ registerErrors.email }}</div>
            </div>
            <div class="form-group password-group">
              <input 
                :type="showRegisterPassword ? 'text' : 'password'" 
                placeholder="密码" 
                class="glass-input password-input"
                :class="{ 'error': registerErrors.password }"
                v-model="registerForm.password"
                @blur="validateRegisterField('password')"
                @input="clearRegisterError('password')"
                autocomplete="new-password"
                required
              >
              <button 
                type="button" 
                class="password-toggle" 
                @click="toggleRegisterPasswordVisibility"
                title="显示/隐藏密码"
              >
                <img v-if="showRegisterPassword" src="@/assets/密码可见.png" alt="密码可见" width="20" height="20">
                <img v-else src="@/assets/密码不可见.png" alt="密码不可见" width="20" height="20">
              </button>
              <div v-if="registerErrors.password" class="error-message">{{ registerErrors.password }}</div>
            </div>
            <div class="form-group password-group">
              <input 
                :type="showRegisterConfirmPassword ? 'text' : 'password'" 
                placeholder="确认密码" 
                class="glass-input password-input"
                :class="{ 'error': registerErrors.confirmPassword }"
                v-model="registerForm.confirmPassword"
                @blur="validateRegisterField('confirmPassword')"
                @input="clearRegisterError('confirmPassword')"
                autocomplete="new-password"
                required
              >
              <button 
                type="button" 
                class="password-toggle" 
                @click="toggleRegisterConfirmPasswordVisibility"
                title="显示/隐藏密码"
              >
                <img v-if="showRegisterConfirmPassword" src="@/assets/密码可见.png" alt="密码可见" width="20" height="20">
                <img v-else src="@/assets/密码不可见.png" alt="密码不可见" width="20" height="20">
              </button>
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
                autocomplete="tel"
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
import { login, register } from '@/api/user.js'
import { updateUserInfo } from '@/utils/userStore.js'

export default {
  name: 'LiquidGlass',
  setup() {
    const router = useRouter()
    const isRegisterMode = ref(false)
    
    // 背景相关
    const currentBackgroundIndex = ref(0)
    const isBackgroundLoading = ref(false)
    const imageLoadError = ref(false)
    const backgroundImages = [
      'http://localhost:9000/chaoshi/背景/侧脸.png',
      'http://localhost:9000/chaoshi/背景/背景1.png',
      'http://localhost:9000/chaoshi/背景/背景2.png',
      'http://localhost:9000/chaoshi/背景/背景3.png',
      'http://localhost:9000/chaoshi/背景/背景4.png',
      'http://localhost:9000/chaoshi/背景/背景5.png',
      'http://localhost:9000/chaoshi/背景/双马尾小猫.png',
      'http://localhost:9000/chaoshi/背景/背景.jpg'
    ]
    
    const showLoginPassword = ref(false)
    const showRegisterPassword = ref(false)
    const showRegisterConfirmPassword = ref(false)
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
      loginForm.value = { username: '', password: '', remember: false }
      registerForm.value = { username: '', email: '', password: '', confirmPassword: '', phone: '' }
      loginErrors.value = { username: '', password: '' }
      registerErrors.value = { username: '', email: '', password: '', confirmPassword: '', phone: '' }
      showLoginPassword.value = false
      showRegisterPassword.value = false
      showRegisterConfirmPassword.value = false
    }

    // 切换密码可见性
    const toggleLoginPasswordVisibility = () => {
      showLoginPassword.value = !showLoginPassword.value
    }

    const toggleRegisterPasswordVisibility = () => {
      showRegisterPassword.value = !showRegisterPassword.value
    }

    const toggleRegisterConfirmPasswordVisibility = () => {
      showRegisterConfirmPassword.value = !showRegisterConfirmPassword.value
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
          } else if (form.username.trim().length < 1) {
            errors.username = '用户名至少需要1个字符'
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
      
      try {
        const response = await login({
          username: loginForm.value.username,
          password: loginForm.value.password
        })
        
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
        
        const storage = loginForm.value.remember ? localStorage : sessionStorage
        
        // 确保同时设置localStorage和sessionStorage的isLogin标志
        // 这样路由守卫和其他组件都能正确识别登录状态
        if (loginForm.value.remember) {
          localStorage.setItem('isLogin', '1')
        }
        // 总是设置sessionStorage，确保当前会话中登录状态被正确识别
        sessionStorage.setItem('isLogin', '1')
        
        storage.setItem('token', userData.token)
        storage.setItem('userId', userData.id)
        storage.setItem('username', userData.username)
        storage.setItem('nickname', userData.nickname || userData.username)
        if (userData.avatar) {
          storage.setItem('avatar', userData.avatar)
        }
        
        // 更新全局用户状态
        updateUserInfo(userData)
        
        if (loginForm.value.remember) {
          localStorage.setItem('rememberedUsername', userData.username)
        } else {
          // 如果未选中记住我，清除可能存在的记住的用户名
          localStorage.removeItem('rememberedUsername')
        }
        
        // 清空表单
        loginForm.value = { username: '', password: '', remember: false }
        
        // 跳转页面（延迟一下让用户看到成功提示）
        setTimeout(() => {
          // 如果有 redirect 参数，跳转到原来想访问的页面
          const redirect = router.currentRoute.value.query.redirect
          if (redirect && redirect !== '/login') {
            router.push(redirect)
          } else {
            router.push('/')
          }
        }, 1500)
        
      } catch (error) {
        let errorMessage = '登录失败'
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
            errorMessage = '用户名或密码错误，请重新输入'
          } else if (status === 404) {
            errorMessage = 'API接口不存在，请检查后端配置'
          } else if (status === 500) {
            errorMessage = '服务器内部错误，请稍后重试'
          } else if (status >= 500) {
            errorMessage = '服务器暂时不可用，请稍后重试'
          } else {
            errorMessage = `请求失败 (${status})，请稍后重试`
          }
        } else if (error.message) {
          errorMessage = '登录失败 ' + error.message
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
      
      try {
        // 调用后端API进行注册
        const response = await register({
          username: registerForm.value.username,
          email: registerForm.value.email,
          password: registerForm.value.password,
          phone: registerForm.value.phone
        })
        
        // 注册成功 - 显示后端返回的成功消息（如果有的话）
        const successMessage = response.message || '注册成功！请使用您的账户登录'
        
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
        
        // 获取错误消息（优先使用后端返回的友好提示）
        let errorMessage = '注册失败'
        let shouldShowError = true
        
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.message === 'Network Error' || error.code === 'ECONNABORTED' || error.code === 'ECONNREFUSED') {
          // 网络错误已在httpUtils.js中处理
          shouldShowError = false
        } else if (error.response) {
          const status = error.response.status
          if (status === 400) {
            errorMessage = '输入信息有误，请检查后重试'
          } else if (status === 409) {
            errorMessage = '用户名或邮箱已存在，请更换后重试'
          } else if (status === 500) {
            errorMessage = '服务器内部错误，请稍后重试'
          } else if (status >= 500) {
            errorMessage = '服务器暂时不可用，请稍后重试'
          } else {
            errorMessage = `注册失败 (${status})，请稍后重试`
          }
        } else if (error.message) {
          errorMessage = '注册失败 ' + error.message
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

    // 图片加载错误处理
    const handleImageError = () => {
      imageLoadError.value = true
    }

    // 背景切换函数
    const switchBackground = () => {
      isBackgroundLoading.value = true
      imageLoadError.value = false
      currentBackgroundIndex.value = (currentBackgroundIndex.value + 1) % backgroundImages.length
      
      // 仅保存登录页面的背景选择，避免影响全局背景
      localStorage.setItem('loginPageBackgroundIndex', currentBackgroundIndex.value.toString())
      
      // 模拟加载时间
      setTimeout(() => {
        isBackgroundLoading.value = false
      }, 500)
    }

    // 初始化时加载记住的用户名和背景
    onMounted(async () => {
      try {
        // 加载记住的用户名
        const rememberedUsername = localStorage.getItem('rememberedUsername')
        if (rememberedUsername) {
          loginForm.value.username = rememberedUsername
          loginForm.value.remember = true
        }
        
        // 加载保存的背景选择
        const savedIndex = localStorage.getItem('loginPageBackgroundIndex')
        if (savedIndex !== null) {
          const index = parseInt(savedIndex)
          if (index >= 0 && index < backgroundImages.length) {
            currentBackgroundIndex.value = index
          }
        }
        
      } catch (error) {
        // 初始化错误处理
      }
    })

    return {
      isRegisterMode,
      loginForm,
      registerForm,
      loginErrors,
      registerErrors,
      showLoginPassword,
      showRegisterPassword,
      showRegisterConfirmPassword,
      toggleMode,
      toggleLoginPasswordVisibility,
      toggleRegisterPasswordVisibility,
      toggleRegisterConfirmPasswordVisibility,
      handleLogin,
      handleRegister,
      validateLoginField,
      validateRegisterField,
      clearLoginError,
      clearRegisterError,
      currentBackgroundIndex,
      backgroundImages,
      isBackgroundLoading,
      imageLoadError,
      handleImageError,
      switchBackground
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


/* 背景控制面板 */
.background-controls {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1001;
}

.bg-toggle-btn {
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
  border: 1px solid rgba(255, 255, 255, 0.2);

  &:hover {
    background: rgba(255, 255, 255, 0.25);
    transform: scale(1.1);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
  }

  &:active {
    transform: scale(0.95);
  }
}

/* 图片背景 */
.image-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: -1;
  transition: all 0.5s ease-in-out;
}

/* 备用渐变背景 */
.fallback-background {
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
  z-index: -2;
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

/* 背景加载指示器 */
.background-loading {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 20px 30px;
  border-radius: 10px;
  text-align: center;
  z-index: 1002;
  backdrop-filter: blur(10px);
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px;
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
      left: 2px;
      color: white;
      font-size: 12px;
      font-weight: bold;
    }
  }
}

.password-group {
  position: relative;
}

.password-input {
  padding-right: 50px;
}

.password-toggle {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.7;
  transition: opacity 0.3s ease;

  &:hover {
    opacity: 1;
  }

  img {
    width: 20px;
    height: 20px;
  }
}

.form-footer {
  text-align: center;
  margin-top: 1.5rem;
}

.switch-form {
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: color 0.3s ease;
  margin: 0;

  &:hover {
    color: rgba(255, 255, 255, 1);
  }
}
</style>
