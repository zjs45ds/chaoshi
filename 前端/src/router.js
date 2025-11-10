import { createRouter, createWebHistory, isNavigationFailure, NavigationFailureType } from 'vue-router'
import { handleError } from '@/utils/errorHandler.js'

// ====== 核心页面（同步加载，首次访问必需） ======
import Home from './views/Home.vue'

// 音乐相关页面 - 高优先级预加载
const Artist = () => import(/* webpackChunkName: "music" */ './views/Artist.vue')
const Album = () => import(/* webpackChunkName: "music" */ './views/Album.vue')
const Toplist = () => import(/* webpackChunkName: "music" */ './views/Toplist.vue')
const MV = () => import(/* webpackChunkName: "music" */ './views/MV.vue')

// 详情页面 - 按需加载
const PlaylistDetail = () => import(/* webpackChunkName: "details" */ './views/PlaylistDetail.vue')
const ArtistDetail = () => import(/* webpackChunkName: "details" */ './views/ArtistDetail.vue')
const AlbumDetail = () => import(/* webpackChunkName: "details" */ './views/AlbumDetail.vue')
const SongDetail = () => import(/* webpackChunkName: "details" */ './views/SongDetail.vue')
const ToplistDetail = () => import(/* webpackChunkName: "details" */ './views/ToplistDetail.vue')


// 用户相关页面 - 独立分组
const User = () => import(/* webpackChunkName: "user" */ './views/User.vue')
const UserMusic = () => import(/* webpackChunkName: "user" */ './views/UserMusic.vue')
const Profile = () => import(/* webpackChunkName: "user" */ './views/Profile.vue')
const LoginRegister = () => import(/* webpackChunkName: "auth" */ './views/LoginRegister.vue')

// 功能页面 - 独立加载
const OpenPlatform = () => import(/* webpackChunkName: "platform" */ './views/OpenPlatform.vue')
const RecordFullScreenPlayer = () => import(/* webpackChunkName: "player" */ './views/RecordFullScreenPlayer.vue')
const Game = () => import(/* webpackChunkName: "game" */ './views/Game.vue')
// 临时改为同步导入解决模块加载问题
import Xue0717 from './views/0717.vue'

// ====== 路由配置 ======
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: '首页',
      keepAlive: true,
      requireAuth: false,
      preload: true
    }
  },
  {
    path: '/artist',
    name: 'Artist',
    component: Artist,
    meta: {
      title: '歌手',
      keepAlive: true,
      requireAuth: false,
      preload: true
    }
  },
  {
    path: '/artist/:id',
    name: 'ArtistDetail',
    component: ArtistDetail,
    meta: {
      title: '歌手详情',
      keepAlive: false,
      requireAuth: false
    }
  },
  {
    path: '/toplist',
    name: 'Toplist',
    component: Toplist,
    meta: {
      title: '排行榜',
      keepAlive: true,
      requireAuth: false,
      preload: true
    }
  },
  {
    path: '/toplist/:id',
    name: 'ToplistDetail',
    component: ToplistDetail,
    meta: {
      title: '排行榜详情',
      keepAlive: false,
      requireAuth: false
    }
  },
  {
    path: '/album',
    name: 'Album',
    component: Album,
    meta: {
      title: '专辑',
      keepAlive: true,
      requireAuth: false,
      preload: true
    }
  },
  {
    path: '/album/:id',
    name: 'AlbumDetail',
    component: AlbumDetail,
    meta: {
      title: '专辑详情',
      keepAlive: false,
      requireAuth: false
    }
  },
  {
    path: '/mv',
    name: 'MV',
    component: MV,
    meta: {
      title: 'MV',
      keepAlive: true,
      requireAuth: false
    }
  },
  {
    path: '/mv/:id',
    name: 'MvDetail',
    component: () => import(/* webpackChunkName: "details" */ './views/MvDetail.vue'),
    meta: {
      title: 'MV详情',
      keepAlive: false,
      requireAuth: false
    }
  },
  {
    path: '/song/:id',
    name: 'SongDetail',
    component: SongDetail,
    meta: {
      title: '歌曲详情',
      keepAlive: false,
      requireAuth: false
    }
  },
  {
    path: '/playlist/:id',
    name: 'PlaylistDetail',
    component: PlaylistDetail,
    meta: {
      title: '歌单详情',
      keepAlive: false,
      requireAuth: false
    }
  },
  {
    path: '/user',
    name: 'User',
    component: User,
    meta: {
      title: '用户',
      keepAlive: false,
      requireAuth: true
    }
  },
  {
    path: '/my-music',
    name: 'UserMusic',
    component: UserMusic,
    meta: {
      title: '我的音乐',
      keepAlive: true,
      requireAuth: false // 改为false，允许未登录用户访问，在组件内部处理登录检查
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: {
      title: '个人资料',
      keepAlive: false,
      requireAuth: true
    }
  },
  {
    path: '/login',
    name: 'LoginRegister',
    component: LoginRegister,
    meta: {
      title: '登录注册',
      keepAlive: false,
      requireAuth: false,
      hideNav: true
    }
  },
  {
    path: '/open-platform',
    name: 'OpenPlatform',
    component: OpenPlatform,
    meta: {
      title: '开放平台',
      keepAlive: false,
      requireAuth: false,
      preload: true
    }
  },
  {
    path: '/record-fullscreen',
    name: 'RecordFullScreenPlayer',
    component: RecordFullScreenPlayer,
    meta: {
      title: '全屏播放器',
      keepAlive: false,
      requireAuth: false,
      hideNav: true
    }
  },
  {
    path: '/game',
    name: 'Game',
    component: Game,
    meta: {
      title: '游戏',
      keepAlive: false,
      requireAuth: false
    }
  },
  {
    path: '/0717',
    name: '0717',
    component: Xue0717,
    meta: {
      title: '0717',
      keepAlive: false,
      requireAuth: false,
      preload: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import(/* webpackChunkName: "error" */ './views/NotFound.vue'),
    meta: {
      title: '页面未找到',
      keepAlive: false,
      requireAuth: false
    }
  }
]

// ====== 创建路由实例 ======
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的滚动位置，则恢复
    if (savedPosition) {
      return savedPosition
    }
    // 如果是锚点链接，滚动到对应元素
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth'
      }
    }
    // 默认滚动到顶部
    return { top: 0 }
  }
})

// ====== 修复路由导航警告和导航取消问题 ======
// 保存原始的 push 方法
const originalPush = router.push
router.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    // 忽略导航重复和导航取消的警告
    if (isNavigationFailure(err, NavigationFailureType.duplicated) || 
        isNavigationFailure(err, NavigationFailureType.cancelled)) {
      return Promise.resolve()
    }
    return Promise.reject(err)
  })
}

// 保存原始的 replace 方法
const originalReplace = router.replace
router.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    if (isNavigationFailure(err, NavigationFailureType.duplicated) || 
        isNavigationFailure(err, NavigationFailureType.cancelled)) {
      return Promise.resolve()
    }
    return Promise.reject(err)
  })
}

// ====== 路由守卫 ======
// 全局前置守卫
router.beforeEach((to, from, next) => {
  try {
    // 设置页面标题
    if (to.meta?.title) {
      document.title = `${to.meta.title} - 潮石音乐`
    }

    // 检查登录状态
    const isLogin = localStorage.getItem('isLogin') === '1'
    const requireAuth = to.meta?.requireAuth

    if (requireAuth && !isLogin) {
      // 需要登录但未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 如果已登录且访问登录页，重定向到首页
    if (isLogin && to.path === '/login') {
      // 避免重复导航到首页
      if (from.path !== '/') {
        next('/')
        return
      } else {
        next(false) // 阻止导航
        return
      }
    }

    next()
  } catch (error) {
    handleError(error, { route: to.path, source: 'router-guard' })
    next(false) // 阻止导航
  }
})

// 全局后置钩子
router.afterEach((to, from, failure) => {
  if (failure) {
    handleError(failure, { route: to.path, source: 'router-navigation' })
  }
  
  // 预加载相关页面
  if (to.meta?.preload) {
    preloadRelatedRoutes(to)
  }
})

// ====== 预加载相关路由 ======
function preloadRelatedRoutes(route) {
  const preloadMap = {
    'Home': ['Artist', 'Album', 'Toplist', 'OpenPlatform', '0717'],
    'Artist': ['ArtistDetail'],
    'Album': ['AlbumDetail'],
    'Toplist': ['ToplistDetail']
  }
  
  const routesToPreload = preloadMap[route.name]
  if (routesToPreload) {
    routesToPreload.forEach(routeName => {
      const routeConfig = routes.find(r => r.name === routeName)
      if (routeConfig?.component && typeof routeConfig.component === 'function') {
        // 延迟预加载，避免影响当前页面渲染
        setTimeout(() => {
          routeConfig.component().catch(error => {
            console.warn(`Failed to preload route ${routeName}:`, error)
          })
        }, 2000)
      }
    })
  }
}

// ====== 导出路由实例 ======
export default router

// 导出路由配置，供其他模块使用
export { routes }
