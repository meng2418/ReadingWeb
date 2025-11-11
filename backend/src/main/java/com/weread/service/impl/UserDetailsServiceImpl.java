package com.weread.service.impl;

import com.weread.entity.UserEntity;
import com.weread.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// 必须实现 UserDetailsService 接口
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring Security 会调用此方法通过用户名（此处为手机号）加载用户信息
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("用户未找到: " + phone));

        // 将 UserEntity 转换为 Spring Security 要求的 UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(),
                // 此处简化处理：不添加权限/角色。如果需要，应从数据库加载权限
                new ArrayList<>() 
        );
    }
}