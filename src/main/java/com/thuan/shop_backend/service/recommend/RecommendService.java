package com.thuan.shop_backend.service.recommend;

import org.apache.spark.SparkConf;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class RecommendService implements IRecommendService{

    @Override
    public void recommendProducts() {
        SparkConf conf = new SparkConf().setAppName("ShopApp").setMaster("local[*]");
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

        Dataset<Row> data = spark.read().format("csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load("data/ratings.csv");

        ALS als = new ALS()
                .setUserCol("user_id")
                .setItemCol("product_id")
                .setRatingCol("rating")
                .setMaxIter(10)
                .setRegParam(0.1);

        ALSModel model = als.fit(data);

        Dataset<Row> recommendations = model.recommendForAllUsers(10);
        recommendations.show();

        spark.stop();
    }
}