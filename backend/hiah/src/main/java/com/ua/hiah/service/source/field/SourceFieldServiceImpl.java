package com.ua.hiah.service.source.field;

import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.repository.source.SourceFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceFieldServiceImpl implements SourceFieldService{

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
