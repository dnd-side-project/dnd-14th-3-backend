package com.dnd.jjigeojulge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JjigeojulgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjigeojulgeApplication.class, args);
	}

}
