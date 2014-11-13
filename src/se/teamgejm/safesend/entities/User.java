package se.teamgejm.safesend.entities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {
	
	private long id;
	private String username;
	private String publicKey;
	
	public User () {
		
	}
	
	public User (String username) {
		setUsername(username);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return "User: " + username + ", id: " + id;
	}
}
