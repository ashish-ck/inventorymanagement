package com.drivojoy.inventory.helpers;

import java.util.Arrays;
import java.util.Date;

public class TokenData {

	private String access_token;
	private String token_type;
	private long expires_in;
	private String client_id;
	private String userName;
	private Date issued;
	private Date expires;
	private String[] ProfileName;
	
	public TokenData(){}
	
	public TokenData(String access_token, String token_type, long expires_in, String client_id, String userName,
			Date issued, Date expires, String[] profileName) {
		super();
		this.access_token = access_token;
		this.token_type = token_type;
		this.expires_in = expires_in;
		this.client_id = client_id;
		this.userName = userName;
		this.issued = issued;
		this.expires = expires;
		this.ProfileName = profileName;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getIssued() {
		return issued;
	}

	public void setIssued(Date issued) {
		this.issued = issued;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public String getProfileName() {
		try{
			return ProfileName[0];
		}catch(Exception ex){
			return "";
		}
	}

	public void setProfileName(String[] profileName) {
		this.ProfileName = profileName;
	}

	@Override
	public String toString() {
		return "TokenData [access_token=" + access_token + ", token_type=" + token_type + ", expires_in=" + expires_in
				+ ", client_id=" + client_id + ", userName=" + userName + ", issued=" + issued + ", expires=" + expires
				+ ", profileName=" + Arrays.toString(ProfileName) + "]";
	}
	
	
}
