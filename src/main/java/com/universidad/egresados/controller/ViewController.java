package com.universidad.egresados.controller;

import com.universidad.egresados.model.Rol;
import com.universidad.egresados.model.Usuario;
import com.universidad.egresados.service.RolService;
import com.universidad.egresados.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ViewController {
    private final UsuarioService usuarioService;
    private final RolService rolService;

    public ViewController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

@GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/registro")
    public String showRegistroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.findAll());
        return "registro";
    }

    @PostMapping("/registro")
public String registrarUsuario(
    @ModelAttribute Usuario usuario,
    @RequestParam("rolId") Long rolId,
    Model model
) {
    Rol rol = rolService.findById(rolId);
    if (rol == null) {
        model.addAttribute("error", "Rol no encontrado.");
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.findAll());
        return "registro";
    }

    usuario.setRoles(List.of(rol));

    boolean registrado = usuarioService.registrarUsuario(usuario);
    if (!registrado) {
        model.addAttribute("error", "El usuario o email ya están en uso.");
        model.addAttribute("roles", rolService.findAll());
        return "registro";
    }

    return "redirect:/login";
    }
}
