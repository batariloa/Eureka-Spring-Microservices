package com.batariloa.product_service_testcontainer.service;


import com.batariloa.product_service_testcontainer.dto.ProductRequest;
import com.batariloa.product_service_testcontainer.dto.ProductResponse;
import com.batariloa.product_service_testcontainer.model.Product;
import com.batariloa.product_service_testcontainer.repository.ProductRepository;
import com.batariloa.product_service_testcontainer.util.ProductUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    final ProductRepository productRepository;


    public void createProduct(ProductRequest productRequest){

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product is saved. ID: "+ product.getId());
    }


    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream().map(ProductUtil::mapToProductResponse).toList();
    }
}
