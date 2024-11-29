package com.thuan.shop_backend.service.attribute;

import com.thuan.shop_backend.dto.request.AttributeRequest;
import com.thuan.shop_backend.dto.response.AttributeResponse;
import com.thuan.shop_backend.entity.Attribute;

import java.util.List;

public interface IAttributeService {
    AttributeResponse createAttribute(AttributeRequest attributeRequest);
    List<AttributeResponse> getAllAttributes();
    AttributeResponse getAttribute(long attributeId);
    AttributeResponse updateAttribute(long attributeId, AttributeRequest attributeRequest);
    void deleteAttribute(long attributeId);
    List<Attribute> getAttributeById(List<Long> attributeIds);
}
