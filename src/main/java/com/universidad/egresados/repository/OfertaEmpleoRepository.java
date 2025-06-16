package com.universidad.egresados.repository;

import com.universidad.egresados.model.OfertaEmpleo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OfertaEmpleoRepository extends JpaRepository<OfertaEmpleo, Long> {

    List<OfertaEmpleo> findByDescripcionContainingIgnoreCase(String descripcion);
    List<OfertaEmpleo> findByEstadoIgnoreCase(String estado);
    List<OfertaEmpleo> findByDescripcionContainingIgnoreCaseAndEstadoIgnoreCase(String descripcion, String estado);

    @Query("SELECT COUNT(a) FROM AplicacionOferta a WHERE a.ofertaEmpleo.id = :ofertaId")
    int countAplicacionesByOfertaId(@Param("ofertaId") Long ofertaId);
}