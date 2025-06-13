package com.universidad.egresados.repository;

import com.universidad.egresados.model.OfertaEmpleo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfertaEmpleoRepository extends JpaRepository<OfertaEmpleo, Long> {

    List<OfertaEmpleo> findByTituloContainingIgnoreCase(String titulo);

    List<OfertaEmpleo> findByCategoria(String categoria);

    List<OfertaEmpleo> findByTituloContainingIgnoreCaseAndCategoria(String titulo, String categoria);

}
