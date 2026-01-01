package com.weread.util.impl; // 假设放在 util.impl 包下

import com.weread.util.DtoConverter;
import com.weread.vo.asset.OrderStatusVO;
import com.weread.vo.user.LoginLogVO;
import com.weread.vo.user.UserDetailVO;
import com.weread.entity.user.UserEntity;
import com.weread.entity.user.LoginLogEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // 标记为 Spring 组件
public class DtoConverterImpl implements DtoConverter {

    // --- 用户/安全相关 ---
    @Override
    public LoginLogVO toLoginLogVO(LoginLogEntity entity) {
        // 实际：使用 BeanUtils 或 MapStruct/ModelMapper 进行属性映射
        return new LoginLogVO(
                entity.getLoginLogId(),
                entity.getLoginTime(),
                entity.getIpAddress(),
                entity.getLoginLocation(),
                entity.getDevice(),
                entity.getStatus());
    }

    

    // --- 订单/充值相关 ---

    // 订单创建 helper 2 (充值)

}