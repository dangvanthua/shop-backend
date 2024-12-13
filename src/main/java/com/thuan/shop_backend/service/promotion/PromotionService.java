package com.thuan.shop_backend.service.promotion;

import com.thuan.shop_backend.constant.DiscountType;
import com.thuan.shop_backend.dto.request.product.ProductPromotionRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionCodeRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionRequest;
import com.thuan.shop_backend.entity.Product;
import com.thuan.shop_backend.entity.ProductPromotionCode;
import com.thuan.shop_backend.entity.Promotion;
import com.thuan.shop_backend.entity.PromotionCode;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.ProductPromotionRepository;
import com.thuan.shop_backend.repository.ProductRepository;
import com.thuan.shop_backend.repository.PromotionCodeRepository;
import com.thuan.shop_backend.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionService implements IPromotionService{

    private final PromotionRepository promotionRepository;
    private final PromotionCodeRepository promotionCodeRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Promotion createPromotion(PromotionRequest promotionRequest) {

        Optional<Promotion> promotionOptional = promotionRepository
                .findByName(promotionRequest.getName());

        if(promotionOptional.isPresent()) {
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }

        if(promotionRequest.getDiscountType() == DiscountType.PERCENTAGE) {
            if(promotionRequest.getDiscountValue() < 0 || promotionRequest.getDiscountValue() > 100){
                throw new AppException(ErrorCode.INVALID_DISCOUNT_VALUE);
            }
        }

        if(promotionRequest.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            if(promotionRequest.getDiscountValue() < 0) {
                throw new AppException(ErrorCode.INVALID_DISCOUNT_VALUE);
            }
        }

        Promotion promotion = Promotion.builder()
                .name(promotionRequest.getName())
                .discountType(promotionRequest.getDiscountType().toString().toLowerCase())
                .discountValue(promotionRequest.getDiscountValue())
                .startDate(promotionRequest.getStartDate())
                .endDate(promotionRequest.getEndDate())
                .isActive(true)
                .build();

        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public PromotionCode createPromotionCode(
            long promotionId,
            PromotionCodeRequest promotionCodeRequest) {

        Optional<PromotionCode> promotionCodeOptional = promotionCodeRepository
                .findByCode(promotionCodeRequest.getCode());

        if(promotionCodeOptional.isPresent()) {
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));

        if(promotionCodeRequest.getDiscountValue() < 0
                || (promotionCodeRequest.getDiscountValue() > promotion.getDiscountValue())) {
            throw new AppException(ErrorCode.INVALID_DISCOUNT_VALUE);
        }

        if(promotionCodeRequest.getStartDate().isBefore(promotion.getStartDate())
        || promotionCodeRequest.getEndDate().isAfter(promotion.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_DATE_VALUE);
        }

        PromotionCode promotionCode = PromotionCode.builder()
                .code(promotionCodeRequest.getCode())
                .promotion(promotion)
                .startDate(promotionCodeRequest.getStartDate())
                .endDate(promotionCodeRequest.getEndDate())
                .discountValue(promotionCodeRequest.getDiscountValue())
                .isActive(true)
                .build();

        return promotionCodeRepository.save(promotionCode);
    }

    @Override
    @Transactional
    public void addPromotionToProduct(ProductPromotionRequest productPromotionRequest) {

        Product product = productRepository.findById(productPromotionRequest.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        PromotionCode promotionCode = promotionCodeRepository
                .findByCode(productPromotionRequest.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_CODE_NOT_EXISTED));

        ProductPromotionCode productPromotionCode = ProductPromotionCode.builder()
                .product(product)
                .promotionCode(promotionCode)
                .build();

        productPromotionRepository.save(productPromotionCode);
    }
}
