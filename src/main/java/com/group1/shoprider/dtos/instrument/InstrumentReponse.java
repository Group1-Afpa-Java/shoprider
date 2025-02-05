package com.group1.shoprider.dtos.instrument;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstrumentReponse {
    public String name;
    public String description;
    public int quantity;
    public double price;
    public String type;
}