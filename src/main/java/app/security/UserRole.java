package app.security;

import app.domain.entities.User;

public enum UserRole {
	GUEST, USER, ADMIN;

	public UserAuthority asAuthorityFor(final User user) {
		final UserAuthority authority = new UserAuthority();
		authority.setAuthority("ROLE_" + toString());
		authority.setUser(user);
		return authority;
	}

	public static UserRole valueOf(final UserAuthority authority) {
		switch (authority.getAuthority()) {
			case "ROLE_GUEST":
				return GUEST;
			case "ROLE_USER":
				return USER;
			case "ROLE_ADMIN":
				return ADMIN;
		}
		throw new IllegalArgumentException("No role defined for authority: " + authority.getAuthority());
	}
}
