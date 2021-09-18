package com.ua.riaw.etlProcedure.target.omopDatabase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OMOPDatabaseRepository extends JpaRepository<OMOPDatabase, Long> {

    void deleteById(Long databaseId);
}
