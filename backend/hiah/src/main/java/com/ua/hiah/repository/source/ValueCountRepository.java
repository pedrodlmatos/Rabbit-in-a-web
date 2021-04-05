package com.ua.hiah.repository.source;

import com.ua.hiah.model.source.ValueCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValueCountRepository extends JpaRepository<ValueCount, Long> {
}
