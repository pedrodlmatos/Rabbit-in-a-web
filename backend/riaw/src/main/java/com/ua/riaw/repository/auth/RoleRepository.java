package com.ua.riaw.repository.auth;


import com.ua.riaw.model.auth.Role;
import com.ua.riaw.model.auth.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum name);

}
