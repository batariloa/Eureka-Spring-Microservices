package com.batariloa.product_service_testcontainer.repository;


import com.batariloa.product_service_testcontainer.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}
