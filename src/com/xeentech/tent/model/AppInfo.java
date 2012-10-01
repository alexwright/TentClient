package com.xeentech.tent.model;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppInfo {
	public String name;
	public String description;
	public String url;
	public String icon;
	public List<String> redirect_uris;
	public Map<String, String> scopes;
	
	public String id;
	public String mac_key_id;
	public String mac_key;
	public String mac_algorithm;
	
	public JSONObject toJsonObject () throws JSONException {
		JSONObject jAppInfo = new JSONObject();
		jAppInfo.put("name", name);
		jAppInfo.put("description", description);
		jAppInfo.put("url", url);
		jAppInfo.put("redirect_uris", new JSONArray(redirect_uris));
		jAppInfo.put("scopes", new JSONObject(scopes));
		
		return jAppInfo;
	}
}
