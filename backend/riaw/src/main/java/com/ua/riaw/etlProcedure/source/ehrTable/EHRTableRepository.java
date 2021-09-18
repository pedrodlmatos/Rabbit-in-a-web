package com.ua.riaw.etlProcedure.source.ehrTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EHRTableRepository extends JpaRepository<EHRTable, Long> {
}
