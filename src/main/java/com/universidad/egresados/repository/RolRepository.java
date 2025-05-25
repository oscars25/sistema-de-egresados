package com.universidad.egresados.repository;

import com.universidad.egresados.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Aquí puedes agregar consultas personalizadas si es necesario
}
