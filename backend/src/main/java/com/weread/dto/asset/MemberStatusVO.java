package com.weread.dto.asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MemberStatusVO - 用户会员状态视图对象
 * 用于查询用户是否为会员、会员等级及到期时间。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatusVO {
   /** 是否为会员 */
    private boolean isMember;

    /** 会员等级 (如：VIP, SVIP) */
    private String level;

    /** 会员到期时间 */
    private LocalDateTime endDate; 
}
