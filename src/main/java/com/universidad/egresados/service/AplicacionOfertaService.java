package com.universidad.egresados.service;

import com.universidad.egresados.model.AplicacionOferta;
import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.repository.AplicacionOfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AplicacionOfertaService {

    @Autowired
    private AplicacionOfertaRepository repository;

    public List<AplicacionOferta> listarTodas() {
        return repository.findAll();
    }

    public Optional<AplicacionOferta> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    public AplicacionOferta guardar(AplicacionOferta aplicacionOferta) {
        return repository.save(aplicacionOferta);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public List<AplicacionOferta> listarPorEmailEgresado(String emailEgresado) {
        return repository.findByEmailEgresado(emailEgresado);
    }

    public List<AplicacionOferta> listarPorOferta(Long ofertaId) {
        return repository.findByOfertaEmpleoId(ofertaId);
    }

    // MÉTODO NUEVO para verificar si ya aplicó el egresado a esa oferta
    public boolean existeAplicacion(String email, Long idOferta) {
        return repository.findByEmailEgresadoAndOfertaEmpleoId(email, idOferta).isPresent();
    }

    // MÉTODO NUEVO para crear una aplicación nueva
public void crearAplicacion(String emailEgresado, OfertaEmpleo oferta) {
    AplicacionOferta aplicacion = new AplicacionOferta();
    aplicacion.setEmailEgresado(emailEgresado);
    aplicacion.setOfertaEmpleo(oferta);
    aplicacion.setFechaAplicacion(LocalDate.now());  // <-- aquí solo fecha

    repository.save(aplicacion);
}
}
