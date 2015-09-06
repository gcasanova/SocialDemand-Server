package app.web;

import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.Comment;
import app.domain.wrappers.CommentWrapper;
import app.service.comment.CommentService;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@RequestMapping(value = "/comments/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readComment(@PathVariable("id") Integer id) {
		Comment mComment = this.commentService.getComment(id);
		if (mComment == null)
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);

		JSONObject json = new JSONObject();
		json.put("comment", mComment);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}

	@RequestMapping(value = "/comments", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readComments(@RequestParam("ids") List<Integer> ids) {
		JSONArray jsonArray = new JSONArray();
		for (Comment aComment : this.commentService.getComments(ids)) {
			jsonArray.add(aComment);
		}
		jsonArray.sort(new CommentComparator());
		
		JSONObject json = new JSONObject();
		json.put("comments", jsonArray);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}

	@RequestMapping(value = "/comments", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> saveComment(@RequestBody CommentWrapper payload) {
		Comment aComment = this.commentService.save(payload.getComment());

		if (aComment != null) {
			JSONObject json = new JSONObject();
			json.put("comment", aComment);
			return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);
		}
	}
	
	// helper methods
	private class CommentComparator implements Comparator<Comment> {
		
		@Override
	    public int compare(Comment a, Comment b) {
	    	Long valA = a.getCreatedAt();
	        Long valB = b.getCreatedAt();
	        
	        if (valA < valB)
	            return 1;
	        if (valA > valB)
	            return -1;
	        return 0;    
	    }
	}
}
