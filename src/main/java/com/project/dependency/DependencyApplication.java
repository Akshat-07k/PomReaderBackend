package com.project.dependency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DependencyApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(DependencyApplication.class, args);
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) { 
		return application.sources(DependencyApplication.class); 
	}
}
