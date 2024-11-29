package com.thuan.shop_backend.service.attribute;

import com.thuan.shop_backend.dto.request.AttributeRequest;
import com.thuan.shop_backend.dto.response.AttributeResponse;
import com.thuan.shop_backend.entity.Attribute;
import com.thuan.shop_backend.entity.CategoryAttribute;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.AttributeCateRepository;
import com.thuan.shop_backend.repository.AttributeProductRepository;
import com.thuan.shop_backend.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeService implements IAttributeService{

    private final AttributeRepository attributeRepository;
    private final AttributeCateRepository attributeCateRepository;
    private final AttributeProductRepository attributeProductRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AttributeResponse createAttribute(AttributeRequest attributeRequest) {

        if(attributeRepository.existsByName(attributeRequest.getName())) {
            throw new AppException(ErrorCode.ATTRIBUTE_EXISTED);
        }

        Attribute attribute = modelMapper.map(attributeRequest, Attribute.class);

        attribute = attributeRepository.save(attribute);

        return AttributeResponse.fromAttribute(attribute);
    }

    @Override
    public List<AttributeResponse> getAllAttributes() {
        List<Attribute> attributes = attributeRepository.findAll();
        return attributes.stream()
                .map(AttributeResponse::fromAttribute)
                .toList();
    }

    @Override
    public AttributeResponse getAttribute(long attributeId) {

        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_EXIST));

        return AttributeResponse.fromAttribute(attribute);
    }

    @Override
    @Transactional
    public AttributeResponse updateAttribute(
            long attributeId,
            AttributeRequest attributeRequest) {

        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_EXIST));

        if(attributeRequest.getName() != null) {
            attribute.setName(attributeRequest.getName());
        }

        if(attributeRequest.getDataType() != null) {
            attribute.setDataType(attributeRequest.getDataType());
        }

        attribute = attributeRepository.save(attribute);

        return AttributeResponse.fromAttribute(attribute);
    }

    @Override
    @Transactional
    public void deleteAttribute(long attributeId) {

        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_EXIST));

        List<CategoryAttribute> categoryAttributes = attributeCateRepository.findByAttribute(attribute);

        if(!categoryAttributes.isEmpty()) {
            attributeCateRepository.deleteAll(categoryAttributes);
        }

        attributeRepository.delete(attribute);
    }

    @Override
    public List<Attribute> getAttributeById(List<Long> attributeIds) {
        if (attributeIds == null || attributeIds.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_ATTRIBUTE_ID_LIST);
        }

        if (attributeIds.stream().anyMatch(id -> id <= 0)) {
            throw new AppException(ErrorCode.INVALID_ATTRIBUTE_ID);
        }

        List<Attribute> attributes = attributeRepository.findAllById(attributeIds);

        if (attributes.size() != attributeIds.size()) {
            throw new AppException(ErrorCode.ATTRIBUTE_NOT_EXIST);
        }

        return attributes;
    }
}
