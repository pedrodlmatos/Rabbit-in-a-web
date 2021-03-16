package com.ua.hiah.service.source.sourceFieldService;

import com.ua.hiah.model.source.SourceField;

public interface SourceFieldService {

    SourceField createField(SourceField field);

    SourceField getFieldById(Long source_id);
}
