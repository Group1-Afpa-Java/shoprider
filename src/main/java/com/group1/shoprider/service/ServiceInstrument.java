package com.group1.shoprider.service;


import com.group1.shoprider.dtos.instrument.InstrumentReponse;
import com.group1.shoprider.dtos.instrument.InstrumentRequest;
import com.group1.shoprider.exceptions.InstrumentNotFoundException;
import com.group1.shoprider.exceptions.TypeNotFoundException;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.models.Type;
import com.group1.shoprider.repository.RepositoryInstrument;
import com.group1.shoprider.repository.RepositoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceInstrument {
    @Autowired
    private RepositoryInstrument repositoryInstrument;

    @Autowired
    private RepositoryType repositoryType;


    // Méthode pour ajouter instrument au stock
    public InstrumentReponse addInstrument(InstrumentRequest instrumentDTO) {
        // verifier que le type existe
        Optional<Type> type = repositoryType.findByName(instrumentDTO.getType());
        if (type.isEmpty()) {
            throw new TypeNotFoundException(String.format("Le type: %s n'existe pas", instrumentDTO.getType()));
        }

        Instrument instrument = InstrumentRequest.convertToEntity(instrumentDTO, type.get());
        repositoryInstrument.save(instrument);

        return InstrumentReponse.convertToReponse(instrument);
    }

    public void deleteInstrument(Long id) {
        repositoryInstrument.deleteById(id);
    }

    }






