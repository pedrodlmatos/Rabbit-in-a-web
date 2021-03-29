package com.ua.hiah.service.target.concept;

import com.ua.hiah.model.target.Concept;
import com.ua.hiah.repository.target.ConceptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptServiceImpl implements ConceptService {

    @Autowired
    private ConceptRepository repository;

    @Override
    public Concept saveConcept(Concept concept) {
        return repository.save(concept);
    }
}
