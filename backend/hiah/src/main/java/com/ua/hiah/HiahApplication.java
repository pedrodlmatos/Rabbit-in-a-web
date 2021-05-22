package com.ua.hiah;

import com.ua.hiah.security.services.UserDetailsServiceImpl;
import com.ua.hiah.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@SpringBootApplication
public class HiahApplication {

	@PostConstruct
	void setUTCTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(HiahApplication.class, args);
	}
}

@Component
class DatabaseLoader implements CommandLineRunner {

	private final RoleService roleService;

	private final UserDetailsServiceImpl userService;

	@Autowired
	public DatabaseLoader (RoleService roleService, UserDetailsServiceImpl userService) {
		this.roleService = roleService;
		this.userService = userService;
	}

	@Override
	public void run(String... args) throws Exception {
		roleService.createRoles();
		userService.createDefaultUser();
		userService.createDefaultAdmin();
	}
}