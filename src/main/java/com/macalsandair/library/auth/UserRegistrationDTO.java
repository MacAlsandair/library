package com.macalsandair.library.auth;

import java.nio.CharBuffer;

public class UserRegistrationDTO {
    private String username;
    private CharBuffer password;
    
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

    public CharBuffer getPassword() {
        return CharBuffer.wrap(password);
    }
    
    public void setPassword(CharBuffer password) {
        if (this.password != null) {
            while (this.password.hasRemaining()) {
                this.password.put((char) 0);
            }
        }
        
        this.password = password;
    }
}

