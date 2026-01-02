package org.policyai.serviceimpl;

import java.util.Collection;
import java.util.Collections;

import org.policyai.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class CustomUserDetailsImpl implements UserDetails {
	
	private final User user;
	
	public CustomUserDetailsImpl(User user) {
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Convert role string to GrantedAuthority
		return Collections.singletonList(
			new SimpleGrantedAuthority(user.getRole())
		);
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
	
	// Expose the underlying user entity if needed
	public User getUser() {
		return user;
	}
}