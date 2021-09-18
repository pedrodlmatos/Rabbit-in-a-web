package com.ua.riaw.etlProcedure.source.ehrDatabase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EHRDatabaseRepository extends JpaRepository<EHRDatabase, Long> {

}
