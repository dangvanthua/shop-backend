package com.thuan.shop_backend.service.product.recommend;

import com.thuan.shop_backend.dto.request.product.ProdRecommendRequest;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import org.springframework.stereotype.Service;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.*;

@Service
public class FeatureService {
    private final Map<String, Integer> productIndexMap = new HashMap<>();

    private String cleanText(String text) {
        if(text == null) {
            return "unknown";
        }
        return text.trim()
                .toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", " ");
    }

    public Instances prepareTFIDFeatures(List<ProdRecommendRequest> prodRecommendRequests) throws Exception {
        ArrayList<Attribute> attributes = new ArrayList<>();

        // Các thuộc tính chuỗi
        Attribute nameAttr = new Attribute("name", (List<String>) null);
        Attribute descAttr = new Attribute("description", (List<String>) null);
        Attribute categoryAttr = new Attribute("categoryName", (List<String>) null);

        // Các thuộc tính số
        Attribute priceAttr = new Attribute("price");
        Attribute quantityAttr = new Attribute("quantity");

        attributes.add(nameAttr);
        attributes.add(descAttr);
        attributes.add(categoryAttr);
        attributes.add(priceAttr);
        attributes.add(quantityAttr);

        Instances data = new Instances("ProductData", attributes, prodRecommendRequests.size());

        for (int i = 0; i < prodRecommendRequests.size(); i++) {
            ProdRecommendRequest product = prodRecommendRequests.get(i);

            DenseInstance instance = new DenseInstance(attributes.size());
            instance.setDataset(data);

            // Gán giá trị cho thuộc tính chuỗi
            instance.setValue(nameAttr, cleanText(product.getName()));
            instance.setValue(descAttr, descAttr.addStringValue(cleanText(product.getDescription())));
            instance.setValue(categoryAttr, categoryAttr.addStringValue(cleanText(product.getCategoryName())));

            // Gán giá trị cho thuộc tính số
            instance.setValue(priceAttr, product.getPrice());
            instance.setValue(quantityAttr, product.getQuantity());

            productIndexMap.put(product.getName(), i);
            data.add(instance);
        }

        // Áp dụng StringToWordVector cho các thuộc tính văn bản
        StringToWordVector filter = new StringToWordVector();
        filter.setTFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setInputFormat(data);

        return Filter.useFilter(data, filter);
    }

    public int getProductIndex(String productName) {
        if (!productIndexMap.containsKey(productName)) {
            throw new AppException(ErrorCode.FAILED_RECOMMEND);
        }
        return productIndexMap.get(productName);
    }
}
