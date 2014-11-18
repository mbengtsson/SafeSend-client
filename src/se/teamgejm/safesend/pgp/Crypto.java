package se.teamgejm.safesend.pgp;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import android.util.Base64;

public class Crypto {
	
	public static PublicKey getRSAPublicKeyFromString(String apiKey) throws Exception{
		KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SC"); 
		byte[] publicKeyBytes = Base64.decode(apiKey.getBytes("UTF-8"), Base64.DEFAULT); 
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes); 
		return keyFactory.generatePublic(x509KeySpec);
	} 
	 
	public static PrivateKey getRSAPrivateKeyFromString(String key) throws Exception {
		byte [] clear = Base64.decode(key, Base64.DEFAULT); 
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear); 
		KeyFactory fact = KeyFactory.getInstance("RSA", "SC"); 
		PrivateKey priv = fact.generatePrivate(keySpec); 
		Arrays.fill(clear, (byte) 0); 
		return priv;
	}
	 
	public static String stripPublicKeyHeaders(String key) {
	        //strip the headers from the key string
	        StringBuilder strippedKey = new StringBuilder();
	        String lines[] = key.split("\n");
	        for(String line : lines) {
	            if(!line.contains("BEGIN PUBLIC KEY") && !line.contains("END PUBLIC KEY")) {
	                strippedKey.append(line.trim());
	            }
	        }
	        return strippedKey.toString().trim();
	}

}
