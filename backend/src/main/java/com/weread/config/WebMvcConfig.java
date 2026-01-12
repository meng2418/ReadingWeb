package com.weread.config;

import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * WebMvc配置类
 * 配置静态资源映射
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 获取项目根目录
        // System.getProperty("user.dir") 在运行时会返回 backend 目录
        // 需要向上查找父目录来找到项目根目录
        String currentDir = System.getProperty("user.dir");
        File currentFile = new File(currentDir);
        File projectRoot;
        
        // 如果当前目录是 backend，则向上查找父目录
        if (currentFile.getName().equals("backend")) {
            projectRoot = currentFile.getParentFile();
        } else {
            // 否则假设当前目录就是项目根目录
            projectRoot = currentFile;
        }
        
        // 配置书籍封面资源映射
        // 将 /bookresources/** 映射到项目根目录下的 bookresources/ 目录
        String bookResourcesPath = projectRoot.getAbsolutePath() + File.separator + "bookresources" + File.separator;
        
        log.info("配置书籍封面资源路径: {}", bookResourcesPath);
        log.info("当前工作目录: {}", currentDir);
        log.info("项目根目录: {}", projectRoot.getAbsolutePath());
        
        // 检查目录是否存在
        File bookResourcesDir = new File(bookResourcesPath);
        if (!bookResourcesDir.exists()) {
            log.warn("警告：书籍封面资源目录不存在: {}", bookResourcesPath);
        } else {
            log.info("书籍封面资源目录存在，包含 {} 个文件", bookResourcesDir.listFiles() != null ? bookResourcesDir.listFiles().length : 0);
        }
        
        registry.addResourceHandler("/bookresources/**")
                .addResourceLocations("file:" + bookResourcesPath)
                .setCachePeriod(3600); // 缓存1小时
        
        // 保留原有的 /static/** 映射（如果还有其他静态资源）
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}
