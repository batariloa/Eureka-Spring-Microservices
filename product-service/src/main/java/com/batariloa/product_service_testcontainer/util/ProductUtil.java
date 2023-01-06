package com.batariloa.product_service_testcontainer.util;

import com.batariloa.product_service_testcontainer.dto.ProductResponse;
import com.batariloa.product_service_testcontainer.model.Product;

public class ProductUtil {


    public static ProductResponse mapToProductResponse(Product product){

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();

    }

}
