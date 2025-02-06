package com.group1.shoprider.controller;


import com.group1.shoprider.dtos.instrument.InstrumentReponse;
import com.group1.shoprider.dtos.instrument.InstrumentRequest;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.repository.RepositoryInstrument;
import com.group1.shoprider.service.ServiceInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instrument")
public class InstrumentController {

    @Autowired
    private ServiceInstrument serviceInstrument;

    @PostMapping("/add")
    public ResponseEntity<InstrumentReponse> addInstrument(@RequestBody InstrumentRequest instrumentDTO){
        InstrumentReponse reponseDTO = serviceInstrument.addInstrument(instrumentDTO);
        return new ResponseEntity<>(reponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Instrument> deleteInstrument(@PathVariable Long id) {
        serviceInstrument.deleteInstrument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
