package com.group1.shoprider.controller;


import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.service.ServiceInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instrument")
public class InstrumentController {

    @Autowired
    private ServiceInstrument serviceInstrument;

    @PostMapping("/add")
    public ResponseEntity<Instrument> addInstrument(@RequestBody Instrument instrument){
        return ResponseEntity.ok(serviceInstrument.addInstrument(instrument));
    }
}
