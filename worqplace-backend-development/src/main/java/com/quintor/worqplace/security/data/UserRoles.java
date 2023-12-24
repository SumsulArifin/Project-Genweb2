package com.quintor.worqplace.security.data;

import lombok.Getter;

/**
 * Roles for users that are available in this application.
 * <p>
 * Consists of:
 * * USER
 * * ADMIN
 * </p>
 */
@Getter
public enum UserRoles {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN");

	private final String roleName;

	/**
	 * Constructor of the enum {@link UserRoles}.
	 *
	 * @param roleName RoleName
	 */
	UserRoles(String roleName) {
		this.roleName = roleName;
	}
}
