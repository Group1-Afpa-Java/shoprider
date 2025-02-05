package com.group1.shoprider.repository;

import com.group1.shoprider.models.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryInstrument extends JpaRepository<Instrument, Long> {
    List<Instrument> findByName(String name);
}
