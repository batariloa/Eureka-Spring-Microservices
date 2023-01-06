package com.batarilo.orderservice.service;

import com.batarilo.orderservice.dto.InventoryResponse;
import com.batarilo.orderservice.dto.OrderLineItemDto;
import com.batarilo.orderservice.dto.OrderRequest;
import com.batarilo.orderservice.model.Order;
import com.batarilo.orderservice.model.OrderLineItem;
import com.batarilo.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class OrderService {


    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);


    public void placeOrder(OrderRequest orderRequest){
        System.out.println("ALL RESULTS ");
        log.info("ALl results");
        Order order = Order.builder()
                .orderLineItemList(
                        orderRequest
                                .getOrderLineItemList()
                                .stream().map(item -> new OrderLineItem(item.getId(),item.getSkuCode(),item.getPrice(), item.getQuantity())).toList())
                .build();

        List<String> skuCodes = extractSkuCodes(order.getOrderLineItemList());
        //Call inventory service and place order if product is in stock
        InventoryResponse[] result = webClient.get()
                    .uri("http://localhost:8082/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build()
                    )
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();


        boolean allProductsInStock = Arrays.stream(result).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock && result.length>0) orderRepository.save(order);
        else throw new IllegalArgumentException("Product is not in stock, please try again later");

    }

    public List<String> extractSkuCodes(List<OrderLineItem> listOfItems){

        return listOfItems.stream().map(OrderLineItem::getSkuCode).toList();
    }

}
