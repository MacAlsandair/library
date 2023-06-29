package com.macalsandair.library.user;

import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.macalsandair.library.auth.Roles;
import com.macalsandair.library.book.Book;

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
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private boolean enabled;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserFavoriteBook> favoriteBooks = new HashSet<>();

    public void addFavoriteBook(Book book) {
        UserFavoriteBook userFavoriteBooks = new UserFavoriteBook(this, book);
        favoriteBooks.add(userFavoriteBooks);
        //book.getUserFavoriteBooks().add(userFavoriteBooks);  // assuming getUserFavoriteBooks() returns a Set<UserFavoriteBooks>
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
            book.getUserFavoriteBooks().remove(toRemove); // assuming getUserFavoriteBooks() returns a Set<UserFavoriteBooks>
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

	public List<Roles> getRoles() {
		return roles;
	}

	public void setRoles(List<Roles> roles) {
		this.roles = roles;
	}
	
	

	public User() {
		super();
	}

	public User(String username, String password, boolean enabled, List<Roles> roles) {
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




	@ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Roles> roles;

    // `UserDetails` methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }
    

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
}