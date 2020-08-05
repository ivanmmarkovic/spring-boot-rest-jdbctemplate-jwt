package com.example.securityproject.controller;

import java.security.Principal;
import java.util.List;

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
import com.example.securityproject.domain.Comment;
import com.example.securityproject.domain.User;
import com.example.securityproject.repository.ArticleRepository;
import com.example.securityproject.repository.CommentRepository;
import com.example.securityproject.repository.UserRepository;



@RestController
public class ArticleController {

	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/articles")
	public ResponseEntity<Article> save(@RequestBody Article article, Principal principal) {
		/*if(principal == null)
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);*/
		String username = principal.getName();
		User user = this.userRepository.findByUsername(username);
		article.setUserId(user.getId());
		article.setUsername(user.getUsername());
		return new ResponseEntity<Article>(
				this.articleRepository.save(article), HttpStatus.CREATED);
	}
	
	@GetMapping("/articles/{id}")
	public ResponseEntity<Article> findById(@PathVariable int id) {
		return new ResponseEntity<Article>(this.articleRepository.findById(id), HttpStatus.OK);
	}
	
	@GetMapping("/articles")
	public ResponseEntity<List<Article>> findAll() {
		return new ResponseEntity<List<Article>>(this.articleRepository.findAll(), HttpStatus.OK);
	}
	
	@PatchMapping("/articles/{id}")
	public ResponseEntity<Article> findById(@PathVariable int id, @RequestBody Article article) {
		return new ResponseEntity<Article>(this.articleRepository.update(id, article), HttpStatus.OK);
	}
	
	@DeleteMapping("/articles/{id}")
	public ResponseEntity<Article> delete(@PathVariable int id, Principal principal) {
		String username = principal.getName();
		User authUser = this.userRepository.findByUsername(username);
		Article article = this.articleRepository.findById(id);
		if(authUser.getId() != article.getUserId()) { // not a author of article
			String authority = this.userRepository.getAuthority(authUser.getId());
			if(!authority.equals("ROLE_ADMIN"))
				return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
		this.articleRepository.delete(id);
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
}
