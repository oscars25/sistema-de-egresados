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

    public List<OfertaEmpleo> buscarPorPalabraClave(String palabraClave) {
        if (palabraClave == null || palabraClave.trim().isEmpty()) {
            return repo.findAll();
        }
        String keyword = palabraClave.trim().toLowerCase();
        return repo.findByDescripcionContainingIgnoreCase(keyword);
    }

    public List<OfertaEmpleo> buscarPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return repo.findAll();
        }
        return repo.findByEstadoIgnoreCase(estado.trim());
    }

    public List<OfertaEmpleo> buscarPorPalabraClaveYEstado(String palabraClave, String estado) {
        boolean palabraVacia = (palabraClave == null || palabraClave.trim().isEmpty());
        boolean estadoVacio = (estado == null || estado.trim().isEmpty());

        if (palabraVacia && estadoVacio) {
            return repo.findAll();
        } else if (palabraVacia) {
            return repo.findByEstadoIgnoreCase(estado.trim());
        } else if (estadoVacio) {
            return repo.findByDescripcionContainingIgnoreCase(palabraClave.trim());
        } else {
            return repo.findByDescripcionContainingIgnoreCaseAndEstadoIgnoreCase(palabraClave.trim(), estado.trim());
        }
    }

    public int contarAplicacionesPorOferta(Long ofertaId) {
        return repo.countAplicacionesByOfertaId(ofertaId);
    }
}