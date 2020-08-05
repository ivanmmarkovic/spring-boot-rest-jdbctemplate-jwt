package com.example.securityproject.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.securityproject.domain.Article;
import com.example.securityproject.domain.User;
import com.example.securityproject.repository.UserRepository;

@RestController
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/users")
	public ResponseEntity<User> save(@RequestBody User user) {
		User createdUser =  this.userRepository.save(user);
		return new ResponseEntity<User>(createdUser, HttpStatus.CREATED);
	}
	
	@PatchMapping("/users/{id}")
	public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user, Principal principal){
		String username = principal.getName();
		User authUser = this.userRepository.findByUsername(username);
		User dbUser = this.userRepository.findById(id);
		if(authUser.getId() != dbUser.getId()) { // not a user itself
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
		
		User updatedUser = this.userRepository.update(id, user);
		return new ResponseEntity<User>(updatedUser, HttpStatus.OK);
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<User> delete(@PathVariable int id, Principal principal) {
		String username = principal.getName();
		User authUser = this.userRepository.findByUsername(username);
		if(authUser.getId() != id) { // not a user itself
			String authority = this.userRepository.getAuthority(authUser.getId());
			if(!authority.equals("ROLE_ADMIN"))
				return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
		this.userRepository.delete(id);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
}
