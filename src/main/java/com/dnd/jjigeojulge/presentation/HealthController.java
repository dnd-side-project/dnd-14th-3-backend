package com.dnd.jjigeojulge.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HealthController {

	@GetMapping("/health")
	public String healthCheck() {
		log.debug("Health check endpoint called");
		return "I'm Alive!";
	}
}
