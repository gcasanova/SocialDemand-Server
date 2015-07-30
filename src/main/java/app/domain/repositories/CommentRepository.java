package app.domain.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.domain.entities.Comment;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {
}
