package com.ua.riaw.repository;

import com.ua.riaw.model.ETL;
import com.ua.riaw.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ETLRepository extends JpaRepository<ETL, Long> {

    List<ETL> findAllByUsersContainingAndDeleted(User user, boolean isDeleted);
}
