// composables/useTitle.js
import { watchEffect } from 'vue'

export function useTitle(newTitle) {
  watchEffect(() => {
    document.title = newTitle.value
  })
}
