package com.ua.riaw.repository.target;

import com.ua.riaw.model.CDMVersion;
import com.ua.riaw.model.omop.OMOPDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OMOPDatabaseRepository extends JpaRepository<OMOPDatabase, Long> {

    OMOPDatabase findOMOPDatabaseByVersion(CDMVersion version);

    void deleteById(Long databaseId);
}
