package com.ua.hiah.service.target.targetField;

import com.ua.hiah.model.target.TargetField;

public interface TargetFieldService {

    TargetField createField(TargetField field);

    TargetField getFieldById(Long target_id);
}
