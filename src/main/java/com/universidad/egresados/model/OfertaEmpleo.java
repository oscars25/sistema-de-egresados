package com.universidad.egresados.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ofertas_empleo")
public class OfertaEmpleo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private String requisitos;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion; // Asegura que sea LocalDate

    private String estado;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    // Se espera un LocalDate, no un String
    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
