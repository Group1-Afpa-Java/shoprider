package com.group1.shoprider.services;


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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public InstrumentReponse updateInstrument(Long id, InstrumentRequest instrumentDTO) {
        Optional<Instrument> optionalInstrument = repositoryInstrument.findById(id);
        if (optionalInstrument.isEmpty()) {
            throw new InstrumentNotFoundException(String.format("L'instrument avec l'ID %d n'existe pas", id));
        }

        Instrument instrument = optionalInstrument.get();

        // Mise à jour des champs
        instrument.setName(instrumentDTO.getName());
        instrument.setDescription(instrumentDTO.getDescription());
        instrument.setPrice(instrumentDTO.getPrice());
        instrument.setQuantity(instrumentDTO.getQuantity());

        // Vérification et mise à jour du type
        Optional<Type> type = repositoryType.findByName(instrumentDTO.getType());
        if (type.isEmpty()) {
            throw new TypeNotFoundException(String.format("Le type: %s n'existe pas", instrumentDTO.getType()));
        }
        instrument.setType(type.get());

        // Sauvegarde des modifications
        Instrument updatedInstrument = repositoryInstrument.save(instrument);

        return InstrumentReponse.convertToReponse(updatedInstrument);
    }
    public List<InstrumentReponse> getAllInstruments() {
        List<Instrument> instruments = repositoryInstrument.findAll();
        return instruments.stream()
                .map(InstrumentReponse::convertToReponse)
                .collect(Collectors.toList());
    }
    public Map<String, List<InstrumentReponse>> getAllInstrumentsByType() {
        List<Instrument> instruments = repositoryInstrument.findAll();
        return instruments.stream()
                .collect(Collectors.groupingBy(
                        instrument -> instrument.getType().getName(),
                        Collectors.mapping(InstrumentReponse::convertToReponse, Collectors.toList())
                ));
    }

    public List<InstrumentReponse> getInstrumentsByType(String typeName) {
        Optional<Type> type = repositoryType.findByName(typeName);
        if (type.isEmpty()) {
            throw new TypeNotFoundException(String.format("Le type: %s n'existe pas", typeName));
        }

        List<Instrument> instruments = repositoryInstrument.findByType(type.get());
        return instruments.stream()
                .map(InstrumentReponse::convertToReponse)
                .collect(Collectors.toList());
    }

}






