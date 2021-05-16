package com.ua.hiah.service.role;

import com.ua.hiah.model.auth.Role;
import com.ua.hiah.model.auth.RoleEnum;
import com.ua.hiah.repository.auth.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService{

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository repository;

    @Override
    public void createRoles() {

        if (repository.count() == 0) {
            logger.info("ROLE SERVICE - Loading database with services");
            for (RoleEnum role : RoleEnum.values())
                repository.save(new Role(role));
        }
    }

}
