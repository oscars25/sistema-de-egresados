package com.universidad.egresados.repository;

import com.universidad.egresados.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    // JpaRepository ya provee métodos básicos (save, findAll, findById, delete, etc)
}
