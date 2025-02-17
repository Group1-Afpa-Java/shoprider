package com.group1.shoprider.controllers;


import com.group1.shoprider.dtos.instrument.InstrumentReponse;
import com.group1.shoprider.dtos.instrument.InstrumentRequest;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.services.ServiceInstrument;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instruments")
public class InstrumentController {

    @Autowired
    private ServiceInstrument serviceInstrument;


    @PostMapping("/add")
    public ResponseEntity<InstrumentReponse> addInstrument(@RequestBody @Valid InstrumentRequest instrumentDTO){
        InstrumentReponse reponseDTO = serviceInstrument.addInstrument(instrumentDTO);
        return new ResponseEntity<>(reponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Instrument> deleteInstrument(@PathVariable Long id) {
        serviceInstrument.deleteInstrument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("update/{id}")
    public ResponseEntity<InstrumentReponse> updateInstrument(@PathVariable Long id, @RequestBody InstrumentRequest instrumentDTO) {
        InstrumentReponse reponseDTO = serviceInstrument.updateInstrument(id, instrumentDTO);
        return new ResponseEntity<>(reponseDTO, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<InstrumentReponse>> getAllInstruments() {
        List<InstrumentReponse> instruments = serviceInstrument.getAllInstruments();
        return new ResponseEntity<>(instruments, HttpStatus.OK);
    }

    @GetMapping("/by-type")
    public ResponseEntity<Map<String, List<InstrumentReponse>>> getAllInstrumentsByType() {
        Map<String, List<InstrumentReponse>> instrumentsByType = serviceInstrument.getAllInstrumentsByType();
        return new ResponseEntity<>(instrumentsByType, HttpStatus.OK);
    }

    @GetMapping("/by-type/{typeName}")
    public ResponseEntity<List<InstrumentReponse>> getInstrumentsByType(@PathVariable String typeName) {
        List<InstrumentReponse> instruments = serviceInstrument.getInstrumentsByType(typeName);
        return new ResponseEntity<>(instruments, HttpStatus.OK);
    }
}
