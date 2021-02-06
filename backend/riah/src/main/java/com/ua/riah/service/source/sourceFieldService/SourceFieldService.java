package com.ua.riah.service.source.sourceFieldService;

import com.ua.riah.model.source.SourceField;

public interface SourceFieldService {

    SourceField createField(SourceField field);

    SourceField getFieldById(Long source_id);
}
