package com.ua.hiah.repository.source;

import com.ua.hiah.model.source.SourceDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceDatabaseRepository extends JpaRepository<SourceDatabase, Long> {

    SourceDatabase findByDatabaseName(String name);
}
