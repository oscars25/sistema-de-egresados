package com.universidad.egresados.controller;

import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.model.AplicacionOferta;
import com.universidad.egresados.service.OfertaEmpleoService;
import com.universidad.egresados.service.AplicacionOfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    public String listarOfertas(Model model, 
                               @AuthenticationPrincipal OidcUser principal, 
                               Authentication authentication) {
        List<OfertaEmpleo> ofertas = service.listarTodas();
        model.addAttribute("ofertas", ofertas);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        model.addAttribute("esEmpresaOAdmin", roles.contains("role_admin") || roles.contains("role_empresa"));
        model.addAttribute("esEgresado", roles.contains("role_egresado"));

        return "lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Authentication authentication, Model model) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        if (roles.contains("role_admin") || roles.contains("role_empresa")) {
            model.addAttribute("oferta", new OfertaEmpleo());
            return "formulario";
        }
        return "redirect:/acceso-denegado";
    }

    @PostMapping("/guardar")
    public String guardarOferta(Authentication authentication,
                               @RequestParam(required = false) Long id,
                               @RequestParam String descripcion,
                               @RequestParam String requisitos,
                               @RequestParam(required = false) String fechaPublicacion,
                               @RequestParam String estado) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        if (!roles.contains("role_admin") && !roles.contains("role_empresa")) {
            return "redirect:/acceso-denegado";
        }

        OfertaEmpleo oferta = (id != null) ? service.obtenerPorId(id).orElse(new OfertaEmpleo()) : new OfertaEmpleo();
        oferta.setDescripcion(descripcion);
        oferta.setRequisitos(requisitos);

        if (fechaPublicacion != null && !fechaPublicacion.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                oferta.setFechaPublicacion(LocalDate.parse(fechaPublicacion, formatter));
            } catch (Exception e) {
                oferta.setFechaPublicacion(LocalDate.now());
            }
        } else {
            oferta.setFechaPublicacion(LocalDate.now());
        }

        oferta.setEstado(estado);

        // Obtener datos de empresa y correo desde OidcUser
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String nombreEmpresa = oidcUser.getAttribute("name"); // o "nickname"
        String correoEmpresa = oidcUser.getAttribute("email"); // correo del usuario autenticado

        oferta.setEmpresa(nombreEmpresa);
        oferta.setCorreoEmpresa(correoEmpresa);

        service.guardar(oferta);
        return "redirect:/ofertas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        if (!roles.contains("role_admin") && !roles.contains("role_empresa")) {
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
    public String eliminarOferta(@PathVariable Long id, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        if (!roles.contains("role_admin") && !roles.contains("role_empresa")) {
            return "redirect:/acceso-denegado";
        }

        service.eliminar(id);
        return "redirect:/ofertas";
    }

    @GetMapping("/ver/{id}")
    public String verOferta(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<OfertaEmpleo> ofertaOpt = service.obtenerPorId(id);
        if (!ofertaOpt.isPresent()) {
            return "redirect:/ofertas";
        }

        OfertaEmpleo oferta = ofertaOpt.get();
        model.addAttribute("oferta", oferta);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        model.addAttribute("esEmpresaOAdmin", roles.contains("role_admin") || roles.contains("role_empresa"));

        return "formulario_aplicar";
    }

    @PostMapping("/aplicar/{id}")
    public String aplicarOferta(@PathVariable Long id, 
                                @AuthenticationPrincipal OidcUser principal, 
                                Authentication authentication,
                                Model model) {
        Optional<OfertaEmpleo> ofertaOpt = service.obtenerPorId(id);
        if (!ofertaOpt.isPresent()) {
            return "redirect:/ofertas";
        }

        AplicacionOferta aplicacion = new AplicacionOferta();
        aplicacion.setOfertaEmpleo(ofertaOpt.get());
        aplicacion.setEmailEgresado(principal.getClaim("email"));
        aplicacion.setFechaAplicacion(LocalDate.now());

        aplicacionService.guardar(aplicacion);

        OfertaEmpleo oferta = ofertaOpt.get();
        model.addAttribute("oferta", oferta);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        model.addAttribute("esEmpresaOAdmin", roles.contains("role_admin") || roles.contains("role_empresa"));

        model.addAttribute("exito", true); // Para mostrar modal de confirmación
        return "formulario_aplicar";
    }
}
