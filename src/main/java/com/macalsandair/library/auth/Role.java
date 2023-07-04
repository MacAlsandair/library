package com.macalsandair.library.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.macalsandair.library.auth.Permission;

public enum Role implements GrantedAuthority {

	USER(Collections.emptySet()),
	
	ADMIN(Set.of(Permission.ADMIN_READ, 
			Permission.ADMIN_UPDATE, 
			Permission.ADMIN_DELETE, 
			Permission.ADMIN_CREATE,
			Permission.MANAGER_READ, 
			Permission.MANAGER_UPDATE, 
			Permission.MANAGER_DELETE, 
			Permission.MANAGER_CREATE)),
	
	MANAGER(Set.of(Permission.MANAGER_READ, 
			Permission.MANAGER_UPDATE, 
			Permission.MANAGER_DELETE,
			Permission.MANAGER_CREATE))

	;

	private final Set<Permission> permissions;

	public List<SimpleGrantedAuthority> getAuthorities() {
		var authorities = getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return authorities;
	}

	private Role(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

    @Override
    public String getAuthority() {
        return name();
    }

}
