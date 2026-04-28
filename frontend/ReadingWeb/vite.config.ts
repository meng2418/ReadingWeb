import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: {
        compilerOptions: {
          isCustomElement: (tag) => tag === 'emoji-picker-element'
        }
      }
    }),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    // 默认端口，可以不写
    port: 5173,
    proxy: {
      // 这里的 '/api' 要跟你 src/utils/request.ts 里的 baseURL 对应
      '/api': {
        // 🔴 关键点：告诉前端，请求要转发给谁
        // 如果后端在自己电脑跑，就是 localhost:8080
        target: 'http://localhost:8080',

        // 允许跨域
        changeOrigin: true,

        // 路径重写（非常重要！）
        // 解释：前端发 /api/auth/login -> 后端收到 /auth/login
        // 如果后端接口本身就有 /api 前缀，就把下面这行 rewrite 注释掉
        //rewrite: (path) => path.replace(/^\/api/, '')
        //target: 'https://m1.apifoxmock.com',
        rewrite: (path) => path.replace(/^\/api/, '/m1/7605134-7343879-default')
      }
    }
  }
})
