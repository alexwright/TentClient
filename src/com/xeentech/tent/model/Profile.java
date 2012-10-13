package com.xeentech.tent.model;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Profile {
	@SerializedName("https://tent.io/types/info/core/v0.1.0")
	public Core core;
	
	@SerializedName("https://tent.io/types/info/basic/v0.1.0")
	public Basic basic;
	
	public class Core {
		public String entity;
		public List<String> licenses;
		public List<String> servers;
		public Map<String, Object> permissions;
	}
	
	public class Basic {
		public String name;
		public String avatar_url;
		public String birthdate;
		public String gender;
		public String bio;
		public Map<String, Object> permissions;
	}
	
	public Long localId;
}
