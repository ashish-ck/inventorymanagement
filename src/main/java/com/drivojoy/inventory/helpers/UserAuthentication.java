package com.drivojoy.inventory.helpers;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserAuthentication implements Authentication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7426462742157234595L;
	private final User user;
	private boolean authenticated = true;
	private String warehouse;
	private String userName;
	private String email;
	private boolean isAdmin = false;

	public UserAuthentication(User user, String warehouse, String userName, String email) {
		this.user = user;
		this.warehouse = warehouse;
		this.userName = userName;
		this.email = email;
		this.isAdmin = hasRole("ROLE_ADMIN");
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String getName() {
		return user.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getAuthorities();
	}

	@Override
	public Object getCredentials() {
		return user.getPassword();
	}

	@Override
	public User getDetails() {
		return user;
	}

	@Override
	public Object getPrincipal() {
		return user.getUsername();
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public boolean hasRole(String role) {
		for (GrantedAuthority authority : this.getAuthorities()) {
			System.out.println(authority.getAuthority().toUpperCase());
			if (authority.getAuthority().toUpperCase().equals(role.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
}