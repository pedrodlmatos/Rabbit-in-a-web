package com.ua.hiah.service.source.field;

import com.ua.hiah.model.source.SourceField;

public interface SourceFieldService {
    SourceField createField(SourceField field);

    SourceField getFieldById(Long source_id);

    SourceField changeComment(Long field, String comment);
}
