package org.policyai.services;

import org.policyai.dtos.AuthRequest;
import org.policyai.dtos.AuthResponse;
import org.policyai.dtos.RegisterRequest;

public interface AuthService {

	public String register(RegisterRequest request);
	public AuthResponse login(AuthRequest request);
}
