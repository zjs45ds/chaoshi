import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { resolve } from 'path'
import autoprefixer from 'autoprefixer'

export default defineConfig(({ command, mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), '')
  
  const isProduction = mode === 'production'
  const isDevelopment = mode === 'development'

  return {
    plugins: [
      vue({
        // Vue SFC 编译优化
        template: {
          compilerOptions: {
            // 在生产环境中移除注释
            comments: !isProduction
          }
        }
      }),
      // 仅在开发环境中启用开发工具
      isDevelopment && vueDevTools(),
      vueJsx()
    ].filter(Boolean),
    
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        '@/types': fileURLToPath(new URL('./src/types', import.meta.url)),
        '@/utils': fileURLToPath(new URL('./src/utils', import.meta.url)),
        '@/api': fileURLToPath(new URL('./src/api', import.meta.url)),
        '@/components': fileURLToPath(new URL('./src/components', import.meta.url)),
        '@/views': fileURLToPath(new URL('./src/views', import.meta.url)),
        '@/assets': fileURLToPath(new URL('./src/assets', import.meta.url)),
        '@/constants': fileURLToPath(new URL('./src/constants', import.meta.url))
      }
    },

    // 开发服务器配置
    // npm run dev (development) → 3000端口（发布用，有代理）
    // npm run start (production) → 3001端口（开发测试用，无代理）
    server: {
      port: isDevelopment ? 3000 : 3001,
      open: true,
      cors: true,
      // 隐藏服务器启动信息
      logLevel: 'warn',
      // 代理配置（仅在development模式下启用，即3000端口）
      proxy: isDevelopment ? {
        '/api': {
          target: 'http://localhost:8081',
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path,
          // 配置代理以支持文件上传
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              // 对于文件上传请求，保持原始请求头
              if (req.headers['content-type']?.includes('multipart/form-data')) {
                // 移除content-length，让代理自动计算
                proxyReq.removeHeader('content-length')
              }
            })
          }
        }
      } : undefined,
      // HMR 配置
      hmr: {
        overlay: true,
        // 隐藏 HMR 连接信息
        clientLogLevel: 'warn'
      }
    },

    // 预览服务器配置
    preview: {
      port: 3000,
      open: true
    },

    // 构建配置
    build: {
      target: 'es2015',
      outDir: 'dist',
      assetsDir: 'assets',
      sourcemap: isDevelopment,
      minify: isProduction ? 'terser' : false,
      
      // Terser 压缩配置
      terserOptions: {
        compress: {
          drop_console: isProduction,
          drop_debugger: isProduction,
          pure_funcs: isProduction ? ['console.log'] : []
        },
        format: {
          comments: false
        }
      },

      // 打包大小分析
      reportCompressedSize: true,
      chunkSizeWarningLimit: 1000,

      // Rollup 配置
      rollupOptions: {
        input: {
          main: resolve(__dirname, 'index.html')
        },
        output: {
          // 手动配置 chunk 分割
          manualChunks: {
            // Vue 核心库
            'vue-vendor': ['vue', 'vue-router'],
            // UI 组件库
            'ui-vendor': ['element-plus'],
            // HTTP 库
            'http-vendor': ['axios'],
            // 工具库
            'utils': [
              './src/utils/httpUtils.js',
              './src/utils/imageUtils.js', 
              './src/utils/errorHandler.js'
            ]
          },
          // 文件命名规则
          chunkFileNames: (chunkInfo) => {
            const facadeModuleId = chunkInfo.facadeModuleId
              ? chunkInfo.facadeModuleId.split('/').pop().replace(/\.[^.]*$/, '')
              : 'chunk'
            return `js/${facadeModuleId}-[hash].js`
          },
          entryFileNames: 'js/[name]-[hash].js',
          assetFileNames: (assetInfo) => {
            const info = assetInfo.name.split('.')
            const ext = info[info.length - 1]
            if (/\.(mp4|webm|ogg|mp3|wav|flac|aac)$/i.test(assetInfo.name)) {
              return `media/[name]-[hash].${ext}`
            } else if (/\.(png|jpe?g|gif|svg|webp|ico)$/i.test(assetInfo.name)) {
              return `images/[name]-[hash].${ext}`
            } else if (/\.(woff2?|eot|ttf|otf)$/i.test(assetInfo.name)) {
              return `fonts/[name]-[hash].${ext}`
            }
            return `assets/[name]-[hash].${ext}`
          }
        },
        // 外部化依赖（如果需要CDN加载）
        external: isProduction ? [] : []
      }
    },

    // CSS 配置
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/assets/styles/variables.scss" as *;`,
          charset: false
        }
      },
      devSourcemap: isDevelopment,
      // PostCSS 配置
      postcss: {
        plugins: [
          // 自动添加浏览器前缀
          autoprefixer({
            overrideBrowserslist: [
              '> 1%',
              'last 2 versions',
              'not dead',
              'not ie 11'
            ]
          })
        ]
      }
    },

    // 优化配置
    optimizeDeps: {
      include: [
        'vue',
        'vue-router',
        'element-plus',
        'axios',
        '@tsparticles/vue3'
      ],
      exclude: [
        'vue-demi'
      ]
    },

    // 全局常量定义
    define: {
      __APP_VERSION__: JSON.stringify(process.env.npm_package_version),
      __BUILD_TIME__: JSON.stringify(new Date().toISOString()),
      // 解决 Vue 在生产环境中的警告
      __VUE_OPTIONS_API__: true,
      __VUE_PROD_DEVTOOLS__: false
    },

    // 环境变量前缀
    envPrefix: 'VITE_',

    // 实验性功能
    experimental: {
      renderBuiltUrl: (filename, { hostType }) => {
        if (hostType === 'js') {
          return `/${filename}`
        } else {
          return { relative: true }
        }
      }
    },

    // Worker 配置
    worker: {
      format: 'es'
    },

    // 性能监控
    esbuild: {
      drop: isProduction ? ['console', 'debugger'] : []
    }
  }
})
