package com.ua.riah.repository.target;

import com.ua.riah.model.CDMVersion;
import com.ua.riah.model.target.TargetDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetDatabaseRepository extends JpaRepository<TargetDatabase, Long> {

    TargetDatabase findTargetDatabaseByVersion(CDMVersion version);
}
