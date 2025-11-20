package com.weread.service.impl;

import com.weread.dto.asset.CreateOrderDTO;
import com.weread.dto.asset.RechargeOrderDTO;
import com.weread.dto.asset.CoinBalanceVO;
import com.weread.dto.asset.MemberStatusVO;
import com.weread.dto.asset.MemberPackageVO;
import com.weread.dto.asset.OrderStatusVO;
import com.weread.dto.asset.PaymentPreOrderVO;
import com.weread.dto.asset.RechargeLogVO;
import com.weread.dto.asset.ExpenseLogVO;
import com.weread.entity.asset.MemberEntity;
import com.weread.entity.asset.MemberPackageEntity;
import com.weread.entity.asset.OrderEntity;
import com.weread.entity.asset.AssetEntity;
import com.weread.entity.asset.RechargeLogEntity;
import com.weread.entity.asset.RechargePackageEntity;
import com.weread.entity.asset.ExpenseLogEntity;
import com.weread.repository.asset.MemberRepository;
import com.weread.repository.asset.MemberPackageRepository;
import com.weread.repository.asset.OrderRepository;
import com.weread.repository.asset.AssetRepository;
import com.weread.repository.asset.RechargeLogRepository;
import com.weread.repository.asset.ExpenseLogRepository;
import com.weread.service.AssetService;
import com.weread.service.external.PaymentGatewayService;
import com.weread.util.DtoConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 假设的自定义异常
class OrderCreationException extends RuntimeException {
    public OrderCreationException(String message) { super(message); }
}
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}

@Service
public class AssetServiceImpl implements AssetService {

    // 完整的依赖注入
    private final MemberRepository memberRepository;
    private final MemberPackageRepository memberPackageRepository;
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final RechargeLogRepository rechargeLogRepository;
    private final ExpenseLogRepository expenseLogRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final DtoConverter converter; 

    // 构造函数注入所有依赖
    public AssetServiceImpl(MemberRepository memberRepository, MemberPackageRepository memberPackageRepository,
                            OrderRepository orderRepository, AssetRepository assetRepository,
                            RechargeLogRepository rechargeLogRepository, ExpenseLogRepository expenseLogRepository,
                            PaymentGatewayService paymentGatewayService, DtoConverter converter) {
        this.memberRepository = memberRepository;
        this.memberPackageRepository = memberPackageRepository;
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.rechargeLogRepository = rechargeLogRepository;
        this.expenseLogRepository = expenseLogRepository;
        this.paymentGatewayService = paymentGatewayService;
        this.converter = converter;
    }

    // ===========================================
    // 1. 会员体系 (Membership System)
    // ===========================================

    @Override
    public MemberStatusVO getMemberStatus(Long userId) {
        MemberEntity member = memberRepository.findByUserId(userId)
                .orElse(null); // 如果用户没有会员记录，则返回 null

        if (member == null || member.getEndDate().isBefore(LocalDateTime.now())) {
            return new MemberStatusVO(false, "NONE", null);
        }

        return converter.toMemberStatusVO(member);
    }

    @Override
    public List<MemberPackageVO> getMemberPackages() {
        List<MemberPackageEntity> packages = memberPackageRepository.findAllAvailable();
        return packages.stream()
                .map(converter::toMemberPackageVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PaymentPreOrderVO createMemberOrder(Long userId, CreateOrderDTO request) {
        // 1. 校验 packageId 和支付方式
        MemberPackageEntity packageEntity = memberPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new OrderCreationException("会员套餐不存在"));

        // 2. 创建订单实体
        OrderEntity order = converter.toOrderEntity(userId, packageEntity, request.getPaymentMethod());
        order.setType("MEMBER");
        order.setStatus("PENDING");
        orderRepository.save(order);

        // 3. 调用外部支付网关，获取预支付信息
        PaymentPreOrderVO preOrderInfo = paymentGatewayService.createPreOrder(
                order.getOrderId(), 
                order.getAmountCents(), 
                order.getPaymentMethod(),
                "会员开通/续费"
        );
        
        // 4. 更新订单的支付网关信息 (如 prepayId)
        order.setPrepayId(preOrderInfo.getPrepayId());
        orderRepository.save(order);
        
        return preOrderInfo;
    }

    @Override
    public OrderStatusVO getOrderStatus(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("订单未找到或无权限访问"));

        // 实际场景中，如果订单状态仍为 PENDING，可能需要调用 paymentGatewayService 查询一次实时状态
        
        return converter.toOrderStatusVO(order);
    }

    // ===========================================
    // 2. 我的资产 (User Assets)
    // ===========================================

    @Override
    public CoinBalanceVO getCoinBalance(Long userId) {
        AssetEntity asset = assetRepository.findByUserId(userId)
                .orElse(null); 
        
        // 如果资产记录不存在，可能需要初始化一个零余额的记录
        if (asset == null) {
            return new CoinBalanceVO(0);
        }
        
        return converter.toCoinBalanceVO(asset);
    }

    @Override
    public PaymentPreOrderVO createRechargeOrder(Long userId, RechargeOrderDTO request) {
        // 1. 校验 packageId 和支付方式
        RechargePackageEntity packageEntity = memberPackageRepository.findRechargePackageById(request.getPackageId())
                .orElseThrow(() -> new OrderCreationException("充值套餐不存在"));
        
        // 2. 创建订单实体
        OrderEntity order = converter.toOrderEntity(userId, packageEntity, request.getPaymentMethod());
        order.setType("RECHARGE");
        order.setStatus("PENDING");
        orderRepository.save(order);

        // 3. 调用外部支付网关
        PaymentPreOrderVO preOrderInfo = paymentGatewayService.createPreOrder(
                order.getOrderId(), 
                order.getAmountCents(), 
                order.getPaymentMethod(),
                "书币充值"
        );

        // 4. 更新订单的支付网关信息
        order.setPrepayId(preOrderInfo.getPrepayId());
        orderRepository.save(order);
        
        return preOrderInfo;
    }

    @Override
    public List<RechargeLogVO> getRechargeLogs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("rechargeTime").descending());
        Page<RechargeLogEntity> logs = rechargeLogRepository.findByUserId(userId, pageable);
        
        return logs.getContent().stream()
                .map(converter::toRechargeLogVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseLogVO> getExpenseLogs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("expenseTime").descending());
        Page<ExpenseLogEntity> logs = expenseLogRepository.findByUserId(userId, pageable);
        
        return logs.getContent().stream()
                .map(converter::toExpenseLogVO)
                .collect(Collectors.toList());
    }
}