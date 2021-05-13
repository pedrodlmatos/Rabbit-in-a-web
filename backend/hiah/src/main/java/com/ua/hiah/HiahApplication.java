package com.ua.hiah;

import com.ua.hiah.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class HiahApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiahApplication.class, args);
	}
}

@Component
class DatabaseLoader implements CommandLineRunner {

	private final RoleService roleService;

	@Autowired
	public DatabaseLoader (RoleService roleService) {
		this.roleService = roleService;
	}

	@Override
	public void run(String... args) throws Exception {
		roleService.createRoles();
	}
}