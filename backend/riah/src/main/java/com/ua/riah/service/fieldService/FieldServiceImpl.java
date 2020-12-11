package com.ua.riah.service.fieldService;

import com.ua.riah.model.DBTable;
import com.ua.riah.model.Field;
import com.ua.riah.repository.FieldRepository;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldServiceImpl implements FieldService {

    @Autowired
    private FieldRepository fieldRepository;

    @Override
    public Field createField(Field field) {
        return fieldRepository.save(field);
    }
}
