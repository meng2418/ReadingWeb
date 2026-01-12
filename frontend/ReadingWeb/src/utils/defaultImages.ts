/**
 * 默认图片工具函数
 */

// 默认头像路径
import defaultAvatar from '@/img/avatar.jpg'
export const DEFAULT_AVATAR = defaultAvatar

// 默认话题图片路径
import defaultTopicBg from '@/img/topic_bg.png'
export const DEFAULT_TOPIC_BG = defaultTopicBg

/**
 * 获取头像URL，如果为空或无效则返回默认头像
 * @param avatar 头像URL
 * @returns 有效的头像URL
 */
export function getAvatarUrl(avatar: string | null | undefined): string {
  if (!avatar || avatar.trim() === '' || avatar === 'null' || avatar === 'undefined') {
    return DEFAULT_AVATAR
  }
  return avatar
}

/**
 * 获取话题图片URL，如果为空或无效则返回默认话题图片
 * @param cover 话题图片URL
 * @returns 有效的话题图片URL
 */
export function getTopicCoverUrl(cover: string | null | undefined): string {
  if (!cover || cover.trim() === '' || cover === 'null' || cover === 'undefined') {
    return DEFAULT_TOPIC_BG
  }
  return cover
}
