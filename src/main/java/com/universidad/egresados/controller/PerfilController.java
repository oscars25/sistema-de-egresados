package com.universidad.egresados.controller;
import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.model.AplicacionOferta;
import com.universidad.egresados.service.OfertaEmpleoService;
import com.universidad.egresados.service.AplicacionOfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class PerfilController {

    @Autowired
    private OfertaEmpleoService ofertaService;

    @Autowired
    private AplicacionOfertaService aplicacionService;

    @GetMapping("/perfil")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRESA', 'EGRESADO')")
    public String mostrarPerfil(@AuthenticationPrincipal OidcUser user,
                                Authentication authentication,
                                Model model,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String estado,
                                @ModelAttribute("mensaje") String mensajeFlash,
                                @ModelAttribute("error") String errorFlash) {

        if (user == null) {
            return "redirect:/error";
        }

        model.addAttribute("usuario", user.getFullName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("foto", user.getPicture());

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();

        model.addAttribute("roles", roles);

        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("estado", estado != null ? estado : "");

        if (mensajeFlash != null && !mensajeFlash.isEmpty()) {
            model.addAttribute("mostrarModalExito", true);
            model.addAttribute("mensajeModal", mensajeFlash);
        } else {
            model.addAttribute("mostrarModalExito", false);
        }

        if (errorFlash != null && !errorFlash.isEmpty()) {
            model.addAttribute("mostrarModalError", true);
            model.addAttribute("mensajeModalError", errorFlash);
        } else {
            model.addAttribute("mostrarModalError", false);
        }

        if (roles.contains("ADMIN") || roles.contains("EMPRESA")) {
            return "perfil";
        }

        if (roles.contains("EGRESADO")) {
            List<OfertaEmpleo> ofertas = ofertaService.buscarPorPalabraClaveYEstado(keyword, estado);
            model.addAttribute("ofertas", ofertas != null ? ofertas : Collections.emptyList());
            return "perfil-egresado";
        }

        return "redirect:/acceso-denegado";
    }

    @GetMapping("/perfil/formulario_aplicar")
    @PreAuthorize("hasAnyRole('EGRESADO', 'ADMIN', 'EMPRESA')")
    public String mostrarFormularioAplicar(@RequestParam Long id,
                                           Model model,
                                           Authentication authentication) {
        var ofertaOpt = ofertaService.obtenerPorId(id);
        if (ofertaOpt.isEmpty()) {
            return "redirect:/perfil";
        }
        OfertaEmpleo oferta = ofertaOpt.get();
        model.addAttribute("oferta", oferta);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();

        model.addAttribute("esEmpresaOAdmin", roles.contains("ADMIN") || roles.contains("EMPRESA"));

        return "formulario_aplicar";
    }

    @PostMapping("/perfil/aplicar")
    @PreAuthorize("hasRole('EGRESADO')")
    public String aplicarOferta(@RequestParam Long id,
                                @AuthenticationPrincipal OidcUser principal,
                                RedirectAttributes redirectAttributes) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }

            var ofertaOpt = ofertaService.obtenerPorId(id);
            if (ofertaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Oferta no encontrada.");
                return "redirect:/perfil";
            }

            String email = principal.getClaim("email");

            boolean yaAplico = aplicacionService.existeAplicacion(email, id);
            if (yaAplico) {
                redirectAttributes.addFlashAttribute("error", "Ya aplicaste a esta oferta.");
                return "redirect:/perfil";
            }

            AplicacionOferta aplicacion = new AplicacionOferta();
            aplicacion.setOfertaEmpleo(ofertaOpt.get());
            aplicacion.setEmailEgresado(email);
            aplicacion.setFechaAplicacion(LocalDate.now());

            aplicacionService.guardar(aplicacion);

            redirectAttributes.addFlashAttribute("mensaje", "Aplicación enviada con éxito.");
            return "redirect:/perfil";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al aplicar: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    // --- Método para crear/guardar oferta con correo empresa asignado ---
@PostMapping("/perfil/ofertas/guardar")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPRESA')")
public String guardarOferta(@ModelAttribute OfertaEmpleo oferta,
                            @AuthenticationPrincipal OidcUser principal,
                            RedirectAttributes redirectAttributes) {
    try {
        if (principal == null) {
            return "redirect:/login";
        }

        // Depurar claims
        principal.getClaims().forEach((k, v) -> System.out.println("Claim: " + k + " -> " + v));

        // Intentar obtener email del claim "email"
        String correoEmpresa = principal.getAttribute("email");
        System.out.println("Correo empresa desde Auth0: " + correoEmpresa);

        oferta.setCorreoEmpresa(correoEmpresa);

        // Asignar nombre empresa si no viene en el formulario
        if (oferta.getEmpresa() == null || oferta.getEmpresa().isBlank()) {
            String empresaNombre = principal.getAttribute("nickname");
            if (empresaNombre != null) {
                oferta.setEmpresa(empresaNombre);
            }
        }

        if (oferta.getFechaPublicacion() == null) {
            oferta.setFechaPublicacion(LocalDate.now());
        }

        ofertaService.guardar(oferta);

        redirectAttributes.addFlashAttribute("mensaje", "Oferta guardada correctamente.");
        return "redirect:/perfil";

    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("error", "Error al guardar la oferta: " + e.getMessage());
        return "redirect:/perfil";
    }
}

}
