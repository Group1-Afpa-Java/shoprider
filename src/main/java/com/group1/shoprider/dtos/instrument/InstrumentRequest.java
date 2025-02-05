package com.group1.shoprider.dtos.instrument;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstrumentRequest {
    public String name;
    public String description;
    public int quantity;
    public double price;
    public String type;
}
