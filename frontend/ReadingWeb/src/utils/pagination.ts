/**
 * 分页工具函数
 * 根据字体大小、行高和容器高度，将文本内容按行分割并分页
 */

export interface PageLine {
  text: string
  originalPIndex: number // 原始段落索引
  originalStart: number // 在原始段落中的起始位置
  originalEnd: number // 在原始段落中的结束位置
}

export interface PaginatedContent {
  pages: PageLine[][]
  totalPages: number
  linesPerPage: number
}

/**
 * 计算每页能显示的行数
 * @param containerHeight 容器可用高度（像素）
 * @param fontSize 字体大小（像素）
 * @param lineHeight 行高倍数
 * @param paddingTop 顶部内边距（像素）
 * @param paddingBottom 底部内边距（像素）
 * @returns 每页行数
 */
export function calculateLinesPerPage(
  containerHeight: number,
  fontSize: number,
  lineHeight: number,
  paddingTop: number = 0,
  paddingBottom: number = 0,
): number {
  const availableHeight = containerHeight - paddingTop - paddingBottom
  const lineHeightPx = fontSize * lineHeight
  const lines = Math.floor(availableHeight / lineHeightPx)
  return Math.max(1, lines) // 至少1行
}

/**
 * 将文本按行分割（考虑换行）
 * @param text 文本内容
 * @param containerWidth 容器宽度（像素）
 * @param fontSize 字体大小（像素）
 * @returns 行数组
 */
export function splitTextIntoLines(
  text: string,
  containerWidth: number,
  fontSize: number,
): string[] {
  // 使用 DOM 元素来精确测量文本宽度
  let measureElement: HTMLElement | null = null
  
  try {
    // 尝试获取或创建测量元素
    measureElement = document.getElementById('text-measure-element') as HTMLElement
    if (!measureElement) {
      measureElement = document.createElement('div')
      measureElement.id = 'text-measure-element'
      measureElement.style.position = 'absolute'
      measureElement.style.visibility = 'hidden'
      measureElement.style.whiteSpace = 'nowrap'
      measureElement.style.fontFamily = 'serif'
      measureElement.style.fontSize = `${fontSize}px`
      measureElement.style.padding = '0'
      measureElement.style.margin = '0'
      document.body.appendChild(measureElement)
    } else {
      measureElement.style.fontSize = `${fontSize}px`
    }
  } catch (e) {
    console.warn('无法创建测量元素，使用估算方法', e)
  }

  const lines: string[] = []
  
  // 如果无法使用 DOM 测量，使用字符数估算
  if (!measureElement) {
    // 中文字符平均宽度约为字体大小的 1.0 倍，英文约为 0.6 倍
    // 混合文本取平均值 0.8
    const avgCharWidth = fontSize * 0.8
    const avgCharsPerLine = Math.max(10, Math.floor(containerWidth / avgCharWidth))
    
    let currentLine = ''
    for (let i = 0; i < text.length; i++) {
      const char = text[i]
      currentLine += char
      
      if (char === '\n') {
        if (currentLine.trim()) {
          lines.push(currentLine.trim())
        }
        currentLine = ''
      } else if (currentLine.length >= avgCharsPerLine) {
        // 尝试在空格处断行
        const lastSpaceIndex = currentLine.lastIndexOf(' ')
        if (lastSpaceIndex > 0) {
          lines.push(currentLine.substring(0, lastSpaceIndex).trim())
          currentLine = currentLine.substring(lastSpaceIndex + 1)
        } else {
          lines.push(currentLine.trim())
          currentLine = ''
        }
      }
    }
    
    if (currentLine.trim()) {
      lines.push(currentLine.trim())
    }
    
    return lines.length > 0 ? lines : [text]
  }

  // 使用 DOM 元素精确测量
  const words = text.split(/(\s+)/)
  let currentLine = ''

  for (const word of words) {
    if (word === '\n') {
      if (currentLine.trim()) {
        lines.push(currentLine.trim())
      }
      currentLine = ''
      continue
    }

    const testLine = currentLine + word
    measureElement.textContent = testLine
    const width = measureElement.offsetWidth
    
    if (width > containerWidth && currentLine.trim()) {
      lines.push(currentLine.trim())
      currentLine = word
    } else {
      currentLine = testLine
    }
  }

  if (currentLine.trim()) {
    lines.push(currentLine.trim())
  }

  return lines.length > 0 ? lines : [text]
}

/**
 * 将内容分页
 * @param content 段落数组
 * @param containerHeight 容器高度（像素）
 * @param containerWidth 容器宽度（像素）
 * @param fontSize 字体大小（像素）
 * @param lineHeight 行高倍数
 * @param paddingTop 顶部内边距（像素）
 * @param paddingBottom 底部内边距（像素）
 * @returns 分页后的内容
 */
export function paginateContent(
  content: string[],
  containerHeight: number,
  containerWidth: number,
  fontSize: number,
  lineHeight: number,
  paddingTop: number = 0,
  paddingBottom: number = 0,
): PaginatedContent {
  // 计算每页行数
  const linesPerPage = calculateLinesPerPage(
    containerHeight,
    fontSize,
    lineHeight,
    paddingTop,
    paddingBottom,
  )

  // 将所有段落按行分割（段落可以跨页）
  const allLines: PageLine[] = []
  
  for (let pIndex = 0; pIndex < content.length; pIndex++) {
    const paragraphRaw = content[pIndex]
    if (!paragraphRaw || typeof paragraphRaw !== 'string') continue // 跳过空段落
    
    const paragraph: string = paragraphRaw // 类型断言，确保 paragraph 是 string
    const lines = splitTextIntoLines(paragraph, containerWidth, fontSize)
    
    // 使用更简单的方法：直接按行分割，记录字符位置
    let charOffset = 0
    for (let lineIndex = 0; lineIndex < lines.length; lineIndex++) {
      const lineText = lines[lineIndex]
      if (!lineText) continue
      
      const line = lineText.trim() // 去除首尾空格用于显示
      if (!line) continue // 跳过空行
      
      // 在原始段落中找到这一行对应的位置
      // 从 charOffset 开始查找，匹配这一行的内容
      const paraLength = paragraph.length
      let start = charOffset
      // 跳过前导空白字符
      const paraChar = paragraph[start]
      while (start < paraLength && paraChar && /\s/.test(paraChar)) {
        start++
      }
      
      // 计算这一行在原始段落中的结束位置
      // 简单方法：从 start 开始，累计非空白字符直到达到 line 的长度
      let end = start
      const lineChars = line.replace(/\s+/g, '') || '' // 去除所有空格用于匹配
      let matchedChars = 0
      
      while (end < paraLength && matchedChars < lineChars.length) {
        const endChar = paragraph[end]
        if (endChar && !/\s/.test(endChar)) {
          matchedChars++
        }
        end++
      }
      
      // 如果这是最后一行，确保包含到段落末尾
      if (lineIndex === lines.length - 1) {
        end = paraLength
      }
      
      allLines.push({
        text: line, // 显示文本（已去除首尾空格）
        originalPIndex: pIndex,
        originalStart: start,
        originalEnd: end,
      })
      
      charOffset = end
    }
  }

  // 不在这里分页，只返回所有行，让调用者根据实际需求分页
  // 返回一个包含所有行的"页"，调用者会重新分页
  return {
    pages: allLines.length > 0 ? [allLines] : [[]], // 临时将所有行放在一页中
    totalPages: 1, // 临时值，会被调用者重新计算
    linesPerPage,
  }
}

