package com.bookman.lms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@GetMapping("/hello")
	public String sayHello() {
		return "Hello, devtool added";
	}
	
	@GetMapping("/welcome")
    public String home() {
        return "Welcome to the secured home page!";
    }

    @GetMapping("/public")
    public String publicPage() {
        return "This is a public page, accessible without authentication.";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "Welcome, Admin! This page requires ADMIN role.";
    }

    @GetMapping("/user")
    public String userPage() {
        return "Welcome, User! This page requires USER role.";
    }
}
