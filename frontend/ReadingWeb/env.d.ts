/// <reference types="vite/client" />
declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
declare module '@/stores/user' {
  export const useUserStore: any
}
declare module '@/stores/useTitle' {
  export const useTitle: any
}
