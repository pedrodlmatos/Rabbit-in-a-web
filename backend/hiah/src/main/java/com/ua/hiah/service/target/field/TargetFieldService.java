package com.ua.hiah.service.target.field;

import com.ua.hiah.model.target.TargetField;

public interface TargetFieldService {

    TargetField createField(TargetField field);

    TargetField getFieldById(Long target_id);

    TargetField changeComment(Long field, String comment);
}
