package com.universidad.egresados.controller;

import com.universidad.egresados.model.ContactMessage;
import com.universidad.egresados.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/contacto")
public class ContactMessageController {

    private final ContactMessageService service;

    public ContactMessageController(ContactMessageService service) {
        this.service = service;
    }

    // Mostrar formulario de contacto vacío
    @GetMapping
    public String showForm(ContactMessage contactMessage) {
        return "contacto";  // Nombre de la plantilla Thymeleaf contacto.html
    }

    // Procesar envío del formulario
    @PostMapping
    public String submitForm(@Valid ContactMessage contactMessage, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "contacto";
        }
        service.saveMessage(contactMessage);
        model.addAttribute("successMessage", "Mensaje enviado correctamente");
        return "contacto";
    }

    // Mostrar todos los mensajes recibidos (opcional)
    @GetMapping("/messages")
    public String listMessages(Model model) {
        List<ContactMessage> messages = service.getAllMessages();
        model.addAttribute("messages", messages);
        return "contact_messages";  // Vista para listar mensajes
    }
}
