package com.drivojoy.inventory;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @author ashishsingh
 *
 * Spring boot main class that starts the application in debug environment
 */

@SpringBootApplication
//@EnableDiscoveryClient
public class InventoryAppApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(InventoryAppApplication.class);

	public static void main(String[] args) throws IOException {
		//System.setProperty("spring.config.name", "inventory-service");
		if(System.getProperty("spring.profiles.active") == null)
			System.setProperty("spring.profiles.active", "debug");
		logger.error("Running inventory app in : "+System.getProperty("spring.profiles.active")+" mode!");
		SpringApplication.run(InventoryAppApplication.class, args);

	}
}
