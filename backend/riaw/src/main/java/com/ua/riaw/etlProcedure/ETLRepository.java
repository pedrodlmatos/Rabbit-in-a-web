package com.ua.riaw.etlProcedure;

import com.ua.riaw.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ETLRepository extends JpaRepository<ETL, Long> {

    List<ETL> findAllByUsersContainingAndDeleted(User user, boolean isDeleted);
}
