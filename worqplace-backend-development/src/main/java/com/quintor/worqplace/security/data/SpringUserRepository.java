package com.quintor.worqplace.security.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * This is a magic interface, which is converted
 * to a class during compilation.
 */
public interface SpringUserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
