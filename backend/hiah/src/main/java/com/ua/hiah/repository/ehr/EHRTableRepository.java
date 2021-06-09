package com.ua.hiah.repository.ehr;

import com.ua.hiah.model.ehr.EHRTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EHRTableRepository extends JpaRepository<EHRTable, Long> {
}
