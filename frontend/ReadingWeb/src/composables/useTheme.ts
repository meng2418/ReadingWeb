import { ref, computed, watch } from 'vue'

// 主题配置
export const themes = [
  {
    value: 'classic-white',
    label: '经典白',
    colors: {
      '--primary-green': '#007c27',
      '--secondary-green': '#1ad6a1',
      '--third-green': '#00a86b',
      '--card-bg': '#ffffff',
      '--text-main': '#1f2937',
      '--text-light': '#9ca3af',
      '--bg-gray': '#f3f4f6',
      '--border-color': '#e5e7eb',
      '--shadow': '0 4px 12px rgba(0, 0, 0, 0.05)',
    },
    previewBg: '#ffffff',
  },
  {
    value: 'silent-black',
    label: '沉静黑',
    colors: {
      '--primary-green': '#6b7280', // 灰色按钮背景
      '--secondary-green': '#4b5563', // 深一点的灰色（hover状态）
      '--third-green': '#9ca3af', // 浅灰色
      '--card-bg': '#1e293b', // 稍微亮一点的深色背景，更柔和
      '--text-main': '#e2e8f0', // 柔和的浅色文字
      '--text-light': '#94a3b8', // 柔和的灰色文字
      '--bg-gray': '#0f172a', // 深色背景，更柔和
      '--border-color': '#334155', // 深色边框
      '--shadow': '0 4px 12px rgba(0, 0, 0, 0.25)',
    },
    previewBg: '#1e293b',
  },
  {
    value: 'tranquil-green',
    label: '恬静绿',
    colors: {
      '--primary-green': '#2d5a3d',
      '--secondary-green': '#4a7c59',
      '--third-green': '#6ba876',
      '--card-bg': '#f0f5f3',
      '--text-main': '#1a3a2a',
      '--text-light': '#5c7a68',
      '--bg-gray': '#e1e8e4',
      '--border-color': '#4a7c59', // 绿色边框，更有存在感
      '--shadow': '0 4px 12px rgba(45, 90, 61, 0.1)',
    },
    previewBg: 'linear-gradient(135deg, #f0f5f3 0%, #d5e6db 100%)',
  },
  {
    value: 'gay-purple',
    label: '基佬紫',
    colors: {
      '--primary-green': '#7c3aed',
      '--secondary-green': '#a78bfa',
      '--third-green': '#c4b5fd',
      '--card-bg': '#f5f3ff',
      '--text-main': '#4c1d95',
      '--text-light': '#8b5cf6',
      '--bg-gray': '#ede9fe',
      '--border-color': '#a78bfa', // 紫色边框，更有存在感
      '--shadow': '0 4px 12px rgba(124, 58, 237, 0.1)',
    },
    previewBg: 'linear-gradient(135deg, #f5f3ff 0%, #ddd6fe 100%)',
  },
  {
    value: 'sweet-pink',
    label: '甜美粉',
    colors: {
      '--primary-green': '#db2777',
      '--secondary-green': '#f472b6',
      '--third-green': '#fbcfe8',
      '--card-bg': '#fff1f2',
      '--text-main': '#881337',
      '--text-light': '#be123c',
      '--bg-gray': '#ffe4e6',
      '--border-color': '#f472b6', // 粉色边框，更有存在感
      '--shadow': '0 4px 12px rgba(219, 39, 119, 0.1)',
    },
    previewBg: 'linear-gradient(135deg, #fff1f2 0%, #fecdd3 100%)',
  },
]

export const useTheme = () => {
  // 从 localStorage 读取保存的主题，如果没有则使用默认主题
  const activeThemeValue = ref(localStorage.getItem('theme') || 'classic-white')
  const previewThemeValue = ref(activeThemeValue.value)

  // 计算当前主题的 CSS 变量
  const cssVars = computed(() => {
    const current = themes.find((t) => t.value === previewThemeValue.value)
    return current ? current.colors : {}
  })

  // 监听主题变化，应用到整个页面
  watch(
    () => previewThemeValue.value,
    (newTheme) => {
      const theme = themes.find((t) => t.value === newTheme)
      if (theme) {
        // 将 CSS 变量应用到 document.documentElement
        Object.entries(theme.colors).forEach(([key, value]) => {
          document.documentElement.style.setProperty(key, value as string)
        })
      }
    },
    { immediate: true }
  )

  // 保存主题
  const saveTheme = () => {
    activeThemeValue.value = previewThemeValue.value
    localStorage.setItem('theme', activeThemeValue.value)
  }

  // 重置预览主题为当前生效的主题
  const resetPreview = () => {
    previewThemeValue.value = activeThemeValue.value
  }

  return {
    themes,
    activeThemeValue,
    previewThemeValue,
    cssVars,
    saveTheme,
    resetPreview,
  }
}
