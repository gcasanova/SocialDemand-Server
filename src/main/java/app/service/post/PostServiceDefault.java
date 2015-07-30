package app.service.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.domain.entities.Post;
import app.domain.repositories.PostRepository;
import app.enums.LocationType;

@Service
public class PostServiceDefault implements PostService {
	
	@Autowired
	private PostRepository postRepository;

	@Override
	public Post getPost(Integer id) {
		return this.postRepository.findOne(id);
	}

	@Override
	public List<Post> getPostsByLocation(Integer locationId, LocationType locationType) {
		return this.postRepository.findByLocationIdAndLocationType(locationId, locationType);
	}

	@Override
	public List<Post> getPostsByUserId(Integer userId) {
		return this.postRepository.findByUser(userId);
	}

	@Override
	public Post save(Post post) {
		return this.postRepository.save(post);
	}

	@Override
	public void deletePost(Integer id) {
		// do not allow deletions for now
		// this.postRepository.delete(id);
	}
}
