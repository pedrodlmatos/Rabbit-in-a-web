package com.ua.riah.service.target.targetField;

import com.ua.riah.model.target.TargetField;

public interface TargetFieldService {

    TargetField createField(TargetField field);

    TargetField getFieldById(Long target_id);
}
