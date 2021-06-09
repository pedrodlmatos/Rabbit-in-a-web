package com.ua.hiah.repository.ehr;

import com.ua.hiah.model.ehr.EHRDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EHRDatabaseRepository extends JpaRepository<EHRDatabase, Long> {

    EHRDatabase findByDatabaseName(String name);
}
