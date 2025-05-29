package com.bookman.lms.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
public class SessionController {
	
	// http://localhost:8080/api/session/set?username=alice
    @PostMapping("/set")
    public String setSession(HttpSession session, @RequestParam String username) {
        session.setAttribute("username", username);
        return "Session variable 'username' set to " + username;
    }
    
    // http://localhost:8080/api/session/get
    @GetMapping("/get")
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
