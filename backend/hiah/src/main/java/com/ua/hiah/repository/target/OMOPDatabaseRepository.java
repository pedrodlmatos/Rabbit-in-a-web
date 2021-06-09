package com.ua.hiah.repository.target;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.omop.OMOPDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OMOPDatabaseRepository extends JpaRepository<OMOPDatabase, Long> {

    OMOPDatabase findOMOPDatabaseByVersion(CDMVersion version);

    void deleteById(Long databaseId);
}
