package com.ua.riah;

import com.ua.riah.service.databaseService.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class RiahApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiahApplication.class, args);
	}
}

@Component
class DatabaseLoader implements CommandLineRunner {

	private final DatabaseService service;

	@Autowired
	public DatabaseLoader (DatabaseService service) {
		this.service = service;
	}

	@Override
	public void run(String... args) throws Exception {
		service.loadCDMDatabases();
		service.loadScanReport();
	}
}
