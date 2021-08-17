package com.drivojoy.inventory.helpers;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public final class TokenHandler {

    private final String secret;
    @Autowired

    public TokenHandler(String secret) {
        this.secret = secret;
    }

    @SuppressWarnings("unchecked")
	public UserAuthentication parseUserFromToken(String tokenBearer) {
    	String token = tokenBearer;
    	System.out.println("Token : "+token);
    	try{
    		Claims claims = Jwts.parser()
            		.setSigningKey(Base64.getUrlDecoder().decode(secret))
                    .parseClaimsJws(token)
                    .getBody();
    		Collection<GrantedAuthority> authorities = new ArrayList<>();
    		String userName = claims.get("unique_name").toString();
    		String phoneNumber = claims.get("PhoneNumber").toString();
    		String profileName = ((ArrayList<String>)claims.get("ProfileName")).get(0);
    		
    		List<String> roles = new ArrayList<>();
    		try{
    			roles = ((ArrayList<String>)claims.get("role"));
    		}catch(ClassCastException ex){
    			String singleRole = (String)claims.get("role"); 
    			roles.add(singleRole);
    		}
    		String warehouse = null;
    		if(claims.get("WorkshopRef") != null){
    			warehouse = claims.get("WorkshopRef").toString();
    		}else{
    			warehouse = "571f7e187f8dbe1c02894f45";
    		}    		
    		for(String role : roles){
    			authorities.add(new GrantedAuthority() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public String getAuthority() {
						String custom = "ROLE_" + role.toUpperCase();
						return custom;
					}
				});
    		}
    		if(userName != null){
    			User user = new User(userName, phoneNumber, authorities);
    			System.out.println("User : "+userName.toString());
    			return new UserAuthentication(user, warehouse, profileName, userName);
    		}
    		else
    			return null;
    	}catch(SignatureException ex){
    		System.out.println("Singature Exception : "+ex.getMessage());
    		return null;
    	}catch(Exception ex){
    		System.out.println("Exception : "+ex.getMessage());
    		return null;
    	}
    	
    }

    public String createTokenForUser(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}