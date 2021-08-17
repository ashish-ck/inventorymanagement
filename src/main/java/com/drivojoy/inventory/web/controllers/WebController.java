package com.drivojoy.inventory.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.drivojoy.inventory.helpers.LoginBean;
import com.drivojoy.inventory.helpers.TokenData;
import com.drivojoy.inventory.helpers.UserAuthentication;
import com.drivojoy.inventory.models.Roles;
import com.drivojoy.inventory.repositories.RolesRepository;

/**
 * SPA entry controller for spring MVC
 * 
 * @author ashishsingh
 *
 */
@Controller
public class WebController {

	@Value("${spring.profiles.active}")
	private String environment;
	@Autowired
	private RolesRepository rolesRepository;

	/**
	 * Returns a view for root url if user is authenticated
	 * @param model Spring MVC View Model
	 * @param user Currently logged in user
	 * @return View
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(Model model, @AuthenticationPrincipal UserAuthentication user) {
		List<String> permissions = new ArrayList<>();
		for (GrantedAuthority authority : user.getAuthorities()) {
			Roles role = rolesRepository.findByRole(authority.getAuthority());
			if (role != null) {
				permissions.addAll(role.getPermissions());
			}
		}
		model.addAttribute("permissions", permissions);
		model.addAttribute("userName", user.getUserName());
		return "layout";
	}

	/**
	 * Returns view for the login SPA
	 * @param model Spring MVC View Model
	 * @return View
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		LoginBean loginBean = new LoginBean();
		model.addAttribute("loginBean", loginBean);
		return "login";

	}

	/**
	 * Login post. This calls the drivojoy.com authentication service and gets
	 * token based on user credentials
	 * 
	 * @param loginBean Username and Password of user
	 * @param result BindingResult object that validates the LoginBean
	 * @param model Spring MVC View Model
	 * @param response HttpServletResponse object
	 * @return Token
	 * @throws IOException Throw IO exception
	 */
	@RequestMapping(value = "/login/do", method = RequestMethod.POST)
	public String doLogin(@ModelAttribute("loginBean") LoginBean loginBean, BindingResult result, Model model,
			HttpServletResponse response) throws IOException {
		//final String uri = "http://drivojoy.com:9001/oauth/token";
		String uri = "http://drivojoy.com:9001/oauth/token";
		if (environment.equals("dev")) {
			//uri = "http://ec2-54-169-71-234.ap-southeast-1.compute.amazonaws.com:9001/oauth/token";
			uri = "http://drivojoy.com:9001/oauth/token";
		} else if (environment.equals("production")) {
			uri = "http://drivojoy.com:9001/oauth/token";
		} else if (environment.equals("test")) {
			uri = "http://drivojoy.com:9001/oauth/token";
		} else if (environment.equals("debug")) {
			uri = "http://drivojoy.com:9001/oauth/token";
		}
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("username", loginBean.getUsername());
		map.add("password", loginBean.getPassword());
		map.add("grant_type", "password");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		//curl -X POST 
		//-H "Content-Type: application/x-www-form-urlencoded" 
		//-H "Accept: application/json" 
		//-H "user-origin: ConsumerApp" 
		//-H "Authorization: bearer" 
		//-H "Cache-Control: no-cache" 
		//-H "Postman-Token: 910ce937-3036-5567-cfa9-b707e0eb724d" 
		//-d 'username=invoice@drivojoy.com&password=invoice123&grant_type=password' "http://drivojoy.com:9001/oauth/token"
		headers.add("Accept", "application/json");
		//headers.add("user-origin", "ConsumerApp");
		headers.add("Authorization", "bearer");
		//headers.add("Cache-Control", "no-cache");
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter());
		messageConverters.add(new FormHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
		TokenData data = (TokenData) restTemplate.postForObject(uri, request, TokenData.class);
		if (data != null) {
			Cookie cookie = new Cookie("IDrivoJoyAuthCookie", "Bearer " + data.getAccess_token());
			Cookie userNameCookie = new Cookie("UserName", data.getProfileName());
			if (environment.equals("dev")) {
				cookie.setDomain("117.255.216.102");
			} else if (environment.equals("production")) {
				cookie.setDomain(".drivojoy.com");
			} else if (environment.equals("test")) {
				cookie.setDomain("localhost");
			} else if (environment.equals("debug")) {
				cookie.setDomain("localhost");
			}
			// cookie.setSecure(true);
			cookie.setPath("/");
			cookie.setMaxAge(24 * 60 * 60);
			userNameCookie.setPath("/");
			userNameCookie.setMaxAge(24 * 60 * 60);
			response.addCookie(cookie);
			response.addCookie(userNameCookie);
			response.setHeader("Authorization", "Bearer " + data.getAccess_token());
			response.sendRedirect("/");
			return "";
		} else {
			// login failed
			return "login";
		}
	}	
}
