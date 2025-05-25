package com.universidad.egresados.controller;

import com.universidad.egresados.model.OfertaEmpleo;
import com.universidad.egresados.service.OfertaEmpleoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PerfilController {

    @Autowired
    private OfertaEmpleoService ofertaService;

    @GetMapping("/perfil")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRESA', 'EGRESADO')")
    public String mostrarPerfil(@AuthenticationPrincipal OidcUser user, Authentication authentication, Model model) {
        if (user == null) {
            return "redirect:/error";
        }

        model.addAttribute("usuario", user.getFullName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("foto", user.getPicture());

        // Extraemos roles sin prefijo ROLE_
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();

        model.addAttribute("roles", roles);

        if (roles.contains("ADMIN") || roles.contains("EMPRESA")) {
            // Perfil para admin o empresa
            return "perfil";
        }

        if (roles.contains("EGRESADO")) {
            // Perfil para egresado con lista de ofertas
            List<OfertaEmpleo> ofertas = ofertaService.listarTodas();
            model.addAttribute("ofertas", ofertas);
            return "perfil-egresado";
        }

        // Si no tiene rol válido
        return "redirect:/acceso-denegado";
    }
}
