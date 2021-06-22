package com.ua.riaw.repository.ehr;

import com.ua.riaw.model.ehr.ValueCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValueCountRepository extends JpaRepository<ValueCount, Long> {
}
