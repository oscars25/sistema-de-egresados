package com.universidad.egresados.repository;

import com.universidad.egresados.model.AplicacionOferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AplicacionOfertaRepository extends JpaRepository<AplicacionOferta, Long> {
    List<AplicacionOferta> findByEmailEgresado(String emailEgresado);
}
