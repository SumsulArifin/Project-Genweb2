package com.quintor.worqplace.security.presentation.dto;

/**
 * DTO for incoming registration request.
 */
public record Registration(String username, String password, String firstname, String lastname) {
}
