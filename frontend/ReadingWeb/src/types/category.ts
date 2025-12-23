export interface CategoryTab {
  id: string
  name: string
}

export interface SubCategory {
  id: string
  name: string
}

export interface RankedBook {
  id: number
  cover: string
  title: string
  author: string
  recommend: string
  readersCount: string | number
  recommendationRate: number
  description: string
}
