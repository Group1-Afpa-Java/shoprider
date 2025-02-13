package com.group1.shoprider.dtos.order;

import com.group1.shoprider.dtos.orderitem.OrderItemRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private List<OrderItemRequest> items;
}