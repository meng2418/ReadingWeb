// Banner
export interface HomeBanner {
  id: number
  image: string
  link: string
}

// 接口原始书籍结构（非常重要：这是“接口事实”）
export interface HomeBookRaw {
  id: number
  title: string
  author: string
  cover: string
  description: string
  category: string
  price: number
  isFree: boolean
  rating: number
  readCount: number
}

// 榜单
export interface HomeRankingsRaw {
  weekly: HomeBookRaw[]
  monthly: HomeBookRaw[]
}

// /home 接口整体返回
export interface HomeData {
  banners: HomeBanner[]
  recommendations: HomeBookRaw[]
  rankings: HomeRankingsRaw
}
