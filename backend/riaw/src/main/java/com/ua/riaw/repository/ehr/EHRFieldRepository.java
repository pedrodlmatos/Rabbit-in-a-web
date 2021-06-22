package com.ua.riaw.repository.ehr;

import com.ua.riaw.model.ehr.EHRField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EHRFieldRepository extends JpaRepository<EHRField, Long> {
}
