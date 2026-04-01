/**
 * 计算文本范围索引的 Composable
 * 用于精确计算被标注文本在原文中的起始和结束字符索引（包含空格、标点）
 */

/**
 * 计算被选中文本在章节内容中的范围索引
 * @param selectedText - 被选中的文本
 * @param fullContent - 完整章节内容
 * @returns { rangeStart, rangeEnd } - 起始和结束索引，如果未找到则返回 -1
 */
export function calculateTextRangeIndex(
  selectedText: string,
  fullContent: string,
): { rangeStart: number; rangeEnd: number } {
  if (!selectedText || !fullContent) {
    return { rangeStart: -1, rangeEnd: -1 }
  }

  // 标准化文本（移除多余空格但保留单个空格）
  const normalizedSelected = selectedText.trim()
  const normalizedContent = fullContent

  // 在完整内容中查找选中文本的位置
  const startIndex = normalizedContent.indexOf(normalizedSelected)

  if (startIndex === -1) {
    // 如果精确匹配失败，尝试模糊匹配（处理可能的换行符差异）
    return fuzzyFindTextIndex(normalizedSelected, normalizedContent)
  }

  const endIndex = startIndex + normalizedSelected.length - 1

  return {
    rangeStart: startIndex,
    rangeEnd: endIndex,
  }
}

/**
 * 模糊查找文本索引（处理换行符等特殊字符差异）
 * @param selectedText - 被选中的文本
 * @param fullContent - 完整章节内容
 * @returns { rangeStart, rangeEnd } 或 { -1, -1 }
 */
function fuzzyFindTextIndex(
  selectedText: string,
  fullContent: string,
): { rangeStart: number; rangeEnd: number } {
  // 移除所有换行和多余空格进行匹配
  const normalizedSelected = selectedText.replace(/\n/g, ' ').replace(/\s+/g, ' ').trim()
  const normalizedContent = fullContent.replace(/\n/g, ' ').replace(/\s+/g, ' ')

  const startIndex = normalizedContent.indexOf(normalizedSelected)

  if (startIndex === -1) {
    return { rangeStart: -1, rangeEnd: -1 }
  }

  const endIndex = startIndex + normalizedSelected.length - 1

  return {
    rangeStart: startIndex,
    rangeEnd: endIndex,
  }
}

/**
 * 通过 Selection API 获取准确的范围索引
 * 该方法使用浏览器的 DOM 节点遍历来计算精确位置
 * @param selection - 浏览器 Selection 对象
 * @param contentElement - 包含章节内容的 DOM 元素
 * @returns { rangeStart, rangeEnd } - 起始和结束索引
 */
export function getRangeIndexFromSelection(
  selection: Selection,
  contentElement: HTMLElement,
): { rangeStart: number; rangeEnd: number } {
  if (!selection.rangeCount || !contentElement) {
    return { rangeStart: -1, rangeEnd: -1 }
  }

  const range = selection.getRangeAt(0)

  try {
    // 创建临时范围来计算起始位置
    const preCaretRange = range.cloneRange()
    preCaretRange.selectNodeContents(contentElement)
    preCaretRange.setEnd(range.endContainer, range.endOffset)

    const rangeStart = preCaretRange.toString().length - range.toString().length
    const rangeEnd = rangeStart + range.toString().length - 1

    return {
      rangeStart: Math.max(0, rangeStart),
      rangeEnd: Math.max(0, rangeEnd),
    }
  } catch (e) {
    console.warn('Failed to calculate range index from Selection API:', e)
    return { rangeStart: -1, rangeEnd: -1 }
  }
}

/**
 * 提取选中文本及其范围索引（综合方法）
 * @param selection - 浏览器 Selection 对象
 * @param fullContent - 完整章节文本内容
 * @param contentElement - 包含内容的 DOM 元素
 * @returns { selectedText, rangeStart, rangeEnd }
 */
export function getSelectedTextWithRange(
  selection: Selection,
  fullContent: string,
  contentElement?: HTMLElement,
): {
  selectedText: string
  rangeStart: number
  rangeEnd: number
} {
  const selectedText = selection.toString()

  if (!selectedText) {
    return { selectedText: '', rangeStart: -1, rangeEnd: -1 }
  }

  // 优先使用 Selection API 获取精确位置
  if (contentElement) {
    const apiRange = getRangeIndexFromSelection(selection, contentElement)
    if (apiRange.rangeStart >= 0) {
      return {
        selectedText,
        rangeStart: apiRange.rangeStart,
        rangeEnd: apiRange.rangeEnd,
      }
    }
  }

  // 回退到文本匹配方法
  const textRange = calculateTextRangeIndex(selectedText, fullContent)

  return {
    selectedText,
    rangeStart: textRange.rangeStart,
    rangeEnd: textRange.rangeEnd,
  }
}

/**
 * 通过范围索引验证文本是否匹配
 * @param fullContent - 完整内容
 * @param rangeStart - 起始索引
 * @param rangeEnd - 结束索引
 * @returns 提取出的文本
 */
export function extractTextByRange(
  fullContent: string,
  rangeStart: number,
  rangeEnd: number,
): string {
  if (rangeStart < 0 || rangeEnd < 0 || rangeStart > rangeEnd) {
    return ''
  }

  // 注意：rangeEnd 是包含的，所以需要 +1
  return fullContent.substring(rangeStart, rangeEnd + 1)
}
