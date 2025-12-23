// types.ts - 添加或修改以下类型
export interface TypographySettings {
  fontSize: number
  lineHeight: number
  charsPerLine?: number  // 每行字符数
  linesPerColumn?: number // 每栏行数
}

export interface BookPage {
  chapter: string
  content: string[]  // 每段文本
  pageNumber: number
  totalPages: number
}

export interface PageConfig {
  charsPerLine: number  // 每行字符数（固定）
  linesPerColumn: number // 每栏行数（固定）
  linesPerPage: number  // 每页总行数（linesPerColumn * 2）
  columnWidth: number   // 每栏宽度
}
