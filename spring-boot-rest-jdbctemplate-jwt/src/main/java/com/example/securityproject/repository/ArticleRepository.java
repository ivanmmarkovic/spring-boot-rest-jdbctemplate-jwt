package com.example.securityproject.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.securityproject.domain.Article;


@Repository
public class ArticleRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private CommentRepository commentRepository;
	
	private RowMapper<Article> rowMapper = (ResultSet rs, int rowNumber) -> {
		Article article = new Article();
		article.setId(rs.getInt("id"));
		article.setTitle(rs.getString("title"));
		article.setBody(rs.getString("body"));
		article.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());
		article.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		article.setUserId(rs.getInt("user_id"));
		article.setUsername(rs.getString("username"));
		return article;
	};
	
	public Article save(Article article) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(
			    "INSERT INTO `article`(`title`, `body`, `created_at`, `modified_at`, `user_id`, `username`) VALUES(?, ?, ?, ?, ?, ?)",
			    Statement.RETURN_GENERATED_KEYS
			);
			ps.setString(1, article.getTitle());
			ps.setString(2, article.getBody());
			ps.setTimestamp(3, Timestamp.valueOf(article.getCreatedAt()));
			ps.setTimestamp(4, Timestamp.valueOf(article.getModifiedAt()));
			ps.setInt(5, article.getUserId());
			ps.setString(6, article.getUsername());
			return ps;
		}, keyHolder);
		article.setId((int)keyHolder.getKey());
		return article;
	}

	public Article findById(int id) {
		Article article =  this.jdbcTemplate.queryForObject("SELECT * FROM `article` WHERE `id` = ?", this.rowMapper, id);
		article.setComments(this.commentRepository.findByArticleId(id));
		return article;
	}

	public List<Article> findAll() {
		return this.jdbcTemplate.query("SELECT * FROM `article`", this.rowMapper);
	}

	public Article update(int id, Article article) {
		Article dbArticle = this.findById(id);
		if(article.getTitle() == null && article.getBody() == null)
			return dbArticle;
		if(article.getTitle() != null)
			dbArticle.setTitle(article.getTitle());
		if(article.getBody() != null)
			dbArticle.setBody(article.getBody());
		dbArticle.setModifiedAt(LocalDateTime.now());
		this.jdbcTemplate.update(
				"UPDATE `article` SET `title` = ?, `body` = ?, `modified_at` = ? WHERE `id` = ?",
				dbArticle.getTitle(), dbArticle.getBody(), Timestamp.valueOf(dbArticle.getModifiedAt()), dbArticle.getId()
		);
		return dbArticle;
	}

	public void delete(int id) {
		this.jdbcTemplate.update("DELETE FROM `article` WHERE `id` = ?", id);
	}

}
