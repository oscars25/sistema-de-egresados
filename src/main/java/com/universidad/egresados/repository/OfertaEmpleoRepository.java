package com.universidad.egresados.repository;

import com.universidad.egresados.model.OfertaEmpleo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfertaEmpleoRepository extends JpaRepository<OfertaEmpleo, Long> {

    // Busca en descripción, ignorando mayúsculas/minúsculas
    List<OfertaEmpleo> findByDescripcionContainingIgnoreCase(String descripcion);

    // Busca por estado, ignorando mayúsculas/minúsculas
    List<OfertaEmpleo> findByEstadoIgnoreCase(String estado);

    // Busca por descripción y estado, ignorando mayúsculas/minúsculas en ambos
    List<OfertaEmpleo> findByDescripcionContainingIgnoreCaseAndEstadoIgnoreCase(String descripcion, String estado);

}
