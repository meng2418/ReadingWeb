package com.weread.service.util.impl;

import com.weread.service.util.GeoLocationService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

@Service
public class GeoLocationServiceImpl implements GeoLocationService {

    // 【1. 声明 DatabaseReader 实例】
    private final DatabaseReader reader;
    private static final Logger logger = LoggerFactory.getLogger(GeoLocationServiceImpl.class);

    // 【2. 构造函数中加载数据库】
    public GeoLocationServiceImpl(@Value("${app.geoip.database-path}") Resource databaseResource) throws IOException {
        
        // 使用 Resource 接口读取数据库文件，这样它既可以读取文件系统中的文件，也可以读取 classpath 中的文件
        if (!databaseResource.exists()) {
            throw new IOException("GeoLite2-City.mmdb 数据库文件未找到，路径: " + databaseResource.getFilename());
        }

        try (InputStream databaseStream = databaseResource.getInputStream()) {
            this.reader = new DatabaseReader.Builder(databaseStream).build();
        } catch (Exception e) {
            // 记录日志并抛出异常，防止服务启动失败
            System.err.println("加载 MaxMind 数据库失败: " + e.getMessage());
            throw new IOException("无法初始化 MaxMind 数据库", e);
        }
    }
    
    @Override
    public String resolveIpToLocation(String ipAddress) {
        
        // 1. 基本安全检查：排除私有IP和本地回环地址
        if (ipAddress == null || ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") || ipAddress.startsWith("127.")) {
            return "内部网络/本地";
        }
        
        try {
            // 2. 将 IP 地址转换为 InetAddress 对象
            InetAddress ip = InetAddress.getByName(ipAddress);
            
            // 3. 调用 DatabaseReader 获取城市信息响应
            CityResponse response = reader.city(ip);
            
            // 4. 提取信息
            String country = response.getCountry().getNames().get("zh-CN"); // 获取中文国家名
            String city = response.getCity().getNames().get("zh-CN");       // 获取中文城市名
            
            // 5. 格式化结果
            StringBuilder locationBuilder = new StringBuilder();
            if (country != null) {
                locationBuilder.append(country);
            }
            if (city != null && !city.equals(country)) { // 避免出现 "美国-美国" 这种情况
                 if (locationBuilder.isEmpty()) {
                     locationBuilder.append("-");
                 }
                 locationBuilder.append(city);
            }
            
            String location = locationBuilder.toString();
            return location.isEmpty() ? "未知地点" : location;

        } catch (Exception e) {
            logger.error("IP解析服务失败。IP地址: {}", ipAddress, e);

            return "IP解析失败"; 
        }
    }
}