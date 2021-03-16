package com.ua.hiah.service.target.targetField;

import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.repository.target.TargetFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetFieldServiceImpl implements TargetFieldService {

    @Autowired
    private TargetFieldRepository repository;


    @Override
    public TargetField createField(TargetField field) {
        return repository.save(field);
    }

    @Override
    public TargetField getFieldById(Long target_id) {
        return repository.findById(target_id).orElse(null);
    }
}
