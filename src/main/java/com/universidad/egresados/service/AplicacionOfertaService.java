package com.universidad.egresados.service;

import com.universidad.egresados.model.AplicacionOferta;

import java.util.List;

public interface AplicacionOfertaService {
    AplicacionOferta guardar(AplicacionOferta aplicacionOferta);
    List<AplicacionOferta> listarPorEgresado(String emailEgresado);
}
