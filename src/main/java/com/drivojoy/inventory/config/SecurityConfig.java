package com.drivojoy.inventory.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.drivojoy.inventory.helpers.StatelessAuthenticationFilter;
import com.drivojoy.inventory.helpers.TokenAuthenticationService;
import com.drivojoy.inventory.helpers.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final TokenAuthenticationService tokenAuthenticationService;

    public SecurityConfig() {
        super(true);
        this.userService = new UserService();
        tokenAuthenticationService = new TokenAuthenticationService("qMCdFDQuF23RV1Y-1Gq9L3cF3VmuFwVbam4fMTdAfpo", userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
    			.authenticationEntryPoint(new AuthenticationEntryPoint() {
    				
    				@Override
    				public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
    						throws IOException, ServletException {
    					response.sendRedirect("/login");
    				}
    			})
    			.accessDeniedHandler(new AccessDeniedHandler() {
    				
    				@Override
    				public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException arg2)
    						throws IOException, ServletException {
    					response.sendError(403, "Access Denied!");
    				}
    			}).and()
                .anonymous().and()
                .servletApi().and()
                .headers().cacheControl().and().and()
                .authorizeRequests()
                // Allow anonymous resource requests
                .antMatchers("/").fullyAuthenticated()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("**/*.html").permitAll()
                .antMatchers("**/*.css").permitAll()
                .antMatchers("**/*.js").permitAll()
                //.antMatchers("/api/**").fullyAuthenticated()

                // Allow anonymous logins
                .antMatchers("/app/login/**").permitAll()
                
                // Block app screens
                //.antMatchers("/").fullyAuthenticated()
                
                
                // All other request need to be authenticated
                //.anyRequest().authenticated().and()
                .and()
                // Custom Token based authentication based on the header previously given to the client
                .addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    public UserService userDetailsService() {
        return userService;
    }

    @Bean
    public TokenAuthenticationService tokenAuthenticationService() {
        return tokenAuthenticationService;
    }
	
	
/*	private final UserService userService ;
	private final TokenAuthenticationService tokenAuthenticationService ;
	public SecurityConfig() {
		super(true);
		this.userService = new UserService();
		tokenAuthenticationService = new TokenAuthenticationService("qMCdFDQuF23RV1Y-1Gq9L3cF3VmuFwVbam4fMTdAfpo", userService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
		.csrf()
		.disable()
		.exceptionHandling()
			.authenticationEntryPoint(new AuthenticationEntryPoint() {
				
				@Override
				public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
						throws IOException, ServletException {
					response.sendError(401, "Unauthorized!");
				}
			})
			.accessDeniedHandler(new AccessDeniedHandler() {
				
				@Override
				public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException arg2)
						throws IOException, ServletException {
					response.sendError(403, "Access Denied!");
				}
			})
		.and()
		.anonymous().and()
		.servletApi().and()
        .headers().cacheControl().disable()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
		.authorizeRequests()
		.antMatchers("/").permitAll()
		
		// Allow anonymous resource requests
		.antMatchers("/resources/**").permitAll()
		.antMatchers("/app/login/**").permitAll()
		.antMatchers("/api/**").fullyAuthenticated()

		// Allow anonymous logins
		.antMatchers("/login/**").permitAll()
		// Allow anonymous logins
		

		// All other request need to be authenticated
		//.anyRequest().authenticated()
		.and()
		// Custom Token based authentication based on the header previously given to the client
		.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService),
				AnonymousAuthenticationFilter.class);
	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	@Override
	public UserService userDetailsService() {
		return userService;
	}

	@Bean
	public TokenAuthenticationService tokenAuthenticationService() {
		return tokenAuthenticationService;
	}
*/
}