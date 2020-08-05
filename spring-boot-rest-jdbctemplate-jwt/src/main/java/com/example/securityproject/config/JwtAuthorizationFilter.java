package com.example.securityproject.config;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.example.securityproject.domain.User;
import com.example.securityproject.repository.UserRepository;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	private UserRepository userRepository;
	private JdbcTemplate jdbcTemplate;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JdbcTemplate jdbcTemplate) {
		super(authenticationManager);
		this.userRepository = userRepository;
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

        String header = request.getHeader(JwtProperties.HEADER_STRING);

        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = getUsernamePasswordAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
	}
	
	private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {
        String token = request.getHeader(JwtProperties.HEADER_STRING)
                .replace(JwtProperties.TOKEN_PREFIX,"");

        if (token != null) {
            String userName = JWT.require(HMAC512(JwtProperties.SECRET.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();

            if (userName != null) {
            	List<SimpleGrantedAuthority> authorities = jdbcTemplate.queryForList(
            		    "SELECT a.authority " +
            		    "FROM user_authorities a, users u " +
            		    "WHERE u.username = ? " +
            		    "AND u.id = a.user_id", String.class, userName)
            		    .stream()
            		    .map(SimpleGrantedAuthority::new)
            		    .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userName, null, authorities);
                return auth;
            }
            return null;
        }
        return null;
    }

}