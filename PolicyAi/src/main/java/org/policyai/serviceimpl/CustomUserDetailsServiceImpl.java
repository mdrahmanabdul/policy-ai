package org.policyai.serviceimpl;

import org.policyai.models.User;
import org.policyai.repos.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService{
	
	
	private UserRepo userRepo;
	
	public CustomUserDetailsServiceImpl(UserRepo userRepo) {
		this.userRepo=userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username).
						orElseThrow(()->new UsernameNotFoundException("User not found: "+username));
		return new CustomUserDetailsImpl(user);
	}

}
