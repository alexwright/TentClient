package com.xeentech.tent.model;

import java.util.List;
import java.util.Map;

public class Post {
	public String type;
	public String id;
	public String entity;
	
	public List<String> licenses;
	public Map<String, Object> permissions;
	public long published_at;
	
	public Map<String, Object> content;
	public List<Attachment> attachments;
	
	public static final String TENT_POST_TYPE_STATUS = "https://tent.io/types/post/status/v0.1.0";
	public static final String TENT_POST_TYPE_ESSAY = "https://tent.io/types/post/essay/v0.1.0";
	public static final String TENT_POST_TYPE_PHOTO = "https://tent.io/types/post/photo/v0.1.0";
	public static final String TENT_POST_TYPE_ALBUM = "https://tent.io/types/post/album/v0.1.0";
}
