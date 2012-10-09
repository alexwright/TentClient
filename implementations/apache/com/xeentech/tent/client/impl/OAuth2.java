package com.xeentech.tent.client.impl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpRequest;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

public class OAuth2 {
	public static void sign (HttpRequest request, String keyId, String keySecret) {
		Uri uri = Uri.parse(request.getRequestLine().getUri());
		int port = uri.getPort() > 0 ? uri.getPort() : (uri.getScheme().equals("https") ? 443 : 80); 
		
		Long ts = generateTs();
		String nonce = generateNonce();
		
		String path = uri.getPath();
		String qs = uri.getEncodedQuery();
		if (qs != null) {
			path += "?" + qs;
		}
		
		StringBuilder sb = new StringBuilder();
		sb
			.append(ts).append("\n")
			.append(nonce).append("\n")
			.append(request.getRequestLine().getMethod()).append("\n")
			.append(path).append("\n")
			.append(uri.getHost()).append("\n")
			.append(port).append("\n")
			.append("").append("\n"); // empty ext field;
		
		Log.d(OAuth2.class.toString(), "sign(): " + sb.toString());
		String sigBase = sb.toString();
		String mac = generateMac(sigBase, keySecret);
		
		String macFormat = "MAC id=\"%s\", ts=\"%s\", nonce=\"%s\", mac=\"%s\"";
		String authHeader = String.format(macFormat, keyId, ts.toString(), nonce, mac);
		Log.d(OAuth2.class.toString(), "sign(): auth: " + authHeader);
		Log.d(OAuth2.class.toString(), "sign(): secret: " + keySecret);
		request.addHeader("Authorization", authHeader);
	}
	
	protected static Long generateTs () {
		int startAt = 1262304000; // 2010-01-01T00:00:00Z
		return (System.currentTimeMillis() / 1000) - startAt;
	}
	
	protected static String generateNonce () {
		Long nonce = new Random().nextLong();
		return Long.toHexString(nonce);
	}
	
	protected static String generateMac (String base, String key) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(keySpec);
		
			return Base64.encodeToString(mac.doFinal(base.getBytes()), Base64.NO_WRAP);
		}
		catch (NoSuchAlgorithmException e) {
		}
		catch (InvalidKeyException e) {
		}
		return null;
	}
}
