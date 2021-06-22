package com.ua.riaw.repository.target;

import com.ua.riaw.model.omop.OMOPTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OMOPTableRepository extends JpaRepository<OMOPTable, Long> {

    List<OMOPTable> findAllByOmopDatabaseId(Long databaseId);
}
