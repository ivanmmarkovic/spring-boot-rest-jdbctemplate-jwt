package com.example.securityproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.securityproject.domain.User;
import com.example.securityproject.repository.UserRepository;

@RestController
public class AdminController {

	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/admins")
	public ResponseEntity<User> save(@RequestBody User user) {
		User createdAdmin =  this.userRepository.saveAdmin(user);
		return new ResponseEntity<User>(createdAdmin, HttpStatus.CREATED);
	}
	
	@PatchMapping("/admins/{id}")
	public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user){
		User updatedUser = this.userRepository.update(id, user);
		return new ResponseEntity<User>(updatedUser, HttpStatus.OK);
	}
	
	@DeleteMapping("/admins/{id}")
	public void delete(int id) {
		this.userRepository.delete(id);
	}
}
