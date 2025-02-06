package com.group1.shoprider.dtos.instrument;

import com.group1.shoprider.exceptions.TypeNotFoundException;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.models.Type;
import lombok.*;

import java.util.Optional;

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

    public static Instrument convertToEntity(InstrumentRequest dto, Type type) {
        Instrument instrument = new Instrument();
        instrument.setName(dto.getName());
        instrument.setDescription(dto.getDescription());
        instrument.setPrice(dto.getPrice());
        instrument.setQuantity(dto.getQuantity());
        instrument.setType(type);

        return instrument;
    }
}
