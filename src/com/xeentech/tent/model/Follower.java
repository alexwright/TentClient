package com.xeentech.tent.model;

import java.util.List;
import java.util.Map;

public class Follower {
	public String entity;
	public String id;
	public List<String> licenses;
	public Map<String, Object> permissions;
	public long created_at;
	public long updated_at;
	public Map<String, Object> profile;
	public List<String> types;
}
