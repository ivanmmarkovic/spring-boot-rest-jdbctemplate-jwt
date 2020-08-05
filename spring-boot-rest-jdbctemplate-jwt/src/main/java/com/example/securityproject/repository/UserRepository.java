package com.example.securityproject.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.securityproject.domain.Article;
import com.example.securityproject.domain.User;

@Repository
public class UserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private RowMapper<User> rowMapper = (ResultSet rs, int rowNumber) -> {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setPassword("password");
		return user;
	};
	
	private RowMapper<String> rowMapperAuthority = (ResultSet rs, int rowNumber) -> {
		String authority = rs.getString("username");
		return authority;
	};
	
	public User save(User user) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(
			    "INSERT INTO `users`(`username`, `password`, `enabled`) VALUES(?, ?, ?)",
			    Statement.RETURN_GENERATED_KEYS
			);
			ps.setString(1, user.getUsername());
			ps.setString(2, passwordEncoder.encode(user.getPassword()));
			ps.setBoolean(3, true);
			return ps;
		}, keyHolder);
		user.setId((int)keyHolder.getKey());
		this.jdbcTemplate.update(
				"INSERT INTO `user_authorities`(`user_id`, `authority`) VALUES(?, ?)",
				user.getId(),
				"ROLE_USER"
		);
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return user;
		
	}
	
	public User saveAdmin(User user) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(
			    "INSERT INTO `users`(`username`, `password`, `enabled`) VALUES(?, ?, ?)",
			    Statement.RETURN_GENERATED_KEYS
			);
			ps.setString(1, user.getUsername());
			ps.setString(2, passwordEncoder.encode(user.getPassword()));
			ps.setBoolean(3, true);
			return ps;
		}, keyHolder);
		user.setId((int)keyHolder.getKey());
		this.jdbcTemplate.update(
				"INSERT INTO `user_authorities`(`user_id`, `authority`) VALUES(?, ?)",
				user.getId(),
				"ROLE_ADMIN"
		);
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return user;
		
	}
	
	public User findByUsername(String username) {
		return this.jdbcTemplate.queryForObject(
				"SELECT `id`, `username`, `password` FROM `users` WHERE `username` = ?", this.rowMapper, username);
	}
	
	public User findById(int id) {
		return this.jdbcTemplate.queryForObject(
				"SELECT `id`, `username`, `password` FROM `users` WHERE `id` = ?", this.rowMapper, id);
	}
	
	public User update(int id, User user) {
		User dbUser = this.findById(id);
		if(user.getUsername() == null && user.getPassword() == null)
			return dbUser;
		if(user.getUsername() != null)
			dbUser.setUsername(user.getUsername());
		if(user.getPassword() != null)
			dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
		this.jdbcTemplate.update("UPDATE `users` SET `username` = ?, `password` = ? WHERE `id` = ?", dbUser.getUsername(), dbUser.getPassword(), dbUser.getId());
		return dbUser;
	}
	
	public void delete(int id) {
		this.jdbcTemplate.update("DELETE FROM `users` WHERE `id` = ?", id);
	}
	
	public String getAuthority(int id) {
		return this.jdbcTemplate.queryForObject("SELECT `authority` FROM `user_authority` WHERE `user_id` = ?", this.rowMapperAuthority, id);
	}
}
