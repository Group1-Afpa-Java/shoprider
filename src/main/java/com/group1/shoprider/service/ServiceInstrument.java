package com.group1.shoprider.service;


import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.repository.RepositoryInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceInstrument {
    @Autowired
    private RepositoryInstrument repositoryInstrument;


    // Méthode pour ajouter instrument au stock
    public Instrument addInstrument(Instrument instrument){
        return repositoryInstrument.save(instrument);
    }
}
