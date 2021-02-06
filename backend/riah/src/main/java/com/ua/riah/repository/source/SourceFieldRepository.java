package com.ua.riah.repository.source;

import com.ua.riah.model.source.SourceField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceFieldRepository extends JpaRepository<SourceField, Long> {
}