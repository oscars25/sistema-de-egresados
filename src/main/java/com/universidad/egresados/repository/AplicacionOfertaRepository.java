package com.universidad.egresados.repository;

import com.universidad.egresados.model.AplicacionOferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AplicacionOfertaRepository extends JpaRepository<AplicacionOferta, Long> {
    
    List<AplicacionOferta> findByEmailEgresado(String emailEgresado);

    List<AplicacionOferta> findByOfertaEmpleoId(Long ofertaId);

    Optional<AplicacionOferta> findByEmailEgresadoAndOfertaEmpleoId(String emailEgresado, Long ofertaId);
}
