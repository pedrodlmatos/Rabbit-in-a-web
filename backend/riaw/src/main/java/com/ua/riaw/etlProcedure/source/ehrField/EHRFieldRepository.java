package com.ua.riaw.etlProcedure.source.ehrField;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EHRFieldRepository extends JpaRepository<EHRField, Long> {
}
