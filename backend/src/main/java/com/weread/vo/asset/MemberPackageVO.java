package com.weread.vo.asset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MemberPackageVO - 会员套餐信息视图对象
 * 用于展示可供购买的会员套餐。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPackageVO {
    /** 套餐ID */
    private Long packageId;

    /** 套餐名称 (如：月度会员, 年度会员) */
    private String name;

    /** 套餐时长 (天) */
    private Integer durationDays;

    /** 套餐价格 (单位：分) */
    private Integer priceCents;
}
