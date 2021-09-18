package com.ua.riaw.etlProcedure.source.valueCounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValueCountRepository extends JpaRepository<ValueCount, Long> {
}
