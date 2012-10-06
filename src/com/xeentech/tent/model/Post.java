package com.xeentech.tent.model;

import java.util.List;
import java.util.Map;

public class Post {
	public String type;
	public String id;
	
	public List<String> licenses;
	public Map<String, Object> permissions;
	public long published_at;
	
	public Map<String, Object> content;
}
