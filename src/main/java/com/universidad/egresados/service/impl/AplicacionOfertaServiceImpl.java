package com.universidad.egresados.service.impl;

import com.universidad.egresados.model.AplicacionOferta;
import com.universidad.egresados.repository.AplicacionOfertaRepository;
import com.universidad.egresados.service.AplicacionOfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AplicacionOfertaServiceImpl implements AplicacionOfertaService {

    @Autowired
    private AplicacionOfertaRepository repository;

    @Override
    public AplicacionOferta guardar(AplicacionOferta aplicacionOferta) {
        return repository.save(aplicacionOferta);
    }

    @Override
    public List<AplicacionOferta> listarPorEgresado(String emailEgresado) {
        return repository.findByEmailEgresado(emailEgresado);
    }
}
