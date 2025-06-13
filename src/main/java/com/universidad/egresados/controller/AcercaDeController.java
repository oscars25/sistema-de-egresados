package com.universidad.egresados.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcercaDeController {

    @GetMapping("/acercade")
    public String mostrarAcercaDe() {
        return "acercade";
    }
}
