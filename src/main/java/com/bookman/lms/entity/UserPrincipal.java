package com.bookman.lms.entity;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

	private final AppUser appUser;

	public UserPrincipal(AppUser appUser) {
		// TODO Auto-generated constructor stub
		this.appUser = appUser;

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return appUser.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return appUser.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return appUser.getUsername();
	}

}
