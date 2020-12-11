package com.ua.riah.repository;

import com.ua.riah.model.DBTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBTableRepository extends JpaRepository<DBTable, String> {
}