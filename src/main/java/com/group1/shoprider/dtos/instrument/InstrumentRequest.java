package com.group1.shoprider.dtos.instrument;

import com.group1.shoprider.exceptions.TypeNotFoundException;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.models.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstrumentRequest {
    @NotBlank
    public String name;
    @NotBlank
    public String description;
    @Min(value = 1, message="La quantiter ne peut pas etre 0.")
    public int quantity;

    public double price;
    @NotBlank
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
