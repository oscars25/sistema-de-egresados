package com.universidad.egresados.service;

import com.universidad.egresados.model.Usuario;
import com.universidad.egresados.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar nuevo usuario con contraseña cifrada
    public boolean registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername()) ||
            usuarioRepository.existsByEmail(usuario.getEmail())) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encodedPassword);
        usuarioRepository.save(usuario);
        return true;
    }

    // Buscar usuario por nombre de usuario
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Nuevo método opcional: obtener usuario por ID
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Método para obtener todos los usuarios, si lo necesitas
    public Iterable<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
