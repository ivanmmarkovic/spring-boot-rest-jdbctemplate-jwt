package com.example.securityproject.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.securityproject.domain.Comment;
import com.example.securityproject.domain.User;
import com.example.securityproject.repository.CommentRepository;
import com.example.securityproject.repository.UserRepository;



@RestController
public class CommentController {
	
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/articles/{articleId}/comments")
	public ResponseEntity<Comment> save(@PathVariable int articleId, @RequestBody Comment comment, Principal principal) {
		String username = principal.getName();
		User user = this.userRepository.findByUsername(username);
		comment.setUserId(user.getId());
		comment.setUsername(user.getUsername());
		comment.setArticleId(articleId);
		return new ResponseEntity<Comment>(this.commentRepository.save(comment), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/articles/{articleId}/comments/{id}")
	public ResponseEntity<Comment> delete(@PathVariable int id, Principal principal) {
		String username = principal.getName();
		User authUser = this.userRepository.findByUsername(username);
		Comment comment = this.commentRepository.findById(id);
		if(authUser.getId() != comment.getUserId()) { // not a author of comment
			String authority = this.userRepository.getAuthority(authUser.getId());
			if(!authority.equals("ROLE_ADMIN"))
				return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
		this.commentRepository.delete(id);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
