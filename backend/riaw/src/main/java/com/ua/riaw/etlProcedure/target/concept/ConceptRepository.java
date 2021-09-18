package com.ua.riaw.etlProcedure.target.concept;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long> {
}
