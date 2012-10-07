package com.xeentech.tent.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.xeentech.tent.model.Account;
import com.xeentech.tent.model.AppInfo;
import com.xeentech.tent.model.AuthorizationRequest;
import com.xeentech.tent.model.AuthorizationResponse;
import com.xeentech.tent.model.Post;
import com.xeentech.tent.model.Profile;
import com.xeentech.tent.model.UploadAttachment;

import android.net.Uri;
import android.util.Log;

public class TentClient {
	static final String TENT_MIME = "application/vnd.tent.v0+json";

	public List<Profile> discover(String entityUri) {
		if (!entityUri.contains("://")) {
			entityUri = "http://" + entityUri;
		}
		List<String> profileUrls = (new DiscoveryClient())
				.getProfileUrl(entityUri);

		List<Profile> profiles = new ArrayList<Profile>();
		for (String profileUrl : profileUrls) {
			Profile profile = getProfile(profileUrl);
			if (profile != null) {
				profiles.add(profile);
			}
		}
		return profiles;
	}
	
	public AppInfo register(AppInfo appInfo, String serverUrl)
			throws TentClientException {
		String apiUrl = Uri.parse(serverUrl).buildUpon().appendPath("apps")
				.build().toString();
		
		HttpPost req = new HttpPost(apiUrl);
		req.setHeader("Content-Type", TENT_MIME);
		req.setHeader("Accept", TENT_MIME);
		try {
			req.setEntity(new StringEntity(new Gson().toJson(appInfo)));
		} catch (UnsupportedEncodingException e) {
			throw new TentClientException("error when encoding api request", e);
		}
		
		try {
			HttpResponse res = getHttpClient().execute(req);
			InputStreamReader is = new InputStreamReader(res.getEntity().getContent());
			return new Gson().fromJson(is, AppInfo.class);
		} catch (IOException e) {
			throw new TentClientException("api error when creating app", e);
		}
	}
	
	public AuthorizationResponse authorize(String appId, String code, String serverUrl) throws TentClientException {
		String apiUrl = Uri.parse(serverUrl).buildUpon()
				.appendPath("apps")
				.appendPath(appId)
				.appendPath("authorizations")
				.build().toString();
		HttpPost req = new HttpPost(apiUrl);
		req.setHeader("Content-Type", TENT_MIME);
		req.setHeader("Accept", TENT_MIME);
		
		AuthorizationRequest authReq = new AuthorizationRequest();
		authReq.code = code;
		try {
			req.setEntity(new StringEntity(new Gson().toJson(authReq)));
		} catch (UnsupportedEncodingException e) {
			throw new TentClientException("error when encoding api request", e);
		}

		try {
			HttpResponse res = getHttpClient().execute(req);
			InputStreamReader is = new InputStreamReader(res.getEntity().getContent());
			return new Gson().fromJson(is, AuthorizationResponse.class);
		} catch (IOException e) {
			throw new TentClientException("api error when creating authorization", e);
		}
	}

	public Profile getProfile(String profileUrl) {
		HttpGet req = new HttpGet(profileUrl);
		try {
			HttpResponse res = getHttpClient().execute(req);
			String resBody = responseToString(res);
			JSONObject profileJson = new JSONObject(resBody);
			return Profile.fromJsonObject(profileJson);
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Profile getProfile(Account account) throws TentClientException {
		String profileUri = Uri.parse(account.serverUrl).buildUpon()
				.appendPath("profile")
				.build().toString();
		
		HttpGet req = new HttpGet(profileUri);
		req.setHeader("Accept", TENT_MIME);
		OAuth2.sign(req, account.macId, account.macKey);

		try {
			HttpResponse res = getHttpClient().execute(req);
			String resBody = responseToString(res);
			JSONObject profileJson = new JSONObject(resBody);
			return Profile.fromJsonObject(profileJson);
		}
		catch (IOException e) {
			throw new TentClientException("Error making http request", e);
		} catch (JSONException e) {
			throw new TentClientException("Error parsing server's response", e);
		}
	}
	
	public Post post (Account account, Post post) throws TentClientException {
		String postsUri = Uri.parse(account.serverUrl).buildUpon()
				.appendPath("posts")
				.build().toString();
		
		HttpPost req = new HttpPost(postsUri);
		req.setHeader("Content-Type", TENT_MIME);
		req.setHeader("Accept", TENT_MIME);

		OAuth2.sign(req, account.macId, account.macKey);
		
		try {
			req.setEntity(new StringEntity(new Gson().toJson(post)));
		} catch (UnsupportedEncodingException e) {
			throw new TentClientException("Error encoding post json", e);
		}
		
		HttpResponse res;
		String resBody;
		try {
			res = getHttpClient().execute(req);
			resBody = responseToString(res);
		} catch (IOException e) {
			throw new TentClientException("Error making http request", e);
		}
		
		try {
			Post createdPost = new Gson().fromJson(resBody, Post.class);
			return createdPost;
		}
		catch (JsonSyntaxException e) {
			throw new TentClientException("Error parsing response from Tent server", e);
		}
	}
	
	public Post multipartPost (Account account, Post post, List<UploadAttachment> uploads) throws TentClientException {
		String postsUri = Uri.parse(account.serverUrl).buildUpon()
				.appendPath("posts")
				.build().toString();

		String jsonPost = new Gson().toJson(post);
		ContentBody postPart = new ByteArrayBody(jsonPost.getBytes(), TENT_MIME, "post.json");

		MultipartEntity requestEntity = new MultipartEntity();
		requestEntity.addPart("post", postPart);
		
		int photoCount = 0;
		for (UploadAttachment upload : uploads) {
			if (upload.mimeType.startsWith("image/")) {
				FileBody image = new FileBody(upload.file, upload.filename, upload.mimeType, null);
				requestEntity.addPart("photos[" + photoCount + "]", image);
				photoCount++;
			}
		}

		HttpPost req = new HttpPost(postsUri);
		req.setHeader("Accept", TENT_MIME);
		req.setEntity(requestEntity);

		OAuth2.sign(req, account.macId, account.macKey);
		
		HttpResponse res;
		String resBody;
		try {
			res = getHttpClient().execute(req);
			resBody = responseToString(res);
		}
		catch (IOException e) {
			throw new TentClientException("api error", e);
		}
		
		try {
			Post createdPost = new Gson().fromJson(resBody, Post.class);
			return createdPost;
		}
		catch (JsonSyntaxException e) {
			throw new TentClientException("Error parsing response from Tent server", e);
		}
	}
	
	public List<Post> getPosts(Account account) throws TentClientException {
		String postsUri = Uri.parse(account.serverUrl).buildUpon()
				.appendPath("posts")
				.build().toString();
		
		HttpGet req = new HttpGet(postsUri);
		OAuth2.sign(req, account.macId, account.macKey);
		
		try {
			HttpResponse res = getHttpClient().execute(req);
			String resBody = responseToString(res);
			
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			JsonArray jPosts = parser.parse(resBody).getAsJsonArray();
			
			List<Post> posts = new ArrayList<Post>();
			for (int i=0,c=jPosts.size(); i<c; i++) {
				Post p = gson.fromJson(jPosts.get(i), Post.class);
				posts.add(p);
			}
			return posts;
		} catch (IOException e) {
			throw new TentClientException("Api error getting posts", e);
		}
	}
	
	@SuppressWarnings("serial")
	public class TentClientException extends Exception {
		public TentClientException (String message, Throwable cause) {
			super(message, cause);
		}
	}

	private HttpClient getHttpClient() {
		return new DefaultHttpClient();
	}
	
	private String responseToString (HttpResponse response) throws IOException {
		InputStream is = response.getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	private class DiscoveryClient {
		List<String> urls;

		public DiscoveryClient() {
			urls = new ArrayList<String>();
		}

		public List<String> getProfileUrl(String entityUri) {
			HttpClient c = getHttpClient();
			HttpGet req = new HttpGet(entityUri);
			try {
				HttpResponse res = c.execute(req);
				fromHeaders(res);
			} catch (IOException e) {
				Log.e("discovery", "discovery fail for url: " + entityUri, e);
			}

			return urls;
		}

		private void fromHeaders(HttpResponse res) {
			for (Header header : res.getHeaders("Link")) {
				for (String link : header.getValue().split(",")) {
					Log.d("discovery", "link: " + link);
					if (!link.contains("rel=\"https://tent.io/rels/profile\"")) {
						continue;
					}
					int strStart = link.indexOf("<");
					int strEnd = link.indexOf(">");
					if (strEnd >= strStart) {
						Log.d("discovery",
								"  adding: "
										+ link.substring(strStart + 1, strEnd));
						urls.add(link.substring(strStart + 1, strEnd));
					}
				}
			}
		}
	}
}
