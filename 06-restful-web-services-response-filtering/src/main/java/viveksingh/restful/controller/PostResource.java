package viveksingh.restful.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import viveksingh.restful.beans.Post;
import viveksingh.restful.dao.PostDaoService;
import viveksingh.restful.exception.PostNotFoundException;

@RestController
public class PostResource {

	@Autowired
	PostDaoService postDaoService;

	// - Retrieve all posts of a user -- GET /users/{id}/posts
	/**
	 * Retrieve all posts for provided user id.
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping(path = { "/users/{userId}/posts" })
	public MappingJacksonValue retrivePosts(@PathVariable(name = "userId") int userId) {

		final List<Post> posts = postDaoService.retrieveAllPosts(userId);

		// Start : Dynamic filtering

		final SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
				.filterOutAllExcept(new String[] { "id", "message" });

		final FilterProvider filters = new SimpleFilterProvider().addFilter("PostBeanFilter", filter);

		final MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(posts);
		mappingJacksonValue.setFilters(filters);

		// End : Dynamic filtering

		if (posts == null || posts.isEmpty()) {
			throw new PostNotFoundException("User ID - " + userId);
		}

		return mappingJacksonValue;
	}

	// - Retrieve a post for a user -- GET /users/{id}/posts/{post_id}
	/**
	 * Retrieve post with for provided user and post ID.
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	@GetMapping(path = { "/users/{userId}/posts/{id}" })
	public List<Post> retrivePost(@PathVariable(name = "userId") int userId,
			@PathVariable(name = "id") final long postId) {

		final List<Post> posts = postDaoService.retrievePost(userId, postId);

		if (posts == null || posts.isEmpty()) {
			throw new PostNotFoundException("User ID - " + userId + ", Post ID - " + postId);
		}

		return posts;
	}

	// - Create a post for a user -- POST /users/{id}/posts
	/**
	 * Save provided post for the user.
	 * 
	 * @param userId
	 * @param post
	 * @return
	 */
	@PostMapping(path = { "/users/{id}/posts" })
	public Post createPost(@PathVariable(name = "id") int userId, @RequestBody(required = true) final Post post) {

		final Post newPost = postDaoService.createPost(userId, post);

		if (newPost == null) {
			throw new PostNotFoundException("User ID - " + userId + ", Message - " + post.getMessage());
		}

		return newPost;
	}

}
