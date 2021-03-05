package com.ua.hiah.repository.source;

import com.ua.hiah.model.source.SourceTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceTableRepository extends JpaRepository<SourceTable, Long> {
}
