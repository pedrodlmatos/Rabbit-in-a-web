package com.ua.riah.repository.target;

import com.ua.riah.model.target.TargetTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetTableRepository extends JpaRepository<TargetTable, Long> {
}
