package com.ua.riah.repository.source;

import com.ua.riah.model.source.SourceTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceTableRepository extends JpaRepository<SourceTable, Long> {
}
