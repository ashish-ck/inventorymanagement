package com.drivojoy.inventory.helpers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

public class TokenAuthenticationService {

	private static final String AUTH_HEADER_NAME = "Authorization";
	private final TokenHandler tokenHandler;


	public TokenAuthenticationService(String secret, UserService userService) {
		tokenHandler = new TokenHandler(secret);
	}


	public void addAuthentication(HttpServletResponse response, UserAuthentication authentication) {
		final User user = authentication.getDetails();
		response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(AUTH_HEADER_NAME);
		if (token == null && request.getCookies() != null && request.getCookies().length > 0) {
			for(Cookie cookie : request.getCookies()){
				if(cookie.getName().equals("IDrivoJoyAuthCookie")){
					System.out.println("Cookie Found");
					token = cookie.getValue();
					break;
				}
			}
		}
		
		if(token != null){
			token = token.replaceAll("Bearer ", "");
			final UserAuthentication user = tokenHandler.parseUserFromToken(token);
			if (user != null) {
				return user;
			}
		}
		
		return null;
	}
}