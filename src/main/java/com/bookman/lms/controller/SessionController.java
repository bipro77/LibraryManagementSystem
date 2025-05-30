package com.bookman.lms.controller;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
public class SessionController {

	@GetMapping("/details") // http://localhost:8080/api/session/details
	public ResponseEntity<Map<String, Object>> getSessionDetails(HttpSession session) {
		Map<String, Object> sessionData = new HashMap<>();
		session.getAttributeNames().asIterator()
				.forEachRemaining(name -> sessionData.put(name, session.getAttribute(name)));
		sessionData.put("sessionId", session.getId());
		return ResponseEntity.ok(sessionData);
	}

	@PostMapping("/set") // http://localhost:8080/api/session/set?username=alice
	public String setSession(HttpSession session, @RequestParam String username) {
		session.setAttribute("username", username);
		return "Session variable 'username' set to " + username;
	}

	@GetMapping("/get") // http://localhost:8080/api/session/get
	public String getSession(HttpSession session) {
		String username = (String) session.getAttribute("username");
		return "Session username: " + (username != null ? username : "not set");
	}

	@PostMapping("/remove")
	public String removeSession(HttpSession session) {
		session.removeAttribute("username");
		return "Session variable 'username' removed";
	}

	@PostMapping("/invalidate")
	public String invalidate(HttpSession session) {
		session.invalidate();
		return "Session invalidated";
	}
}
