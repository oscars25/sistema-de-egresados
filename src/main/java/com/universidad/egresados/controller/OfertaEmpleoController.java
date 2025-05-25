package com.universidad.egresados.controller;

import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.service.OfertaEmpleoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ofertas")
public class OfertaEmpleoController {

    @Autowired
    private OfertaEmpleoService service;

    // Lista todas las ofertas de empleo
    @GetMapping
    public String listarOfertas(Model model) {
        List<OfertaEmpleo> ofertas = service.listarTodas();
        model.addAttribute("ofertas", ofertas);
        return "lista";
    }

    // Muestra el formulario para crear una oferta
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("oferta", new OfertaEmpleo());
        return "formulario";
    }

    // Guarda la oferta de empleo
    @PostMapping("/guardar")
    public String guardarOferta(
            @RequestParam(required = false) Long id,
            @RequestParam String descripcion,
            @RequestParam String requisitos,
            @RequestParam String fechaPublicacion, // Llega como String y se convierte a LocalDate
            @RequestParam String estado
    ) {
        // Crear o actualizar la oferta
        OfertaEmpleo oferta = (id != null) ? service.obtenerPorId(id).orElse(new OfertaEmpleo()) : new OfertaEmpleo();
        
        oferta.setDescripcion(descripcion);
        oferta.setRequisitos(requisitos);

        // Convertir la fecha de String a LocalDate
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            oferta.setFechaPublicacion(LocalDate.parse(fechaPublicacion, formatter));
        } catch (Exception e) {
            System.out.println("Error al convertir la fecha: " + e.getMessage());
            return "redirect:/ofertas/crear"; // En caso de error, redirige al formulario
        }

        oferta.setEstado(estado);

        // Guardamos la oferta en la BD
        service.guardar(oferta);
        
        return "redirect:/ofertas";
    }

    // Editar una oferta de empleo
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<OfertaEmpleo> ofertaOpt = service.obtenerPorId(id);
        
        if (!ofertaOpt.isPresent()) {
            return "redirect:/ofertas"; // Si no existe, redirige a la lista
        }

        model.addAttribute("oferta", ofertaOpt.get());
        return "formulario";
    }

    // Eliminar una oferta
    @GetMapping("/eliminar/{id}")
    public String eliminarOferta(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/ofertas";
    }
}
