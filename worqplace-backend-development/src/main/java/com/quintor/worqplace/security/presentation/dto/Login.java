package com.quintor.worqplace.security.presentation.dto;

/**
 * DTO for incoming login request.
 */
public record Login(String username, String password) {
}
