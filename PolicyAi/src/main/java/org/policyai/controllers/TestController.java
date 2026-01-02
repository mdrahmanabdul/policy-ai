package org.policyai.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    
    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello from public endpoint!";
    }
    
    @GetMapping("/user/hello")
    public String userHello() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Hello " + auth.getName() + "! You are authenticated.";
    }
    
    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHello() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Hello Admin " + auth.getName() + "!";
    }
    
    @GetMapping("/user/me")
    public String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Current user: " + auth.getName() + 
               ", Roles: " + auth.getAuthorities();
    }
}