package com.quintor.worqplace.security.data;

import lombok.Getter;

/**
 * This is a data model.
 * <p>
 * It is similar to a domain model, but is
 * intended for storage purposes. It does not
 * contain a lot of business logic.
 */
@Getter
public class UserProfile {
	private String username;

	/**
	 * Constructor of the {@link UserProfile} class.
	 *
	 * @param username username
	 */
	public UserProfile(String username) {
		this.username = username;
	}
}
