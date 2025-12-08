package com.weread.service.impl.asset;

import com.weread.entity.asset.MemberEntity;
import com.weread.repository.asset.MemberRepository;
import com.weread.service.asset.MemberService;
import com.weread.service.asset.TrialCardService; // 依赖体验卡服务
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TrialCardService trialCardService; // 注入体验卡服务

    @Override
    @Transactional
    public MemberEntity activateMember(Long userId, int daysToAdd) {
        
        int totalDays = daysToAdd;
        
        // 1. 合并体验卡天数 (业务规则)
        int trialDays = trialCardService.getTrialDaysBalance(userId);
        if (trialDays > 0) {
            totalDays += trialDays;
            trialCardService.clearTrialBalance(userId); // 清零体验卡余额
        }

        // 2. 获取或创建 MemberEntity
        MemberEntity member = memberRepository.findByUserId(userId)
            .orElseGet(() -> {
                MemberEntity newMember = new MemberEntity();
                newMember.setUserId(userId);
                newMember.setLevel(1);
                return newMember;
            });

        // 3. 计算新的过期日期
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseDate = (member.getExpireDate() == null || member.getExpireDate().isBefore(now)) 
                                 ? now 
                                 : member.getExpireDate();
        
        member.setExpireDate(baseDate.plusDays(totalDays));
        
        return memberRepository.save(member);
    }
    
    @Override
    public boolean isMemberValid(Long userId) {
        // 1. 检查正式会员是否有效
        Optional<MemberEntity> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isPresent() && memberOpt.get().getExpireDate() != null && memberOpt.get().getExpireDate().isAfter(LocalDateTime.now())) {
            return true;
        }

        // 2. 检查体验卡余额是否大于 0
        if (trialCardService.getTrialDaysBalance(userId) > 0) {
            return true;
        }

        return false;
    }
    @Override
    public Optional<MemberEntity> getMemberEntityByUserId(Long userId) {
        return memberRepository.findByUserId(userId); // Uses the injected repository
    }
}