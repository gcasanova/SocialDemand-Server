package app.service.comment;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.domain.entities.Comment;
import app.domain.entities.Post;
import app.domain.repositories.CommentRepository;
import app.domain.repositories.PostRepository;

@Service
public class CommentServiceDefault implements CommentService {
	
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private PostRepository postRepository;

	@Override
	public Comment getComment(Integer id) {
		return this.commentRepository.findOne(id);
	}
	
	@Override
	public List<Comment> getComments(List<Integer> ids) {
		List<Comment> comments = new ArrayList<>();
		this.commentRepository.findAll(ids).forEach(comments::add);
		return comments;
	}

	@Override
	@Transactional
	public Comment save(Comment aComment) {
		if (aComment.isRootComment()) {
			Post mPost = this.postRepository.findOne(aComment.getParentId());
			if (mPost != null) {
				// save comment to retrieve id
				aComment = this.commentRepository.save(aComment);
				
				// add to post entity and save it
				mPost.increaseCommentCount();
				mPost.addComment(aComment.getId());
				this.postRepository.save(mPost);
				
				// return new comment
				return aComment;
			}
		} else {
			Comment mComment = this.commentRepository.findOne(aComment.getParentId());
			if (mComment != null) {
				// save comment to retrieve id
				aComment = this.commentRepository.save(aComment);
				
				// add to post entity and save it
				mComment.increaseCommentCount();
				mComment.addComment(aComment.getId());
				this.commentRepository.save(mComment);
				
				// find other parents and increase their counts
				while (!mComment.isRootComment()) {
					mComment = this.commentRepository.findOne(mComment.getParentId());
					mComment.increaseCommentCount();
					this.commentRepository.save(mComment);
				}
				
				// we have reached root comment, increase its post parent comments count
				Post mPost = this.postRepository.findOne(mComment.getParentId());
				mPost.increaseCommentCount();
				this.postRepository.save(mPost);
				
				// return new comment
				return aComment;
			}
		}
		
		// if it reaches here something went wrong
		return null;
	}

	@Override
	public void deleteComment(Integer id) {
		// do not allow deletions for now
		// this.commentRepository.delete(id);
	}
}
