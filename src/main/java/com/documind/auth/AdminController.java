package com.documind.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@GetMapping("/ping")
	public PingResponse ping() {
		return new PingResponse("ok");
	}

	public record PingResponse(String status) {
	}
}
