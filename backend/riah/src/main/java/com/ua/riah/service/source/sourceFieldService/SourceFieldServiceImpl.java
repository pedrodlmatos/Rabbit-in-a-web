package com.ua.riah.service.source.sourceFieldService;

import com.ua.riah.model.source.SourceField;
import com.ua.riah.repository.source.SourceFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceFieldServiceImpl implements SourceFieldService {

    @Autowired
    private SourceFieldRepository repository;

    @Override
    public SourceField createField(SourceField field) {
        return repository.save(field);
    }

    @Override
    public SourceField getFieldById(Long source_id) {
        return repository.findById(source_id).orElse(null);
    }
}
