package com.universidad.egresados.controller;

import com.universidad.egresados.model.AplicacionOferta;
import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.service.AplicacionOfertaService;
import com.universidad.egresados.service.OfertaEmpleoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/aplicaciones")
public class AplicacionOfertaController {

    @Autowired
    private AplicacionOfertaService aplicacionService;

    @Autowired
    private OfertaEmpleoService ofertaService;

    // Agrega el token CSRF a todas las vistas de este controlador
    @ModelAttribute
    public void addCsrfToken(Model model, HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", token);
    }

    // Listar todas las aplicaciones - solo para roles admin o empresa
    @GetMapping
    public String listarAplicaciones(Model model, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        if (!roles.contains("role_admin") && !roles.contains("role_empresa")) {
            return "redirect:/acceso-denegado";
        }

        List<AplicacionOferta> aplicaciones = aplicacionService.listarTodas();
        model.addAttribute("aplicaciones", aplicaciones);
        return "lista_aplicaciones";
    }

    // Listar aplicaciones del egresado autenticado
    @GetMapping("/mis-aplicaciones")
    public String listarAplicacionesEgresado(Model model, @AuthenticationPrincipal OidcUser principal) {
        String email = principal.getClaim("email");
        List<AplicacionOferta> aplicaciones = aplicacionService.listarPorEmailEgresado(email);
        model.addAttribute("aplicaciones", aplicaciones);
        return "mis_aplicaciones";
    }

    // Eliminar aplicación - solo admin o empresa
    @GetMapping("/eliminar/{id}")
    public String eliminarAplicacion(@PathVariable Long id, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();

        if (!roles.contains("role_admin") && !roles.contains("role_empresa")) {
            return "redirect:/acceso-denegado";
        }

        aplicacionService.eliminar(id);
        return "redirect:/aplicaciones";
    }

    // Detalle de aplicación con control de permisos
    @GetMapping("/detalle/{id}")
    public String detalleAplicacion(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<AplicacionOferta> aplicacionOpt = aplicacionService.obtenerPorId(id);

        if (aplicacionOpt.isEmpty()) {
            return "redirect:/aplicaciones";
        }

        model.addAttribute("aplicacion", aplicacionOpt.get());

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();
        boolean puedeEditar = roles.contains("role_admin") || roles.contains("role_empresa");
        model.addAttribute("puedeEditar", puedeEditar);

        return "detalle_aplicacion";
    }

    // Mostrar formulario para aplicar a una oferta
    @GetMapping("/aplicar/{idOferta}")
    public String mostrarFormularioAplicar(@PathVariable Long idOferta,
                                           @RequestParam(required = false) String exito,
                                           @RequestParam(required = false) String error,
                                           Model model,
                                           Authentication authentication,
                                           @AuthenticationPrincipal OidcUser principal) {

        Optional<OfertaEmpleo> ofertaOpt = ofertaService.obtenerPorId(idOferta);
        if (ofertaOpt.isEmpty()) {
            return "redirect:/perfil";
        }

        OfertaEmpleo oferta = ofertaOpt.get();
        model.addAttribute("oferta", oferta);
        model.addAttribute("exito", exito != null);
        model.addAttribute("error", error);

        // Nombre y correo de empresa (con valores por defecto si no existen)
        String nombreEmpresa = (oferta.getEmpresa() != null && !oferta.getEmpresa().isBlank())
                ? oferta.getEmpresa()
                : "Desconocida";
        model.addAttribute("nombreEmpresa", nombreEmpresa);

        String correoEmpresa = (oferta.getCorreoEmpresa() != null && !oferta.getCorreoEmpresa().isBlank())
                ? oferta.getCorreoEmpresa()
                : "No disponible";
        model.addAttribute("correoEmpresa", correoEmpresa);

        // Roles para controlar funcionalidades
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toLowerCase)
                .toList();
        boolean esEmpresaOAdmin = roles.contains("role_empresa") || roles.contains("role_admin");
        model.addAttribute("esEmpresaOAdmin", esEmpresaOAdmin);

        // Email usuario autenticado
        if (principal != null) {
            String email = principal.getClaim("email");
            model.addAttribute("emailUsuario", email);
        }

        return "formulario_aplicar";
    }

    // Acción POST para aplicar a una oferta
    @PostMapping("/aplicar/{idOferta}")
    public String aplicarOferta(@PathVariable Long idOferta,
                                @AuthenticationPrincipal OidcUser principal,
                                RedirectAttributes redirectAttributes) {
        String email = principal.getClaim("email");
        Optional<OfertaEmpleo> ofertaOpt = ofertaService.obtenerPorId(idOferta);

        if (ofertaOpt.isPresent()) {
            aplicacionService.crearAplicacion(email, ofertaOpt.get());
            redirectAttributes.addFlashAttribute("exitoAplicacion", true);
        } else {
            redirectAttributes.addFlashAttribute("errorAplicacion", "Oferta no encontrada");
        }

        return "redirect:/perfil";
    }
}
