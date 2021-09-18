package com.ua.riaw.etlProcedure.target.omopField;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OMOPFieldRepository extends JpaRepository<OMOPField, Long> {
}
