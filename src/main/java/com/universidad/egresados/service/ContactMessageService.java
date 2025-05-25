package com.universidad.egresados.service;

import com.universidad.egresados.model.ContactMessage;
import com.universidad.egresados.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository repository;

    public ContactMessageService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    public ContactMessage saveMessage(ContactMessage message) {
        return repository.save(message);
    }

    public List<ContactMessage> getAllMessages() {
        return repository.findAll();
    }

    public ContactMessage getMessageById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }
}
