package app.web;

import java.util.Comparator;

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

import app.domain.entities.Post;
import app.domain.wrappers.PostWrapper;
import app.enums.LocationType;
import app.service.post.PostService;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")
public class PostController {

	@Autowired
	private PostService postService;

	@RequestMapping(value = "/posts/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readPost(@PathVariable("id") Integer id) {
		Post mPost = this.postService.getPost(id);
		if (mPost == null)
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);
		
		JSONObject json = new JSONObject();
		json.put("post", mPost);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/posts", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> readPostByLocation(
			@RequestParam(value = "locationId", required = false) Integer locationId, 
			@RequestParam(value = "locationType", required = false) String locationType, 
			@RequestParam(value = "userId", required = false) Integer userId) {
		
		if (locationId != null && locationType != null) {
			LocationType type;
			try {
				type = LocationType.valueOf(locationType.toUpperCase());
			} catch (Exception e) {
				// location type provided is not a valid location type
				return new ResponseEntity<JSONObject>(HttpStatus.BAD_REQUEST);
			}
			
			JSONArray jsonArray = new JSONArray();
			for (Post aPost : this.postService.getPostsByLocation(locationId, type)) {
				jsonArray.add(aPost);
			}
			jsonArray.sort(new PostComparator());
			
			JSONObject json = new JSONObject();
			json.put("posts", jsonArray);
			return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else if (userId != null) {
			JSONArray jsonArray = new JSONArray();
			for (Post aPost : this.postService.getPostsByUserId(userId)) {
				jsonArray.add(aPost);
			}
			JSONObject json = new JSONObject();
			json.put("posts", jsonArray);
			return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		}
		return new ResponseEntity<JSONObject>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value = "/posts", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> savePost(@RequestBody PostWrapper payload) {
		Post aPost = this.postService.save(payload.getPost());

		if (aPost != null) {
			JSONObject json = new JSONObject();
			json.put("post", aPost);
			return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);
		}
	}
	
	// helper methods
	private class PostComparator implements Comparator<Post> {
		
		@Override
	    public int compare(Post a, Post b) {
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
