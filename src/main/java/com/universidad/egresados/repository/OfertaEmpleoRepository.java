package com.universidad.egresados.repository;

import com.universidad.egresados.model.OfertaEmpleo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfertaEmpleoRepository extends JpaRepository<OfertaEmpleo, Long> {

    List<OfertaEmpleo> findByDescripcionContainingIgnoreCase(String descripcion);

    List<OfertaEmpleo> findByEstado(String estado);

    List<OfertaEmpleo> findByDescripcionContainingIgnoreCaseAndEstado(String descripcion, String estado);
}
