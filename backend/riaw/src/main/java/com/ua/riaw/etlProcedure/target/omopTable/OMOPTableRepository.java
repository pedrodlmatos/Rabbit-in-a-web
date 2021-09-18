package com.ua.riaw.etlProcedure.target.omopTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OMOPTableRepository extends JpaRepository<OMOPTable, Long> {
}
