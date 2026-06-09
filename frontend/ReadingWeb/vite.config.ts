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
          isCustomElement: (tag) => tag === 'emoji-picker-element',
        },
      },
    }),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    // 默认端口，可以不写
    port: 5173,
    proxy: {
      // 这里的 '/api' 要跟你 src/utils/request.ts 里的 baseURL 对应
      '/api': {
        // 代理到后端（本地后端通常运行在 8080）
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 前端请求 /api/...，转发给后端时去掉 /api 前缀
        // 即 /api/auth/login -> /auth/login
        rewrite: (path) => path.replace(/^\/api/, ''),
        configure: (proxy) => {
          proxy.on('proxyRes', (proxyRes) => {
            const contentType = proxyRes.headers['content-type']
            if (contentType && String(contentType).includes('text/event-stream')) {
              proxyRes.headers['cache-control'] = 'no-cache'
              proxyRes.headers['x-accel-buffering'] = 'no'
            }
          })
        },
      },
    },
  },
})
