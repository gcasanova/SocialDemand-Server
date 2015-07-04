package app.domain.entities;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.userdetails.UserDetails;

import app.domain.entities.location.Location;
import app.security.UserAuthority;
import app.security.UserRole;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
@JsonInclude(Include.NON_NULL)
public class User implements UserDetails {
	private static final long serialVersionUID = -947757741988164699L;

	private Integer id;
	private String name;
	private String email;
	private String phone;
	private String document; // official identification document
	private String password;
	private Integer municipalityId;

	private boolean accountLocked;
	private boolean accountExpired;
	private boolean accountEnabled;
	private boolean credentialsExpired;
	private Set<UserAuthority> authorities;

	private Location location; // transient property populated when appropriate

	@Id
	@JsonProperty
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@NotNull
	@JsonProperty
	@Size(min=3, max=25)
	@Pattern(regexp="^[A-zÀ-ÿ ]*$")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Email
	@NotNull
	@JsonProperty
	@Pattern(regexp="^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	public String getPhone() {
		return phone;
	}
	@JsonProperty
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@JsonIgnore
	public String getDocument() {
		return document;
	}
	@JsonProperty
	public void setDocument(String document) {
		this.document = document;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	@NotNull
	@JsonProperty
	@Column(name = "municipality_id")
	public Integer getMunicipalityId() {
		return municipalityId;
	}
	public void setMunicipalityId(Integer municipalityId) {
		this.municipalityId = municipalityId;
	}

	@Transient
	@JsonProperty
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	////// SECURITY RELATED //////

	@Override
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
	public Set<UserAuthority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<UserAuthority> authorities) {
		this.authorities = authorities;
	}

	@Transient
	// Use Roles as external API
	public Set<UserRole> getRoles() {
		Set<UserRole> roles = EnumSet.noneOf(UserRole.class);
		if (authorities != null) {
			for (UserAuthority authority : authorities) {
				roles.add(UserRole.valueOf(authority));
			}
		}
		return roles;
	}

	public void setRoles(Set<UserRole> roles) {
		for (UserRole role : roles) {
			grantRole(role);
		}
	}

	public void grantRole(UserRole role) {
		if (authorities == null) {
			authorities = new HashSet<UserAuthority>();
		}
		authorities.add(role.asAuthorityFor(this));
	}

	public void revokeRole(UserRole role) {
		if (authorities != null) {
			authorities.remove(role.asAuthorityFor(this));
		}
	}

	public boolean hasRole(UserRole role) {
		return authorities.contains(role.asAuthorityFor(this));
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}
	
	@Override
	@Transient
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}
	
	@Override
	@Transient
	@JsonIgnore
	public boolean isEnabled() {
		return !accountEnabled;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getUsername();
	}

	@Override
	@Transient
	@JsonIgnore
	public String getUsername() {
		return email;
	}
	@JsonProperty
	public void setUsername(String username) {
		this.email = username;
	}
}
