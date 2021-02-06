package com.ua.riah.repository;

import com.ua.riah.model.TableMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableMappingRepository extends JpaRepository<TableMapping, Long> {

    void deleteAllByEtl_Id(Long etl_id);

    List<TableMapping> findAllByEtl_Id(Long etl_id);
}
