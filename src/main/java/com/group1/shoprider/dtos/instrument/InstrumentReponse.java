package com.group1.shoprider.dtos.instrument;

import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.models.Type;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class InstrumentReponse {
    public Long id;
    public String name;
    public String description;
    public int quantity;
    public double price;
    public String type;


    public static InstrumentReponse convertToReponse(Instrument instrument) {
        InstrumentReponse dto = new InstrumentReponse();
        dto.setId(instrument.getId());
        dto.setName(instrument.getName());
        dto.setDescription(instrument.getDescription());
        dto.setQuantity(instrument.getQuantity());
        dto.setPrice(instrument.getPrice());
        dto.setType(instrument.getType().getName());
        return dto;
    }
}