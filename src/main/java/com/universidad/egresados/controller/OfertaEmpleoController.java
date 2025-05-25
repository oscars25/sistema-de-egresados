package com.universidad.egresados.controller;

import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.model.AplicacionOferta;
import com.universidad.egresados.service.OfertaEmpleoService;
import com.universidad.egresados.service.AplicacionOfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

    @Autowired
    private AplicacionOfertaService aplicacionService;

    @GetMapping
    public String listarOfertas(Model model) {
        List<OfertaEmpleo> ofertas = service.listarTodas();
        model.addAttribute("ofertas", ofertas);
        return "lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(@AuthenticationPrincipal OidcUser principal, Model model) {
        List<String> roles = principal.getClaimAsStringList("roles");

        model.addAttribute("oferta", new OfertaEmpleo());

        if (roles != null && (roles.contains("admin") || roles.contains("Empresa"))) {
            return "formulario";
        } else if (roles != null && roles.contains("Egresado")) {
            return "formulario-estudiante";
        } else {
            return "redirect:/acceso-denegado";
        }
    }

    @PostMapping("/guardar")
    public String guardarOferta(
            @AuthenticationPrincipal OidcUser principal,
            @RequestParam(required = false) Long id,
            @RequestParam String descripcion,
            @RequestParam String requisitos,
            @RequestParam String fechaPublicacion,
            @RequestParam String estado
    ) {
        List<String> roles = principal.getClaimAsStringList("roles");
        if (roles == null || (!roles.contains("admin") && !roles.contains("Empresa"))) {
            return "redirect:/acceso-denegado";
        }

        OfertaEmpleo oferta = (id != null) ? service.obtenerPorId(id).orElse(new OfertaEmpleo()) : new OfertaEmpleo();

        oferta.setDescripcion(descripcion);
        oferta.setRequisitos(requisitos);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            oferta.setFechaPublicacion(LocalDate.parse(fechaPublicacion, formatter));
        } catch (Exception e) {
            return "redirect:/ofertas/crear";
        }

        oferta.setEstado(estado);
        service.guardar(oferta);

        return "redirect:/ofertas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, @AuthenticationPrincipal OidcUser principal) {
        List<String> roles = principal.getClaimAsStringList("roles");
        if (roles == null || (!roles.contains("admin") && !roles.contains("Empresa"))) {
            return "redirect:/acceso-denegado";
        }

        Optional<OfertaEmpleo> ofertaOpt = service.obtenerPorId(id);
        if (!ofertaOpt.isPresent()) {
            return "redirect:/ofertas";
        }

        model.addAttribute("oferta", ofertaOpt.get());
        return "formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarOferta(@PathVariable Long id, @AuthenticationPrincipal OidcUser principal) {
        List<String> roles = principal.getClaimAsStringList("roles");
        if (roles == null || (!roles.contains("admin") && !roles.contains("Empresa"))) {
            return "redirect:/acceso-denegado";
        }

        service.eliminar(id);
        return "redirect:/ofertas";
    }

    // ✅ Mostrar detalles para aplicar
    @GetMapping("/ver/{id}")
    public String verOferta(@PathVariable Long id, Model model, @AuthenticationPrincipal OidcUser principal) {
        Optional<OfertaEmpleo> ofertaOpt = service.obtenerPorId(id);
        if (!ofertaOpt.isPresent()) {
            return "redirect:/ofertas";
        }

        model.addAttribute("oferta", ofertaOpt.get());
        return "formulario-estudiante";
    }

    // ✅ Aplicar a una oferta
    @PostMapping("/aplicar/{id}")
    public String aplicarOferta(@PathVariable Long id, @AuthenticationPrincipal OidcUser principal) {
        Optional<OfertaEmpleo> ofertaOpt = service.obtenerPorId(id);
        if (!ofertaOpt.isPresent()) {
            return "redirect:/ofertas";
        }

        AplicacionOferta aplicacion = new AplicacionOferta();
        aplicacion.setOfertaEmpleo(ofertaOpt.get());  // Cambiado para coincidir con el setter en la entidad
        aplicacion.setEmailEgresado(principal.getClaim("email"));  // Cambiado para obtener email correctamente
        aplicacion.setFechaAplicacion(LocalDate.now());

        aplicacionService.guardar(aplicacion);

        return "redirect:/ofertas";
    }
}
