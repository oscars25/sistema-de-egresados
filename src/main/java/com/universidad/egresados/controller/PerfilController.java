package com.universidad.egresados.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {

    @GetMapping("/perfil")
    public String mostrarPerfil(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("usuario", user.getFullName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("foto", user.getPicture());
        return "perfil";
    }
}
