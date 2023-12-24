package com.quintor.worqplace.security.data;

import com.quintor.worqplace.domain.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * This is a data model.
 * <p>
 * It is similar to a domain model, but is
 * intended for storage purposes. It does not
 * contain a lot of business logic.
 * <p>
 * It implements UserDetails in order to make it usable
 * as login/registration model for Spring.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
	@Id
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;
	private String password;
	private UserRoles role = UserRoles.USER;

	@OneToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	/**
	 * Constructor of the {@link User} class.
	 *
	 * @param username Email
	 * @param password Password
	 * @param employee {@link Employee}
	 */
	public User(Long id, String username, String password, Employee employee) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.employee = employee;
	}

	/**
	 * Constructor of the {@link User} class.
	 *
	 * @param username Email
	 * @param password Password
	 * @param role     Role {@link UserRoles}
	 * @param employee {@link Employee}
	 */
	public User(Long id, String username, String password, UserRoles role, Employee employee) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.employee = employee;
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.getRole().getRoleName()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
