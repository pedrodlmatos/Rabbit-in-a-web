package com.ua.hiah.repository;

import com.ua.hiah.model.ETL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ETLRepository extends JpaRepository<ETL, Long> {
}
