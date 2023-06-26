package com.macalsandair.library.auth;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {

    USER("USER"),
    ADMINISTRATOR("ADMINISTRATOR");

    private final String role;

    Roles(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return name();
    }

    public String getRole() {
        return role;
    }
}
