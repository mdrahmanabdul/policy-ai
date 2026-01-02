package org.policyai.dtos;

public class AuthResponse {
	
    private String token;
    private String username;
    private String role;
	public AuthResponse(String token, String username, String role) {
		super();
		this.token = token;
		this.username = username;
		this.role = role;
	}
	public AuthResponse() {
		super();
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
    
    
}