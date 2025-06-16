package com.universidad.egresados.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ofertas_empleo")
public class OfertaEmpleo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private String requisitos;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    private String estado;
    private String empresa;
    private String correoEmpresa;

    @OneToMany(mappedBy = "ofertaEmpleo")
    private List<AplicacionOferta> aplicaciones;

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

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCorreoEmpresa() {
        return correoEmpresa;
    }

    public void setCorreoEmpresa(String correoEmpresa) {
        this.correoEmpresa = correoEmpresa;
    }

    public List<AplicacionOferta> getAplicaciones() {
        return aplicaciones;
    }

    public void setAplicaciones(List<AplicacionOferta> aplicaciones) {
        this.aplicaciones = aplicaciones;
    }
}