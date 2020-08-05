package com.example.securityproject.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.securityproject.domain.Comment;



@Repository
public class CommentRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<Comment> rowMapper = (ResultSet rs, int rowNumber) -> {
		Comment comment = new Comment();
		comment.setId(rs.getInt("id"));
		comment.setBody(rs.getString("body"));
		comment.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());
		comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		comment.setArticleId(rs.getInt("article_id"));
		comment.setUserId(rs.getInt("user_id"));
		comment.setUsername(rs.getString("username"));
		return comment;
	};
	
	public Comment save(Comment comment) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(
			    "INSERT INTO `comment`(`body`, `created_at`, `modified_at`, `article_id`, `user_id`, `username`) VALUES(?, ?, ?, ?, ?, ?)",
			    Statement.RETURN_GENERATED_KEYS
			);
			ps.setString(1, comment.getBody());
			ps.setTimestamp(2, Timestamp.valueOf(comment.getCreatedAt()));
			ps.setTimestamp(3, Timestamp.valueOf(comment.getModifiedAt()));
			ps.setInt(4, comment.getArticleId());
			ps.setInt(5, comment.getUserId());
			ps.setString(6, comment.getUsername());
			return ps;
		}, keyHolder);
		comment.setId((int)keyHolder.getKey());
		return comment;
	}

	public Comment findById(int id) {
		return this.jdbcTemplate.queryForObject("SELECT * FROM `comment` WHERE `id` = ?", this.rowMapper, id);
	}

	public List<Comment> findByArticleId(int id) {
		return this.jdbcTemplate.query("SELECT * FROM `comment` WHERE `article_id` = ?", this.rowMapper, id);
	}

	public void delete(int id) {
		this.jdbcTemplate.update("DELETE FROM `comment` WHERE `id` = ?", id);
	}

}
