package com.example.securityproject.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.securityproject.repository.UserRepository;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private DataSource dataSource;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
	        .jdbcAuthentication()
	        .dataSource(dataSource)
	        .passwordEncoder(passwordEncoder())
	        .usersByUsernameQuery(
	            "SELECT username, password, enabled from users where username = ?")
	        .authoritiesByUsernameQuery(
	            "SELECT u.username, a.authority " +
	            "FROM user_authorities a, users u " +
	            "WHERE u.username = ? " +
	            "AND u.id = a.user_id"
	        );
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.addFilter(new JwtAuthenticationFilter(authenticationManager()))
		.addFilter(new JwtAuthorizationFilter(authenticationManager(), this.userRepository, jdbcTemplate))
		.authorizeRequests()
			.antMatchers("/login").permitAll()
			
			// comments
			.antMatchers("POST", "/articles/{[\\d+]}/comments").hasRole("USER")
			.antMatchers("DELETE", "/articles/{[\\d+]}/comments/{[\\d+]}").hasAnyRole("ADMIN", "USER")
			
			// articles
			.antMatchers("POST", "/articles").hasRole("USER")
			.antMatchers("PATCH", "/articles/{[\\d+]}").hasRole("USER")
			.antMatchers("DELETE", "/articles/{[\\d+]}").hasAnyRole("ADMIN", "USER")
			.antMatchers("GET", "/articles/{[\\d+]}").permitAll()
			.antMatchers("GET", "/articles").permitAll()
				
			// users
			.antMatchers("POST", "/users").permitAll()
			.antMatchers("PATCH", "/users/{[\\d+]}").hasRole("USER")
			.antMatchers("DELETE", "/users/{[\\d+]}").hasAnyRole("ADMIN", "USER")
			
			.antMatchers("POST", "/admins").hasRole("ADMIN")
			.antMatchers("PATCH", "/admins/{[\\d+]}").hasRole("ADMIN")
			.antMatchers("DELETE", "/admins/{[\\d+]}").hasRole("ADMIN")
			
			.anyRequest().permitAll();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
