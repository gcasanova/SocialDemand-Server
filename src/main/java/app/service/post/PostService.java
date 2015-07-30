package app.service.post;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.Post;
import app.enums.LocationType;

@Validated
public interface PostService {

	Post getPost(
			@NotNull(message = "{validate.postService.getPost.id}") Integer id);
	
	List<Post> getPostsByLocation(
			@NotNull(message = "{validate.postService.getPostsByLocation.locationId}") Integer locationId,
			@NotNull(message = "{validate.postService.getPostsByLocation.locationType}") LocationType locationType);
	
	List<Post> getPostsByUserId(
			@NotNull(message = "{validate.postService.getPostsByUserId.userId}") Integer userId);

	Post save(
			@NotNull(message = "{validate.postService.save.post}") @Valid Post post);

	void deletePost(Integer id);
}
