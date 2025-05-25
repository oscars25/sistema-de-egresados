package com.universidad.egresados.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "aplicaciones_oferta")
public class AplicacionOferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private OfertaEmpleo ofertaEmpleo;

    @NotNull
    private String emailEgresado;

    @NotNull
    private LocalDate fechaAplicacion;

    public AplicacionOferta() {}

    public AplicacionOferta(OfertaEmpleo ofertaEmpleo, String emailEgresado, LocalDate fechaAplicacion) {
        this.ofertaEmpleo = ofertaEmpleo;
        this.emailEgresado = emailEgresado;
        this.fechaAplicacion = fechaAplicacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OfertaEmpleo getOfertaEmpleo() {
        return ofertaEmpleo;
    }

    public void setOfertaEmpleo(OfertaEmpleo ofertaEmpleo) {
        this.ofertaEmpleo = ofertaEmpleo;
    }

    public String getEmailEgresado() {
        return emailEgresado;
    }

    public void setEmailEgresado(String emailEgresado) {
        this.emailEgresado = emailEgresado;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }
}
