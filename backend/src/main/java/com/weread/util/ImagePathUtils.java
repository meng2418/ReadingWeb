package com.weread.util;

/**
 * ImagePathUtils - 图片路径处理工具类
 * 负责将数据库中的相对路径（如"1_cover.jpeg"）转换为前端可访问的完整路径
 */
public class ImagePathUtils {

    private static final String STATIC_IMAGE_PREFIX = "/static/images/";

    private ImagePathUtils() {
        // 防止实例化
    }

    /**
     * 处理封面图片路径
     * 如果路径是相对路径（如"1_cover.jpeg"），则转换为"/static/images/1_cover.jpeg"
     * 如果已经是完整路径（以"/"或"http"开头），则直接返回
     * 如果为空或null，返回null
     *
     * @param coverPath 数据库中的封面路径
     * @return 处理后的完整路径
     */
    public static String processCoverPath(String coverPath) {
        if (coverPath == null || coverPath.trim().isEmpty()) {
            return null;
        }

        // 如果已经是完整路径（以"/"或"http"开头），直接返回
        if (coverPath.startsWith("/") || coverPath.startsWith("http")) {
            return coverPath;
        }

        // 如果是相对路径（如"1_cover.jpeg"），拼接为完整路径
        return STATIC_IMAGE_PREFIX + coverPath;
    }

    /**
     * 处理封面图片路径，如果为空则返回默认封面
     *
     * @param coverPath 数据库中的封面路径
     * @param defaultCover 默认封面路径
     * @return 处理后的完整路径
     */
    public static String processCoverPathWithDefault(String coverPath, String defaultCover) {
        String processed = processCoverPath(coverPath);
        return processed != null ? processed : defaultCover;
    }
}
