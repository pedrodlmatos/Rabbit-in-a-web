package com.ua.riah.repository;

import com.ua.riah.model.ETL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ETLRepository extends JpaRepository<ETL, String> {
}
