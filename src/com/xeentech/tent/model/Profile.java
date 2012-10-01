package com.xeentech.tent.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profile {
	public String entityName;
	public String name;
	public String avatar_url;
	public String birthdate;
	public String location;
	public String gender;
	public String bio;
	public List<String> servers;
	
	public static final String TENT_PROFILE_CORE_URI = "https://tent.io/types/info/core/v0.1.0";
	public static final String TENT_PROFILE_BASIC_URI = "https://tent.io/types/info/basic/v0.1.0";
	
	public String toString () {
		if (name != null && entityName != null) {
			return name + " (" + entityName + ")";
		}
		else if (entityName != null) {
			return entityName;
		}
		else {
			return super.toString();
		}
	}
	
	public static Profile fromJsonObject (JSONObject jsonObject) throws JSONException {
		Profile p = new Profile();
		
		if (jsonObject.has(TENT_PROFILE_BASIC_URI)) {
			JSONObject jBasic = jsonObject.getJSONObject(TENT_PROFILE_BASIC_URI);
			if (jBasic.has("name"))
				p.name = jBasic.getString("name");
			if (jBasic.has("avatar_url"))
				p.avatar_url = jBasic.getString("avatar_url");
			if (jBasic.has("birthdate"))
				p.birthdate = jBasic.getString("birthdate");
			if (jBasic.has("location"))
				p.location = jBasic.getString("location");
			if (jBasic.has("gender"))
				p.gender = jBasic.getString("gender");
			if (jBasic.has("bio"))
				p.bio = jBasic.getString("bio");
		}
		
		if (jsonObject.has(TENT_PROFILE_CORE_URI)) {
			JSONObject jCore = jsonObject.getJSONObject(TENT_PROFILE_CORE_URI);
			if (jCore.has("entity"))
				p.entityName = jCore.getString("entity");
			if (jCore.has("servers")) {
				p.servers = new ArrayList<String>();
				JSONArray jServers = jCore.getJSONArray("servers");
				for (int i=0, c=jServers.length(); i<c; i++) {
					p.servers.add(jServers.getString(i));
				}
				p.entityName = jCore.getString("entity");
			}
		}
		
		return p;
	}
}
