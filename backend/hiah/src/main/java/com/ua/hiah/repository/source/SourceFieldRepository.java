package com.ua.hiah.repository.source;

import com.ua.hiah.model.source.SourceField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceFieldRepository extends JpaRepository<SourceField, Long> {
}
