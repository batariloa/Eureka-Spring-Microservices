package com.batariloa.inventoryservice.service;

import com.batariloa.inventoryservice.dto.InventoryResponse;
import com.batariloa.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService
{

    public final InventoryRepository inventoryRepository;


    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){

        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->{
                    return InventoryResponse.builder()
                           .skuCode(inventory.getSkuCode())
                           .isInStock(inventory.getQuantity() > 0)
                           .build();}
                ).toList();

    }
}
