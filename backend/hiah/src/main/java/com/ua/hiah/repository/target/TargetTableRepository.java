package com.ua.hiah.repository.target;

import com.ua.hiah.model.target.TargetTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetTableRepository extends JpaRepository<TargetTable, Long> {

    List<TargetTable> findAllByTargetDatabaseId(Long databaseId);
}
