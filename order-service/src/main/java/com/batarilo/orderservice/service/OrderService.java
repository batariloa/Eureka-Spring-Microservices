package com.batarilo.orderservice.service;

import com.batarilo.orderservice.dto.OrderRequest;
import com.batarilo.orderservice.model.Order;
import com.batarilo.orderservice.model.OrderLineItem;
import com.batarilo.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {


    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){

        Order order = Order.builder()
                .orderLineItemList(
                        orderRequest
                                .getOrderLineItemList()
                                .stream().map(item -> new OrderLineItem(item.getId(),item.getSkuCode(),item.getPrice(), item.getQuantity())).toList())
                .build();

        orderRepository.save(order);

    }

}
