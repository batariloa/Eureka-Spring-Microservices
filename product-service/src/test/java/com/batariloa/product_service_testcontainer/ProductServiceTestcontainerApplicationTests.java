package com.batariloa.product_service_testcontainer;

import com.batariloa.product_service_testcontainer.dto.ProductRequest;
import com.batariloa.product_service_testcontainer.dto.ProductResponse;
import com.batariloa.product_service_testcontainer.model.Product;
import com.batariloa.product_service_testcontainer.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceTestcontainerApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.14"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProductRepository productRepository;


    @BeforeEach
    void clearDatabase(){
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {

        ProductRequest productRequest = getProductRequest();

        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform((MockMvcRequestBuilders
                        .post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString)))
                .andExpect(status().isCreated());

        Assertions.assertTrue(productRepository.findAll().size() == 1 );
    }

    @Test
    void shouldGetAProduct() throws Exception {
        Product product = getProduct();

        //add item to repository first
        productRepository.save(product);


        //check if the item is returned
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.
                get("/api/product")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String val = result.getResponse().getContentAsString();
        List<ProductResponse> actual = Arrays.asList(objectMapper.readValue(val, ProductResponse[].class));

        Assertions.assertEquals(actual.size(), 1);
    }


    private ProductRequest getProductRequest(){

        return ProductRequest.builder()
                .name("table")
                .description("desc")
                .price(BigDecimal.valueOf(299))
                .build();
    }

    private Product getProduct(){

        return Product.builder()
                .name("table")
                .description("desc")
                .price(BigDecimal.valueOf(299))
                .build();
    }

}
