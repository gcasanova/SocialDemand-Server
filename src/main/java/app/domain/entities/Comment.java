package app.domain.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "comments")
@JsonInclude(Include.NON_EMPTY)
public class Comment implements Serializable {
	private static final long serialVersionUID = -8233474905179542882L;
	
	private Integer id;
	private Integer user;
	private String text;
	private Long createdAt;
	private boolean rootComment; // is the parent of this comment a 'Post' entity?

	
	private Integer parentId;
	private Set<Integer> comments;
	private Integer commentsCount = 0;

	@Id
	@JsonProperty
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@JsonProperty
	@Column(name = "user_id")
	public Integer getUser() {
		return user;
	}
	public void setUser(Integer user) {
		this.user = user;
	}
	
	@JsonProperty
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@JsonProperty
	@Column(name = "created_at")
	public Long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	
	@JsonProperty
	@Column(name = "root_comment")
	public boolean isRootComment() {
		return rootComment;
	}
	public void setRootComment(boolean rootComment) {
		this.rootComment = rootComment;
	}
	
	@JsonProperty
	@Column(name = "parent_id")
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	@JsonProperty
	@ElementCollection
	@Column(name = "inner_comment_id")
	@CollectionTable(name = "inner_comments", joinColumns = @JoinColumn(name = "comment_id"))
	public Set<Integer> getComments() {
		return comments;
	}
	public void setComments(Set<Integer> comments) {
		this.comments = comments;
	}
	
	@JsonProperty
	@Column(name = "comments_count")
	public Integer getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(Integer commentsCount) {
		this.commentsCount = commentsCount;
	}
	
	// helper methods
	public void addComment(Integer comment) {
		if (comments == null)
			comments = new HashSet<>();
		
		comments.add(comment);
	}
	public void removeComment(String comment) {
		if (comments != null) {
			if (comments.size() < 2) {
				comments = null;
			} else {
				comments.remove(comment);
			}
		}
	}
	
	
	public void increaseCommentCount() {
		commentsCount++;
	}
	public void decreaseCommentCount() {
		if (commentsCount > 0)
			commentsCount --;
	}
}
