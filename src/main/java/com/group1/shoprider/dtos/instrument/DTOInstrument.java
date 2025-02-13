package com.group1.shoprider.dtos.instrument;


import com.group1.shoprider.models.Instrument;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DTOInstrument {
    public String name;
    public int quantity;
    public double price;
    public String type;


    public static DTOInstrument convertToDTOInstrument(Instrument instrument) {
        return new DTOInstrument(instrument.getName(), instrument.getQuantity(), instrument.getPrice(), instrument.getType().getName());
    }
    public static List<DTOInstrument> convertToDTOInstrumentList(List<Instrument> instruments) {
        return instruments.stream().map(DTOInstrument::convertToDTOInstrument).toList();
    }
}
