package com.drore.cloud.tdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan
public class DataInterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataInterfaceApplication.class, args);
	}
}
