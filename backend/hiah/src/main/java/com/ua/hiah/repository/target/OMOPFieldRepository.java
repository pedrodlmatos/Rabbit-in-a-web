package com.ua.hiah.repository.target;

import com.ua.hiah.model.omop.OMOPField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OMOPFieldRepository extends JpaRepository<OMOPField, Long> {
}
