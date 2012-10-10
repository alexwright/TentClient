package com.xeentech.tent.client;

import java.util.List;
import java.util.Map;

import com.xeentech.tent.model.Account;
import com.xeentech.tent.model.AppInfo;
import com.xeentech.tent.model.AuthorizationResponse;
import com.xeentech.tent.model.Follower;
import com.xeentech.tent.model.Following;
import com.xeentech.tent.model.Post;
import com.xeentech.tent.model.Profile;
import com.xeentech.tent.model.UploadAttachment;

public interface TentClient {
	// Discovery and Authentication
	public List<Profile> discover(String entityUri);
	public AppInfo register(AppInfo appInfo, String serverUrl) throws TentClientException;
	public AuthorizationResponse authorize(String appId, String code, Account account) throws TentClientException;
	
	// Profile stuff
	public Profile getProfile(String profileUrl);
	public Profile getProfile(Account account) throws TentClientException;
	
	// Posts and Posting
	public Post post (Account account, Post post) throws TentClientException;
	public Post multipartPost (Account account, Post post, List<UploadAttachment> uploads) throws TentClientException;
	public List<Post> getPosts(Account account) throws TentClientException;
	public List<Post> getPosts(Account account, Map<String, String> filterParameters) throws TentClientException;
	
	// Followers and Following
	public List<Following> getFollowings (Account account) throws TentClientException;
	public Following follow (Account account, Following following) throws TentClientException;
	public void unfollow (Account account, String followingId) throws TentClientException;
	public List<Follower> getFollowers (Account account) throws TentClientException;
}
