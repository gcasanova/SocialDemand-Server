package app.domain.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.domain.entities.Post;
import app.enums.LocationType;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
	List<Post> findByLocationIdAndLocationType(Integer locationId, LocationType locationType);
	List<Post> findByUser(Integer user);
}
