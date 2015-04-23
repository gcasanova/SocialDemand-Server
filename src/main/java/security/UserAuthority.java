package security;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;

import app.domain.entities.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@IdClass(UserAuthority.class)
public class UserAuthority implements GrantedAuthority {
	private static final long serialVersionUID = 3376884643901069882L;

	@Id
	@NotNull
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@Id
	@NotNull
	private String authority;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserAuthority))
			return false;

		UserAuthority ua = (UserAuthority) obj;
		return ua.getAuthority() == this.getAuthority() || ua.getAuthority().equals(this.getAuthority());
	}

	@Override
	public int hashCode() {
		return getAuthority() == null ? 0 : getAuthority().hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getAuthority();
	}
}
