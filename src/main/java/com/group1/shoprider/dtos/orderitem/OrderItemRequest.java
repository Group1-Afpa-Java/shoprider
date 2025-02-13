package com.group1.shoprider.dtos.orderitem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    private Long instrumentId;
    private Integer quantity;
}