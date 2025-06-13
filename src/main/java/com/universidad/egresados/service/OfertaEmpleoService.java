package com.universidad.egresados.service;

import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.repository.OfertaEmpleoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfertaEmpleoService {

    @Autowired
    private OfertaEmpleoRepository repo;

    public List<OfertaEmpleo> listarTodas() {
        return repo.findAll();
    }

    public Optional<OfertaEmpleo> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public void guardar(OfertaEmpleo oferta) {
        repo.save(oferta);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // Nuevo método para búsqueda filtrada
    public List<OfertaEmpleo> buscarOfertas(String keyword, String estado) {
        if ((keyword == null || keyword.isBlank()) && (estado == null || estado.isBlank())) {
            return listarTodas();
        } else if ((keyword != null && !keyword.isBlank()) && (estado == null || estado.isBlank())) {
            return repo.findByDescripcionContainingIgnoreCase(keyword);
        } else if ((keyword == null || keyword.isBlank()) && (estado != null && !estado.isBlank())) {
            return repo.findByEstado(estado);
        } else {
            return repo.findByDescripcionContainingIgnoreCaseAndEstado(keyword, estado);
        }
    }

}

