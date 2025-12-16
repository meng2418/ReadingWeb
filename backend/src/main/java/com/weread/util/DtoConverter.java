package com.weread.util;

// 假设导入所有需要转换的 Entity 和 DTO/VO
import com.weread.entity.user.LoginLogEntity;
import com.weread.entity.user.UserEntity;
import com.weread.vo.user.LoginLogVO;
import com.weread.vo.user.UserDetailVO;


/**
 * DtoConverter - 实体(Entity)与视图对象(VO)之间的转换工具。
 */
public interface DtoConverter {

    // --- 用户/安全相关 ---
    LoginLogVO toLoginLogVO(LoginLogEntity entity);
    UserDetailVO toUserDetailVO(UserEntity entity);

    

    // --- 订单/充值相关 ---
    
}