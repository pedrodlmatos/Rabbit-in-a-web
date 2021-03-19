package com.ua.hiah.repository.target;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.target.TargetDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetDatabaseRepository extends JpaRepository<TargetDatabase, Long> {

    TargetDatabase findTargetDatabaseByVersion(CDMVersion version);

}
