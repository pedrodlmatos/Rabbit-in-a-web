package com.ua.riah.service.target.targetField;

import com.ua.riah.model.target.TargetField;
import com.ua.riah.repository.target.TargetFieldRepository;
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
