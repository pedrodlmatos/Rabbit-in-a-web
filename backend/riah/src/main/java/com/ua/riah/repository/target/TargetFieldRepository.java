package com.ua.riah.repository.target;

import com.ua.riah.model.target.TargetField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetFieldRepository extends JpaRepository<TargetField, Long> {
}
