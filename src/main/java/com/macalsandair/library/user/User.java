package com.macalsandair.library.user;

import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.macalsandair.library.auth.Role;
import com.macalsandair.library.book.Book;
import com.macalsandair.library.comment.CommentToBook;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    
    @JsonIgnore
    private String password;
    @JsonIgnore
    private boolean enabled;
	
	@ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<UserFavoriteBook> favoriteBooks = new HashSet<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author", orphanRemoval = true)
    @JsonIgnore
    private Set<CommentToBook> comments;

    public void addFavoriteBook(Book book) {
        UserFavoriteBook userFavoriteBooks = new UserFavoriteBook(this, book);
        favoriteBooks.add(userFavoriteBooks);
    }

    public void removeFavoriteBook(Book book) {
        UserFavoriteBook toRemove = null;
        for (UserFavoriteBook userFavoriteBooks : this.favoriteBooks) {
            if (userFavoriteBooks.getBook().equals(book)) {
                toRemove = userFavoriteBooks;
                break;
            }
        }
        if (toRemove != null){
            book.getUserFavoriteBooks().remove(toRemove);
            favoriteBooks.remove(toRemove);
            toRemove.setBook(null);
            toRemove.setUser(null);
        }
    }
    
    public Set<UserFavoriteBook> getFavoriteBooks() {
		return favoriteBooks;
	}

	public void setFavoriteBooks(Set<UserFavoriteBook> favoriteBooks) {
		this.favoriteBooks = favoriteBooks;
	}

	public boolean isFavoriteBook(Book book) {
	    for (UserFavoriteBook favoriteBook : favoriteBooks) {
	        if (favoriteBook.getBook().equals(book)) {
	            return true;
	        }
	    }
	    return false;
	}


    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	

	public User() {
		super();
	}

	public User(String username, String password, boolean enabled, List<Role> roles) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.roles = roles;
	}
	



	@Override
	public int hashCode() {
		return Objects.hash(enabled, id, password, roles, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return enabled == other.enabled && Objects.equals(id, other.id) && Objects.equals(password, other.password)
				&& Objects.equals(roles, other.roles) && Objects.equals(username, other.username);
	}





    // `UserDetails` methods

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return this.roles.stream()
	            .flatMap(role -> role.getAuthorities().stream())
	            .collect(Collectors.toList());
	}

    

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}
}