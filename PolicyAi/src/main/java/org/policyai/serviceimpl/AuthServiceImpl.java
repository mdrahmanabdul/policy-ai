package org.policyai.serviceimpl;

import org.policyai.dtos.AuthRequest;
import org.policyai.dtos.AuthResponse;
import org.policyai.dtos.RegisterRequest;
import org.policyai.models.User;
import org.policyai.repos.UserRepo;
import org.policyai.security.JwtUtil;
import org.policyai.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{
    
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    public AuthServiceImpl(UserRepo userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
    	this.userRepository=userRepository;
    	this.passwordEncoder=passwordEncoder;
    	this.jwtUtil=jwtUtil;
    	this.authenticationManager=authenticationManager;
    }
    
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "ROLE_USER");
        user.setEnabled(true);
        
        userRepository.save(user);
        
        return "User registered successfully";
    }
    
    public AuthResponse login(AuthRequest request) {
       
    	Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        //From here onwards Spring takes care of the rest
        
        CustomUserDetailsImpl userDetails = (CustomUserDetailsImpl) authentication.getPrincipal();      
        String token = jwtUtil.generateToken(userDetails);
        
        return new AuthResponse(
            token,
            userDetails.getUsername(),
            userDetails.getUser().getRole()
        );
    }
}