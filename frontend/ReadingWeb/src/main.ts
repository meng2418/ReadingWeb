// main.ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 全局引入 global.css（关键）
import '@/assets/styles/global.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 在应用挂载前恢复用户会话
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
userStore.restoreSession()

app.mount('#app')
