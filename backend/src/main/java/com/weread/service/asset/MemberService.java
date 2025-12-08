package com.weread.service.asset;

import com.weread.entity.asset.MemberEntity;

import java.util.Optional;

public interface MemberService {
    MemberEntity activateMember(Long userId, int daysToAdd);
    boolean isMemberValid(Long userId);
    Optional<MemberEntity> getMemberEntityByUserId(Long userId);
}