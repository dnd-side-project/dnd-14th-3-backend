package com.dnd.jjigeojulge.global;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@GetMapping("/health")
	public String healthCheck() {
		return "I'm Alive!";
	}
}
