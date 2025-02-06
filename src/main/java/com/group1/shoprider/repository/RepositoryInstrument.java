package com.group1.shoprider.repository;

import com.group1.shoprider.dtos.instrument.InstrumentRequest;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositoryInstrument extends JpaRepository<Instrument, Long> {
    Instrument findByName(String name);

    Optional<Instrument> findById(InstrumentRequest instrumentId);
    List<Instrument> findByType(Type type);
}
