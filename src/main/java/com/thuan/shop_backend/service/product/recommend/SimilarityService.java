package com.thuan.shop_backend.service.product.recommend;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;
import weka.core.Instances;

@Service
public class SimilarityService {

    public double[] calculateSimilarities(Instances data, int targetIndex) {
        int numInstance = data.numInstances();
        double[] similarities = new double[numInstance];

        // Vector của sản phẩm mục tiêu
        double[] targetVector = data.instance(targetIndex).toDoubleArray();

        for (int i = 0; i < numInstance; i++) {
            if (i == targetIndex) {
                similarities[i] = 1.0;
                continue;
            }

            // Vector của sản phẩm hiện tại
            double[] currentVector = data.instance(i).toDoubleArray();
            similarities[i] = calculateCosineSimilarity(targetVector, currentVector);
        }

        return similarities;
    }

    private double calculateCosineSimilarity(double[] vec1, double[] vec2) {
        RealVector v1 = new ArrayRealVector(vec1);
        RealVector v2 = new ArrayRealVector(vec2);

        double dotProduct = v1.dotProduct(v2);
        double normProduct = v1.getNorm() * v2.getNorm();
        return (normProduct == 0) ? 0 : dotProduct / normProduct;
    }
}
