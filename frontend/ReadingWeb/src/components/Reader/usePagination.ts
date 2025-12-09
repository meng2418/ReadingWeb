import { ref, watch, onUnmounted, nextTick } from 'vue'

export interface PageMapItem {
  pageIndex: number
  pIndex: number
  charOffset: number
}

export function usePagination(options: {
  paragraphs: () => string[]
  typography: () => { fontSize: number; lineHeight: number; fontFamily?: string; padding?: string }
  containerRef: () => HTMLElement | null
  viewportHeight: () => number
  debounceMs?: number
}) {
  const pages = ref<PageMapItem[]>([])
  const measuringDiv = ref<HTMLElement | null>(null)
  let cancelled = false

  const createMeasuringDiv = () => {
    if (measuringDiv.value) return measuringDiv.value
    const d = document.createElement('div')
    Object.assign(d.style, {
      position: 'absolute',
      left: '-9999px',
      top: '0px',
      visibility: 'hidden',
      whiteSpace: 'normal',
      wordBreak: 'break-word',
      boxSizing: 'border-box',
    } as any)
    document.body.appendChild(d)
    measuringDiv.value = d
    return d
  }

  const destroyMeasuringDiv = () => {
    if (measuringDiv.value && measuringDiv.value.parentNode) {
      measuringDiv.value.parentNode.removeChild(measuringDiv.value)
      measuringDiv.value = null
    }
  }

  onUnmounted(() => {
    cancelled = true
    destroyMeasuringDiv()
  })

  // 将样式同步到测量容器（必须与真实阅读器一致）
  const syncStyles = () => {
    const d = createMeasuringDiv()
    const cont = options.containerRef()
    const tp = options.typography()
    const width = cont ? `${cont.clientWidth}px` : '800px'
    d.style.width = width
    d.style.fontSize = `${tp.fontSize}px`
    d.style.lineHeight = `${tp.lineHeight}`
    if (tp.fontFamily) d.style.fontFamily = tp.fontFamily
    if (tp.padding) d.style.padding = tp.padding
  }

  // 在段落 text 上二分查找最大可放字符数（startOffset 开始）
  const findMaxCharsForPage = (
    baseHtmlPrefix: string,
    paragraph: string,
    startOffset: number,
    availableHeight: number,
  ) => {
    const d = measuringDiv.value!
    let lo = startOffset
    let hi = paragraph.length
    let best = startOffset
    while (lo <= hi) {
      const mid = Math.floor((lo + hi) / 2)
      d.innerText = baseHtmlPrefix + paragraph.slice(startOffset, mid)
      if (d.scrollHeight <= availableHeight) {
        best = mid
        lo = mid + 1
      } else {
        hi = mid - 1
      }
    }
    return best
  }

  // 主计算函数：构造 pages 映射表
  const compute = async () => {
    cancelled = false
    pages.value = []
    const paras = options.paragraphs()
    if (!paras || paras.length === 0) return

    syncStyles()
    await nextTick()
    const d = createMeasuringDiv()
    const viewportH = options.viewportHeight()
    const totalParas = paras.length

    let curP = 0
    let curChar = 0
    let pageIndex = 1

    while (curP < totalParas && !cancelled) {
      // 记录当前页起始
      pages.value.push({ pageIndex, pIndex: curP, charOffset: curChar })

      // 填充直到溢出
      d.innerText = ''
      // 如果 curChar > 0, append remainder of curP
      if (curChar < paras[curP].length) {
        d.innerText += paras[curP].slice(curChar)
      }
      let p = curP + 1
      while (p < totalParas && d.scrollHeight <= viewportH) {
        // 追加换段（用 newline 保留段落间间距）
        d.innerText += '\n\n' + paras[p]
        if (d.scrollHeight > viewportH) break
        p++
      }

      if (d.scrollHeight <= viewportH) {
        // 到末尾都没溢出：所有后续内容都在当前页
        break
      }

      // 溢出发生在某个段落（可能是 curP 或 p）
      // 找到第一个导致溢出的段落索引
      let overflowP = curP
      // 重建并逐段追加以定位具体段落
      d.innerText = ''
      if (curChar < paras[curP].length) d.innerText += paras[curP].slice(curChar)
      let q = curP + 1
      while (q < totalParas && d.scrollHeight <= viewportH) {
        d.innerText += '\n\n' + paras[q]
        if (d.scrollHeight > viewportH) {
          overflowP = q
          break
        }
        q++
      }
      if (d.scrollHeight <= viewportH) {
        // 不应该到这里，但保险处理
        break
      }

      // 计算 base prefix 内容（所有在 overflowP 之前已完全加入的内容）
      let basePrefix = ''
      if (overflowP === curP) {
        basePrefix = ''
      } else {
        basePrefix = paras[curP].slice(curChar)
        for (let i = curP + 1; i < overflowP; i++) basePrefix += '\n\n' + paras[i]
        basePrefix += '\n\n' // 保持与二分查找时一致的连接
      }

      // 对 overflowP 段做二分查找
      const availableHeight = viewportH
      const charPos = findMaxCharsForPage(basePrefix, paras[overflowP], 0, availableHeight)

      // 下一页起点：如果 charPos == 0 => 下一页从 overflowP 开始第0字符；否则从 overflowP,charPos
      curP = overflowP
      curChar = charPos
      pageIndex++
      // 防无限循环保护
      if (pageIndex > 10000) break
    }

    // 释放测量节点（可保留以便下次快速重算）
    // destroyMeasuringDiv()
  }

  // 监听依赖变化（节流）
  let timer: any = null
  const scheduleCompute = () => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      compute().catch(() => {})
    }, options.debounceMs ?? 120)
  }

  watch([options.paragraphs, options.typography, options.containerRef], scheduleCompute, {
    immediate: true,
  })

  return {
    pages,
    recompute: compute,
    destroy: destroyMeasuringDiv,
  }
}
