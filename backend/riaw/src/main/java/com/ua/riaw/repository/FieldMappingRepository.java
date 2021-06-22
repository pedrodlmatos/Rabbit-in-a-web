package com.ua.riaw.repository;

import com.ua.riaw.model.FieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldMappingRepository extends JpaRepository<FieldMapping, Long> {

    List<FieldMapping> findAllByTableMapping_Id(Long tableMappingId);
}
