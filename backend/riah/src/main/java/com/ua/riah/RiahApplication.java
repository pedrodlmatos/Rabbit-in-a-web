package com.ua.riah;

import com.ua.riah.service.source.sourceDatabaseService.SourceDatabaseService;
import com.ua.riah.service.target.targetDatabase.TargetDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;

@SpringBootApplication
public class RiahApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiahApplication.class, args);
	}
}


/*
@Component
class DatabaseLoader implements CommandLineRunner {


	//private final RoleService roleService;

	private final TargetDatabaseService targetDatabaseService;

	//private final SourceDatabaseService sourceDatabaseService;

	@Autowired
	public DatabaseLoader (TargetDatabaseService targetDatabaseService, SourceDatabaseService sourceDatabaseService) {
		//this.roleService = roleService;
		this.targetDatabaseService = targetDatabaseService;
		//this.sourceDatabaseService = sourceDatabaseService;
	}

	@Override
	public void run(String... args) throws Exception {
		//roleService.createRoles();
		targetDatabaseService.loadCDMDatabases();
		//sourceDatabaseService.loadScanReport();
	}
}
*/

