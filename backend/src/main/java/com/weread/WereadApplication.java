package com.weread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class WereadApplication {

    private Map<String, Object> testUser = createTestUser();

    public static void main(String[] args) {
        SpringApplication.run(WereadApplication.class, args);
        System.out.println("🚀 微信读书测试服务器启动成功！");
        System.out.println("📍 访问: http://localhost:8080");
        System.out.println("");
        System.out.println("📋 核心功能测试端点:");
        System.out.println("  GET  /api/test/features         - 查看所有功能演示");
        System.out.println("  POST /api/auth/login            - JWT认证测试");
        System.out.println("  GET  /api/test/protected        - 权限验证测试");
        System.out.println("  GET  /api/reader/demo           - 阅读器功能演示");
        System.out.println("  GET  /api/community/feed        - 无限滚动测试");
    }

    private Map<String, Object> createTestUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", "test");
        user.put("password", "123");
        user.put("nickname", "测试用户");
        user.put("isMember", true);
        user.put("coins", 500);
        user.put("totalReadingTime", 3600);
        return user;
    }

    // 1. JWT认证系统测试
    @PostMapping("/api/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Map<String, Object> response = new HashMap<>();
        
        if (testUser.get("username").equals(username) && testUser.get("password").equals(password)) {
            String token = "mock-jwt-token-" + System.currentTimeMillis();
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", testUser.get("id"));
            userData.put("username", testUser.get("username"));
            userData.put("nickname", testUser.get("nickname"));
            userData.put("isMember", testUser.get("isMember"));
            userData.put("coins", testUser.get("coins"));
            userData.put("totalReadingTime", testUser.get("totalReadingTime"));

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userData);

            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", data);
        } else {
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
        }
        return response;
    }

    // 2. 权限验证中间件测试
    @GetMapping("/api/test/protected")
    public Map<String, Object> protectedEndpoint(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("code", 401);
            response.put("message", "需要认证token");
            return response;
        }

        String token = authHeader.substring(7);
        if (!token.startsWith("mock-jwt-token-")) {
            response.put("code", 401);
            response.put("message", "token无效");
            return response;
        }

        response.put("code", 200);
        response.put("message", "访问成功");
        response.put("data", Map.of(
            "user", testUser.get("username"),
            "protectedData", "这是受保护的资源",
            "timestamp", System.currentTimeMillis()
        ));
        return response;
    }

    // 3. 阅读器功能演示
    @GetMapping("/api/reader/demo")
    public Map<String, Object> readerDemo() {
        Map<String, Object> book = new HashMap<>();
        book.put("id", 1);
        book.put("title", "测试书籍");
        book.put("author", "测试作者");
        book.put("cover", "/images/test-cover.jpg");
        book.put("progress", 0.35);

        List<Map<String, Object>> chapters = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> chapter = new HashMap<>();
            chapter.put("id", i);
            chapter.put("title", "第" + i + "章 测试章节");
            chapter.put("content", "这是第" + i + "章的内容。".repeat(50));
            chapter.put("order", i);
            chapters.add(chapter);
        }

        Map<String, Object> readerSettings = new HashMap<>();
        readerSettings.put("fontSize", 16);
        readerSettings.put("theme", "light");
        readerSettings.put("layout", "scroll");
        readerSettings.put("lineHeight", 1.6);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", Map.of(
            "book", book,
            "chapters", chapters,
            "currentChapter", 1,
            "readerSettings", readerSettings,
            "notes", Arrays.asList(
                Map.of("id", 1, "content", "这是一个测试笔记", "position", 120),
                Map.of("id", 2, "content", "重要内容标记", "position", 450)
            )
        ));
        return response;
    }

    // 4. 无限滚动社区内容测试
    @GetMapping("/api/community/feed")
    public Map<String, Object> communityFeed(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> posts = new ArrayList<>();
        int startId = (page - 1) * limit + 1;
        
        for (int i = 0; i < limit; i++) {
            Map<String, Object> post = new HashMap<>();
            post.put("id", startId + i);
            post.put("title", "社区帖子 #" + (startId + i));
            post.put("content", "这是第 " + (startId + i) + " 个帖子的内容。".repeat(10));
            post.put("author", "用户" + (startId + i));
            post.put("likes", (int)(Math.random() * 100));
            post.put("comments", (int)(Math.random() * 50));
            post.put("timestamp", System.currentTimeMillis() - i * 3600000);
            posts.add(post);
        }

        boolean hasMore = page < 5;

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", Map.of(
            "posts", posts,
            "pagination", Map.of(
                "page", page,
                "limit", limit,
                "total", 50,
                "hasMore", hasMore
            )
        ));
        return response;
    }

    // 5. 统一API响应格式测试
    @GetMapping("/api/test/features")
    public Map<String, Object> allFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("JWT认证系统", "✅ 用户登录、token验证、权限控制");
        features.put("阅读器功能", "✅ 字体调整、主题切换、进度保存、笔记功能");
        features.put("状态管理", "✅ 用户状态、阅读状态、UI设置管理");
        features.put("无限滚动", "✅ 分页加载、滚动优化、数据缓存");
        features.put("API管理", "✅ 统一响应格式、错误处理、请求拦截");

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "核心功能测试服务器");
        response.put("data", features);
        return response;
    }

    // 6. 错误处理演示
    @GetMapping("/api/test/error")
    public Map<String, Object> errorDemo() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "服务器内部错误");
        response.put("error", "模拟错误信息");
        return response;
    }
}