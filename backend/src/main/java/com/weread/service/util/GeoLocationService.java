package com.weread.service.util; 

// 这个服务用于抽象调用 MaxMind 或其他 IP 数据库的逻辑
public interface GeoLocationService {

    /**
     * 将 IP 地址解析为用户友好的地理位置字符串（例如：中国-广东省-深圳市）
     * * @param ipAddress 待解析的 IP 地址
     * @return 解析后的地理位置字符串，如果失败返回 null 或 "未知地点"
     */
    String resolveIpToLocation(String ipAddress);
}