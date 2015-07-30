package app.service.comment;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.Comment;

@Validated
public interface CommentService {

	Comment getComment(
			@NotNull(message = "{validate.commentService.getComment.id}") Integer id);
	
	List<Comment> getComments(
			@NotNull(message = "{validate.commentService.getComments.ids}") List<Integer> ids);

	Comment save(
			@NotNull(message = "{validate.commentService.save.comment}") @Valid Comment comment);

	void deleteComment(Integer id);
}
